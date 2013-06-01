package com.typesafe.luc.plugin.future.model

trait Model extends ModelJava {

  def roots(refresh: () => Unit): List[Root]

  def roots(): List[Root] = roots(() => ???)

  def rootsArray(refresh: Runnable): Array[Root] = roots(refresh.run).toArray

}

trait Element {
  val id: String
}

case class Root(id: String)(var subs: List[Sub]) extends Element {

  def this(id: String, s: Array[Sub]) {
    this(id)(s.toList)
  }

  def subs(s: Array[Sub]) {
    subs = s.toList
  }
}

case class Sub(id: String)(var leaves: List[Leaf]) extends Element {

  def this(id: String, l: Array[Leaf]) {
    this(id)(l.toList)
  }
  
  def leaves(l: Array[Leaf]) {
    leaves = l.toList
  }
}

case class Leaf(id: String) extends Element