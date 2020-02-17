package com.bitcoinpaygate.bitcoin4s

import spray.json._

object TestData {

  val walletInfoResponse = readJson("wallet-info-response.json")
  val networkInfoResponse = readJson("network-info-response.json")
  val miningInfoResponse = readJson("mining-info-response.json")
  val memPoolInfoResponse = readJson("mem-pool-info-response.json")
  val blockchainInfoResponse = readJson("blockchain-info-response.json")
  val listUnspentResponse = readJson("list-unspent-response.json")
  val getNewAddressResponse = readJson("get-new-address-response.json")
  val getRawChangeAddressResponse = readJson("get-raw-change-address-response.json")
  val sendToAddressResponse = readJson("sendtoaddress-response.json")
  val generateToAddressResponse = readJson("generate-to-address-response.json")
  val parseErrorResponse = readJson("parse-error-response.json")
  val insufficientFundsResponse = readJson("insufficient-funds-response.json")
  val setTxFeeResponse = readJson("set-tx-fee-response.json")
  val setTxFeeOutOfRangeResponse = readJson("set-tx-tee-out-of-range-response.json")
  val getTransactionResponse = readJson("get-transaction-response.json")
  val getRawTransactionResponseVerbose = readJson("get-raw-transaction-response-verbose.json")
  val getRawTransactionResponseVerboseCoinbase = readJson("get-raw-transaction-response-verbose-coinbase.json")
  val listSinceBlockResponse = readJson("list-since-block-response.json")
  val sendManyResponse = readJson("send-many-response.json")
  val createRawTransaction = readJson("create-raw-transaction.json")
  val signRawTransactionWithWallet = readJson("sign-raw-transaction-with-wallet.json")
  val sendRawTransaction = readJson("send-raw-transaction.json")
  val estimateSmartFee = readJson("estimate-smart-fee-response.json")
  val validateAddress = readJson("validate-address-response.json")
  val createWalletResponse = readJson("create-wallet-response.json")
  val invalidJsonResponse = readJson("invalid-json-response.json")

  private def readJson(name: String): JsValue = {
    val json = scala.io.Source.fromResource(name).getLines.mkString
    JsonParser(json)
  }

}
