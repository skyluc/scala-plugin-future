package com.typesafe.luc.plugin.future.model

import scala.collection._
import org.eclipse.core.runtime.jobs.Job
import com.typesafe.luc.plugin.future.client.Client
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Status
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FullParallelModel {
  val FetchingSubs = List(Sub("...")(Nil))
  val FetchingLeaves = List(Leaf("..."))
}

class FullParallelModel extends Model {

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

  private def fetch(): Future[List[Leaf]] = {
    Future {
      val roots = Client.getRoots.map(Root(_)(FetchingSubs))
      rootsCache = roots
      refresh()
      fetchSubs(roots)
      roots
    }.flatMap(fetchSubs(_))
  }

  private def fetchSubs(roots: List[Root]): Future[List[Leaf]] = {
    Future.traverse(roots) { root =>
      Future {
        val subs = Client.getSubs(root.id).map(Sub(_)(FetchingLeaves))
        root.subs = subs
        refresh()
        subs
      }.flatMap(fetchLeaves(_))
    }.map(_.flatten)
  }

  private def fetchLeaves(subs: List[Sub]): Future[List[Leaf]] = {
    Future.traverse(subs) { sub =>
      Future {
        val leaves = Client.getLeaves(sub.id).map(Leaf(_))
        sub.leaves = leaves
        refresh()
        leaves
      }
    }.map(_.flatten)

  }

}