package com.typesafe.luc.plugin.future

import com.typesafe.luc.plugin.future.model.FullParallelModel

class FullParallelFutureView extends BaseFutureView {

  override val model = new FullParallelModel()
  
}