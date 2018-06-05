package com.bitcoinpaygate.bitcoin4s

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials

import scala.concurrent.Future

abstract class HttpClient(val user: String, val password: String, val host: String, val port: Int) {
  val connectionUri = s"http://$host:$port/"
  val authorization = headers.Authorization(BasicHttpCredentials(user, password))

  def httpRequest(method: String): HttpRequest = {
    HttpRequest(
      uri = connectionUri,
      method = HttpMethods.POST,
      entity = HttpEntity(
        s"""
           |{ "method": "$method" }
        """.stripMargin),
      headers = List(authorization))
  }

  def httpRequestWithParams(method: String, params: Vector[Any]): HttpRequest = {
    val formattedParams = HttpParamsConverter.rpcParamsToJson(params)

    HttpRequest(
      uri = connectionUri,
      method = HttpMethods.POST,
      entity = HttpEntity(
        s"""
           |{
           | "method": "$method",
           | "params": [${formattedParams.mkString(",")}]
           |}
        """.stripMargin),
      headers = List(authorization))
  }

  def performRequest(request: HttpRequest): Future[HttpResponse]
}

