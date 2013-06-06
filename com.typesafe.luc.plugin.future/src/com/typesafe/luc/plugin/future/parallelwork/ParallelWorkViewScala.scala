package com.typesafe.luc.plugin.future.parallelwork

class ParallelWorkViewScala extends ParallelWorkViewBase {
  
  override protected val model = new ParallelWorkModelScala(this)

}