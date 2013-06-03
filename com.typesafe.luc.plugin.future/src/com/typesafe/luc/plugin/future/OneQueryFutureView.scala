package com.typesafe.luc.plugin.future

import com.typesafe.luc.plugin.future.model.OneQueryModel

class OneQueryFutureView extends BaseFutureView {
  
  override protected[future] val model = new OneQueryModel

}