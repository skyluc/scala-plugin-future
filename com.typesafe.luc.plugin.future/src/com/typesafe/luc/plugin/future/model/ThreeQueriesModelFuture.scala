package com.typesafe.luc.plugin.future.model

import scala.collection._
import org.eclipse.core.runtime.jobs.Job
import com.typesafe.luc.plugin.future.client.Client
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Status
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ThreeQueriesModelFuture {
  val FetchingSubs = List(Sub("...")(Nil))
  val FetchingLeaves = List(Leaf("..."))
}

class ThreeQueriesModelFuture extends Model {

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
          fetch()
          fetching = true
        }
      }
    }
    rootsCache
  }

  private def refresh() {
    refreshList foreach { r => r() }
  }

  private def fetch() {
    for {
      roots <- fetchRoots()
      subs <- fetchSubs(roots)
    } fetchLeaves(subs)

  }

  private def fetchRoots() = Future {
    val roots = Client.getRoots.map(Root(_)(FetchingSubs))
    rootsCache = roots
    refresh()
    roots
  }

  private def fetchSubs(roots: List[Root]) = Future {
    val allSubs = roots.flatMap { root =>
      val subs = Client.getSubs(root.id).map(Sub(_)(FetchingLeaves))
      root.subs = subs
      subs
    }
    refresh()
    allSubs
  }

  private def fetchLeaves(allSubs: List[Sub]) = Future {
    allSubs.foreach { sub =>
      val leaves = Client.getLeaves(sub.id).map(Leaf(_))
      sub.leaves = leaves
    }

    refreshList.synchronized {
      fetched = true
    }

    refresh
  }

}