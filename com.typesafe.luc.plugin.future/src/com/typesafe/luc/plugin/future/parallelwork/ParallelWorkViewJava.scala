package com.typesafe.luc.plugin.future.parallelwork

class ParallelWorkViewJava extends ParallelWorkViewBase {
  
  override protected val model = new ParallelWorkModelJava(this)

}