package com.typesafe.luc.plugin.future.server

import java.net.ServerSocket
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.luc.plugin.future.model.StaticModel
import com.typesafe.luc.plugin.future.Configuration

object Server {

  def main(args: Array[String]) {
    val server = Server(Configuration.Port)
    server.start
    readLine
    server.stop
  }

  def apply(port: Int): Server = {
    new Server(new ServerSocket(port))
  }

}

class Server(socket: ServerSocket) {

  def start {
    Future {
      waitForConnection
    }
    println(s"Server started on ${Configuration.Port}")
  }

  def stop {
    socket.close()
    println("Server stopped")
  }

  private def waitForConnection {
    val incoming = socket.accept()
    Future {
      IncomingRequest(incoming, StaticModel)
    }
    Future {
      waitForConnection
    }
  }

}