package com.typesafe.luc.plugin.future.client

import com.typesafe.luc.plugin.future.message.Request
import com.typesafe.luc.plugin.future.message.Response
import com.typesafe.luc.plugin.future.message.RootsRequest
import com.typesafe.luc.plugin.future.message.RootsResponse
import com.typesafe.luc.plugin.future.message.SubsResponse
import com.typesafe.luc.plugin.future.message.SubsRequest
import com.typesafe.luc.plugin.future.message.LeavesRequest
import com.typesafe.luc.plugin.future.message.LeavesResponse
import java.net.Socket
import com.typesafe.luc.plugin.future.Configuration
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader

object Client {

  def getRoots(): List[String] =
    performRequest(RootsRequest()) match {
      case RootsResponse(roots) =>
        roots
    }
  
  def getSubs(root: String): List[String] =
    performRequest(SubsRequest(root)) match {
      case SubsResponse(subs) =>
        subs
    }
  
  def getLeaves(sub: String): List[String] =
    performRequest(LeavesRequest(sub)) match {
      case LeavesResponse(leaves) =>
        leaves
    }
  
  private def performRequest(request: Request): Response = {
    val socket = new Socket("localhost", Configuration.Port)
    try {
      val writer = new OutputStreamWriter(socket.getOutputStream())
      writer.write(request.encode)
      writer.flush()
      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))

      val content = reader.readLine()

      val Response(response) = content
      response
    } finally {
      socket.close
    }
  }

}