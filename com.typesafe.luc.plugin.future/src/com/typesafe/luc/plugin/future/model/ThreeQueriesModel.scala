package com.typesafe.luc.plugin.future.model

import scala.collection._
import org.eclipse.core.runtime.jobs.Job
import com.typesafe.luc.plugin.future.client.Client
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Status

object ThreeQueriesModel {
  val FetchingSubs = List(Sub("...")(Nil))
  val FetchingLeaves = List(Leaf("..."))
}

class ThreeQueriesModel extends Model {

  import ThreeQueriesModel._

  private var rootsCache: List[Root] = List(Root("...")(Nil))
  private var fetched = false
  private var fetching = false

  private var refreshList = List[() => Unit]()

  override def roots(refresh: () => Unit): List[Root] = {
    if (!fetched) {
      refreshList synchronized {
        refreshList = refresh :: refreshList
        if (!fetching) {
          new FetchJob1().schedule
          fetching = true
        }
      }
    }
    rootsCache
  }

  class FetchJob1 extends Job("ThreeQueries 1 job") {

    def run(monitor: IProgressMonitor): IStatus = {
      // roots
      val roots = Client.getRoots.map(Root(_)(FetchingSubs))

      new FetchJob2(roots).schedule
      
      rootsCache = roots

      refreshList.foreach { r => r() }

      Status.OK_STATUS
    }
  }

  class FetchJob2(roots: List[Root]) extends Job("ThreeQueries 2 job") {

    def run(monitor: IProgressMonitor): IStatus = {
      // subs
      val allSubs = roots.flatMap { root =>
        val subs = Client.getSubs(root.id).map(Sub(_)(FetchingLeaves))
        root.subs = subs
        subs
      }

      new FetchJob3(allSubs).schedule

      refreshList.foreach { r => r() }

      Status.OK_STATUS
    }
  }

  class FetchJob3(allSubs: List[Sub]) extends Job("ThreeQueries 3 job") {

    def run(monitor: IProgressMonitor): IStatus = {

      // leaves
      allSubs.foreach { sub =>
        val leaves = Client.getLeaves(sub.id).map(Leaf(_))
        sub.leaves = leaves
      }

      refreshList.synchronized {
        fetched = true
      }

      refreshList.foreach { r => r() }

      Status.OK_STATUS
    }
  }
}