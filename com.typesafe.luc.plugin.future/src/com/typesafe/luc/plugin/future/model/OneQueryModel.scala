package com.typesafe.luc.plugin.future.model

import scala.collection.mutable

import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.core.runtime.jobs.Job

import com.typesafe.luc.plugin.future.client.Client

class OneQueryModel extends Model {

  private var rootsCache: List[Root] = _

  private val refreshList = mutable.ListBuffer[() => Unit]()

  override def roots(refresh: () => Unit): List[Root] = {
    if (rootsCache != null) {
      rootsCache
    } else {
      refreshList.synchronized {
        if (rootsCache != null) {
          rootsCache
        } else {
          refreshList.append(refresh)
          new FetchJob().schedule()
          List(Root("...")(Nil))
        }
      }
    }
  }

  class FetchJob extends Job("OneQuery job") {

    def run(monitor: IProgressMonitor): IStatus = {
      // roots
      val roots = Client.getRoots.map(Root(_)(Nil))
      
      // subs
      val allSubs = roots.flatMap { root =>
        val subs = Client.getSubs(root.id).map(Sub(_)(Nil))
        root.subs = subs
        subs
      }
      
      // leaves
      allSubs.foreach { sub =>
         val leaves = Client.getLeaves(sub.id).map(Leaf(_))
         sub.leaves = leaves
      }
      
      refreshList.synchronized {
        rootsCache = roots
      }
      
      refreshList foreach {r => r()}
      refreshList.clear()

      Status.OK_STATUS
    }

  }
}
