package com.typesafe.luc.plugin.future.model

object StaticModel extends Model {

  override def roots(notUsed: () => Unit) = roots

  override val roots =
    List(
      Root("A")(List(
        Sub("A1")(List(
          Leaf("A1a"))))),
      Root("B")(Nil),
      Root("C")(List(
        Sub("C1")(Nil))),
      Root("D")(List(
        Sub("D1")(List(
          Leaf("D1a"),
          Leaf("D1b"),
          Leaf("D1c"))),
        Sub("D2")(List(
          Leaf("D2a"),
          Leaf("D2b"))))))

}

