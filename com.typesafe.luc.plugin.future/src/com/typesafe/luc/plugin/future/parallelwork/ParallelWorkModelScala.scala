package com.typesafe.luc.plugin.future.parallelwork

import scala.concurrent.ExecutionContext.Implicits.global

class ParallelWorkModelScala(view: ParallelWorkViewBase) extends ParallelWorkModel {

  @volatile private var internal = List[Element]()

  def addNew() {

    val newElement = synchronized {
      val element = Element(internal.size, None)
      internal = element :: internal
      element
    }

    view.refresh()

    val future = Client.fetchValue(newElement.id)
    future.onSuccess {
      case value: String =>
        update(newElement, value)
    }

  }
  
  def content: Array[Object] =
    internal.toArray

  def update(element: Element, value: String) {
    synchronized {
      val updatedElement = element.copy(value = Some(value))
      internal = internal.map {
        e =>
          if (e.id == updatedElement.id)
            updatedElement
          else
            e
      }
    }
    view.refresh()

  }

  private case class Element(id: Int, value: Option[String]) {
    override def toString() =
      f"${id}%03d : ${value.getOrElse("...")}"
  }

}