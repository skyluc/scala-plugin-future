package com.typesafe.luc.plugin.future.parallelwork

import scala.concurrent.Promise
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

trait ParallelWorkModel {

  def addNew()

  def content: Array[Object]

}

object Client {

  @volatile private var promises = List[Promise[String]]()

  def fetchValue(id: Int): Future[String] = {
    val promise = Promise[String]
    synchronized {
      promises = promise :: promises
    }
    promise.future
  }

  def fetchValueWait(id: Int): String = {
    Await.result(fetchValue(id), 1 day)
  }

  def processed {
    synchronized {
      promises.foreach {
        _.success(Random.nextString(9))
      }
      promises = List()
    }
  }
}

