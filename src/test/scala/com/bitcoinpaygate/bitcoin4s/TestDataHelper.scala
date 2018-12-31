package com.bitcoinpaygate.bitcoin4s

import spray.json._

trait TestDataHelper {

  protected def extractMethod(body: String): (String, Vector[String]) = {
    val entityJson = body.parseJson.asJsObject
    val method = entityJson.fields("method") match {
      case JsString(m) => m.toString
      case other       => deserializationError(s"expected method as String but got: $other")
    }

    val params = entityJson.fields.get("params").map {
      case JsArray(values) =>
        values.map {
          case JsString(s)  => s
          case JsNumber(n)  => n.toString
          case JsBoolean(b) => b.toString
          case JsObject(a)  => a.toString
          case JsArray(o)   => o.toString()
          case other        => deserializationError(s"expected JsArray to be String but got: $other")
        }
      case other => deserializationError(s"expected params as JsArray but got: $other")
    }

    (method, params.getOrElse(Vector.empty[String]))
  }

  protected def loadJsonResponseFromTestData(arg: (String, Vector[String])): String =
    arg match {
      case (method, params) =>
        val json = method match {
          case _ if params.contains("parseError")                 => TestData.parseErrorResponse
          case "getwalletinfo"                                    => TestData.walletInfoResponse
          case "getnetworkinfo"                                   => TestData.networkInfoResponse
          case "getmininginfo"                                    => TestData.miningInfoResponse
          case "getmempoolinfo"                                   => TestData.memPoolInfoResponse
          case "getblockchaininfo"                                => TestData.blockchainInfoResponse
          case "listunspent"                                      => TestData.listUnspentResponse
          case "listaccounts"                                     => TestData.listAccountsResponse
          case "getnewaddress"                                    => TestData.getNewAddressResponse
          case "generate"                                         => TestData.generateResponse
          case "sendfrom" if params.contains("insufficientFunds") => TestData.insufficientFundsResponse
          case "sendfrom"                                         => TestData.sendFromResponse
          case "sendtoaddress" if params(1).toDouble > 100        => TestData.insufficientFundsResponse
          case "sendtoaddress"                                    => TestData.sendToAddressResponse
          case "settxfee" if params(0).toDouble < 0               => TestData.setTxFeeOutOfRangeResponse
          case "settxfee"                                         => TestData.setTxFeeResponse
          case "gettransaction"                                   => TestData.getTransactionResponse
          case "getrawtransaction"                                => TestData.getRawTransactionResponse
          case "listsinceblock"                                   => TestData.listSinceBlockResponse
          case "sendmany"                                         => TestData.sendManyResponse
          case "createrawtransaction"                             => TestData.createRawTransaction
          case "signrawtransaction"                               => TestData.signRawTransaction
          case "sendrawtransaction"                               => TestData.sendRawTransaction
          case "estimatesmartfee"                                 => TestData.estimateSmartFee
          case "validateaddress"                                  => TestData.validateAddress

          case _ => JsNumber(-1)
        }
        json.prettyPrint
    }
}
