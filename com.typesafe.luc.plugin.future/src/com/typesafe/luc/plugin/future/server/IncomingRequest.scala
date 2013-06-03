package com.typesafe.luc.plugin.future.server

import java.net.Socket
import java.io.StringReader
import java.io.InputStreamReader
import java.io.BufferedReader
import com.typesafe.luc.plugin.future.message.SubsRequest
import com.typesafe.luc.plugin.future.model.Model
import com.typesafe.luc.plugin.future.message.RootsRequest
import com.typesafe.luc.plugin.future.message.LeavesRequest
import com.typesafe.luc.plugin.future.message.RootsResponse
import com.typesafe.luc.plugin.future.message.SubsResponse
import com.typesafe.luc.plugin.future.message.LeavesResponse
import java.io.OutputStreamWriter
import scala.util.Random

object IncomingRequest {
  
  val Noop = () => {}

  def apply(socket: Socket, model: Model) {
    new IncomingRequest(socket, model).process()
  }

}

class IncomingRequest private (socket: Socket, model: Model) {
  
  import IncomingRequest._

  def process() {
    try {
      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
      val content = reader.readLine()
      println(content)
      
      // simulate slow operation
      Thread.sleep(Random.nextInt(1000))
      
      val response= content match {
        case RootsRequest() =>
          processRootsRequest()
        case SubsRequest(root) =>
          processSubsRequest(root)
        case LeavesRequest(sub) =>
          processLeavesRequest(sub)
      }
      
      val writer = new OutputStreamWriter(socket.getOutputStream())
      writer.write(response.encode)
      writer.flush()
      
    } finally {
      socket.close()
    }
  }
  
  private def processRootsRequest() =
    RootsResponse(model.roots(Noop).map(_.id))
  
  private def processSubsRequest(root: String) =
	SubsResponse(model.roots(Noop).find(_.id == root).map(_.subs.map(_.id)).getOrElse(Nil))
  
  private def processLeavesRequest(sub: String) =
	LeavesResponse(model.roots(Noop).flatMap(_.subs).find(_.id == sub).map(_.leaves.map(_.id)).getOrElse(Nil))

}