package com.bitcoinpaygate.bitcoin4s

import spray.json._

object TestData {

  val walletInfoResponse = readJson("wallet-info-response.json")
  val networkInfoResponse = readJson("network-info-response.json")
  val miningInfoResponse = readJson("mining-info-response.json")
  val memPoolInfoResponse = readJson("mem-pool-info-response.json")
  val blockchainInfoResponse = readJson("blockchain-info-response.json")
  val estimateFeeResponse = readJson("esitmate-fee-response.json")
  val listUnspentResponse = readJson("list-unspent-response.json")
  val listAccountsResponse = readJson("list-accounts-response.json")
  val getNewAddressResponse = readJson("get-new-address-response")
  val addWitnessAddressResponse = readJson("add-witness-address-response.json")
  val sendFromResponse = readJson("send-from-response.json")
  val sendToAddressResponse = sendFromResponse
  val generateResponse = readJson("generate-response.json")
  val parseErrorResponse = readJson("parse-error-response.json")
  val insufficientFundsResponse = readJson("insufficient-funds-response.json")
  val setTxFeeResponse = readJson("set-tx-fee-response.json")
  val setTxFeeOutOfRangeResponse = readJson("set-tx-tee-out-of-range-response.json")
  val getTransactionResponse = readJson("get-transaction-response.json")
  val listSinceBlockResponse = readJson("list-since-block-response.json")
  val sendManyResponse = readJson("send-many-response.json")

  private def readJson(name: String): JsValue = {
    val json = scala.io.Source.fromResource(name).getLines.mkString
    JsonParser(json)
  }

}
