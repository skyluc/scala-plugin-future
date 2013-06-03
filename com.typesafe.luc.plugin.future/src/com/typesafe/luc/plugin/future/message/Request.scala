package com.typesafe.luc.plugin.future.message

abstract class Request(messageId: String, payload: String) {

  def encode = s"$messageId:$payload\n"

}

case class RootsRequest extends Request(RootsRequest.Keyword, "")

object RootsRequest {
  private val Keyword = "Roots"
  private val Regex = s"${Keyword}:".r
  
  def unapply(content: String): Boolean =
    content match {
    case Regex() =>
      true
    case _ =>
      false
  }
}

case class SubsRequest(root: String) extends Request(SubsRequest.Keyword, root)

object SubsRequest {
  private val Keyword = "Subs"
  private val Regex= s"$Keyword:(.*)".r
  
  def unapply(content: String) : Option[String]= {
    content match {
      case Regex(root) =>
        Some(root)
      case _ =>
        None
    }
  }
}

case class LeavesRequest(sub: String) extends Request(LeavesRequest.Keyword, sub)

object LeavesRequest {
  private val Keyword = "Leaves"
  private val Regex= s"$Keyword:(.*)".r
  
  def unapply(content: String) : Option[String]= {
    content match {
      case Regex(sub) =>
        Some(sub)
      case _ =>
        None
    }
  }
}