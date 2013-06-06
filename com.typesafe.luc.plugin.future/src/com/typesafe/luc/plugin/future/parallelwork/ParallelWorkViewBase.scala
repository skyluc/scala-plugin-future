package com.typesafe.luc.plugin.future.parallelwork

import org.eclipse.ui.part.ViewPart
import org.eclipse.swt.widgets.Composite
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.ui.part.DrillDownAdapter
import org.eclipse.swt.SWT
import org.eclipse.jface.viewers.ViewerSorter
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.LabelProvider
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random
import org.eclipse.swt.widgets.Label
import java.lang.management.ManagementFactory
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.events.SelectionEvent

abstract class ParallelWorkViewBase extends ViewPart {

  protected val model: ParallelWorkModel
  
  private var viewer: TreeViewer = _
  private var threadLabel: Label = _

  def createPartControl(parent: Composite) {
    val container = new Composite(parent, SWT.NONE)
    container.setLayout(new GridLayout(3, false))
    threadLabel = new Label(container, SWT.NONE)
    threadLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false))
    val addNewButton = new Button(container, SWT.PUSH)
    addNewButton.setText("Add new")
    addSelectionListener(addNewButton, () => model.addNew)
    val processButton = new Button(container, SWT.PUSH)
    processButton.setText("Process")
    addSelectionListener(processButton, () => Client.processed)
    updateThreadLabel()
    viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL)
    val gridData = new GridData(GridData.FILL_BOTH)
    gridData.horizontalSpan = 3
    viewer.getTree().setLayoutData(gridData)
    new DrillDownAdapter(viewer)
    viewer.setContentProvider(new ViewContentProvider())
    viewer.setLabelProvider(new ViewLabelProvider())
    viewer.setSorter(new ViewerSorter())
    viewer.setInput(getViewSite())

    viewer.expandAll()

    parent.pack

  }

  private def addSelectionListener(widget: Button, action: () => Unit) {
    widget.addSelectionListener(new SelectionListener {
      def widgetDefaultSelected(event: SelectionEvent) {
        // nothing to do
      }
      def widgetSelected(event: SelectionEvent) {
        action()
      }
    })
  }

  def setFocus(): Unit = {
    viewer.getControl.setFocus()
  }

  def refresh() {
    viewer.getControl().getDisplay().asyncExec(
      new Runnable {
        def run {
          viewer.refresh()
          updateThreadLabel
        }
      })
  }

  def updateThreadLabel() {
    val threadCount = ManagementFactory.getThreadMXBean().getThreadCount()
    threadLabel.setText(s"number of running threads: $threadCount")
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

    def getChildren(parentElement: Any): Array[Object] = null

    def getElements(inputElement: Any): Array[Object] = {
      model.content
    }

    def getParent(element: Any): Object = null

    def hasChildren(element: Any): Boolean = false
  }

  class ViewLabelProvider extends LabelProvider {

    override def getText(element: Object) =
      element.toString
  }



}

