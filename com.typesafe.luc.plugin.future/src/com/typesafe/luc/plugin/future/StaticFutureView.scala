package com.typesafe.luc.plugin.future

import com.typesafe.luc.plugin.future.model.StaticModel

class StaticFutureView extends BaseFutureView {
  
  override protected[future] val model = StaticModel

}