package com.typesafe.luc.plugin.future.model

trait Model {

  def roots(refresh: () => Unit): List[Root]
  
  def roots(): List[Root]= roots(() => ???)
  
}

trait Element {
  val id: String
}

case class Root(id: String)(var subs: List[Sub]) extends Element

case class Sub(id: String)(var leaves: List[Leaf]) extends Element

case class Leaf(id: String) extends Element