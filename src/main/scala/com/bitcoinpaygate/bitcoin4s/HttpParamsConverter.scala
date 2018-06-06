package com.bitcoinpaygate.bitcoin4s

import spray.json._
import spray.json.DefaultJsonProtocol._

private[bitcoin4s] object HttpParamsConverter {
  def rpcParamsToJson(params: Vector[Any]): Vector[String] = params.map {
    case param: Int                      => param.toString
    case param: BigDecimal               => param.toString
    case param: String                   => "\"" + param + "\""
    case param: Boolean                  => param.toString
    case param: ClientObjects.Recipients => param.value.toJson.compactPrint
  }
}
