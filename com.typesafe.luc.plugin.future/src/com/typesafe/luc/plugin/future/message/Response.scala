package com.typesafe.luc.plugin.future.message

abstract class Response(messageId: String, payload: List[String]) {

  def encode: String = s"$messageId:${payload.mkString(",")}\n"

}

object Response {
  def unapply(content: String): Option[Response] = {
    content match {
      case RootsResponse.Regex(payload) =>
        Some(RootsResponse(parseList(payload)))
      case SubsResponse.Regex(payload) =>
        Some(SubsResponse(parseList(payload)))
      case LeavesResponse.Regex(payload) =>
        Some(LeavesResponse(parseList(payload)))
      case _ =>
        None
    }
  }

  def parseList(payload: String): List[String] = {
    if (payload.isEmpty()) {
      Nil
    } else {
      payload.split(",").toList
    }
  }

}

case class RootsResponse(roots: List[String]) extends Response(RootsResponse.Keyword, roots)

object RootsResponse {
  private val Keyword = "Roots"
  private[message] val Regex = s"$Keyword:(.*)".r
}

case class SubsResponse(subs: List[String]) extends Response(SubsResponse.Keyword, subs)

object SubsResponse {
  private val Keyword = "Subs"
  private[message] val Regex = s"$Keyword:(.*)".r

}

case class LeavesResponse(leaves: List[String]) extends Response(LeavesResponse.Keyword, leaves)

object LeavesResponse {
  private val Keyword = "Leaves"
  private[message] val Regex = s"$Keyword:(.*)".r
}