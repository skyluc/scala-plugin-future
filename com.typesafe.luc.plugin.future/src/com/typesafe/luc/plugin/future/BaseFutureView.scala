package com.typesafe.luc.plugin.future

import com.typesafe.luc.plugin.future.model.StaticModel
import org.eclipse.ui.part.ViewPart
import org.eclipse.swt.widgets.Composite
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.ui.part.DrillDownAdapter
import org.eclipse.swt.SWT
import org.eclipse.jface.viewers.ViewerSorter
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.LabelProvider
import com.typesafe.luc.plugin.future.model.Leaf
import com.typesafe.luc.plugin.future.model.Root
import com.typesafe.luc.plugin.future.model.Sub
import com.typesafe.luc.plugin.future.model.Element
import com.typesafe.luc.plugin.future.model.ModelJava

abstract class BaseFutureView extends ViewPart {

  protected[future] val model: ModelJava

  private var viewer: TreeViewer = _

  def createPartControl(parent: Composite) {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL)
    new DrillDownAdapter(viewer)
    viewer.setContentProvider(new ViewContentProvider())
    viewer.setLabelProvider(new ViewLabelProvider())
    viewer.setSorter(new ViewerSorter())
    viewer.setInput(getViewSite())

    viewer.expandAll()

  }

  def setFocus(): Unit = {
    viewer.getControl.setFocus()
  }

  class ViewContentProvider extends ITreeContentProvider {
    // Members declared in org.eclipse.jface.viewers.IContentProvider

    def dispose() {
      // nothing to do
    }

    def inputChanged(x$1: org.eclipse.jface.viewers.Viewer, x$2: Any, x$3: Any) {
      // nothing to do
    }

    // Members declared in org.eclipse.jface.viewers.ITreeContentProvider

    def getChildren(parentElement: Any): Array[Object] =
      parentElement match {
        case r: Root =>
          r.subs.toArray
        case s: Sub =>
          s.leaves.toArray
        case _ => null
      }

    def getElements(inputElement: Any): Array[Object] = {
      model.rootsArray {
        new Runnable {
          def run() {
            viewer.getControl().getDisplay().asyncExec(new Runnable {
              def run() {
                viewer.refresh()
                viewer.expandAll()
              }
            })
          }
        }
      }.asInstanceOf[Array[Object]]
    }

    def getParent(element: Any): Object = null

    def hasChildren(element: Any): Boolean =
      element match {
        case r: Root =>
          r.subs.nonEmpty
        case s: Sub =>
          s.leaves.nonEmpty
        case _ => false
      }
  }

  class ViewLabelProvider extends LabelProvider {

    override def getText(element: Object) =
      element match {
        case e: Element =>
          e.id
        case _ =>
          null
      }

  }
}