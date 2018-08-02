package com.bitcoinpaygate.bitcoin4s

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.bitcoinpaygate.bitcoin4s.ClientObjects.{RawTransactionInputs, Recipients}
import com.bitcoinpaygate.bitcoin4s.Responses._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

class BitcoinClient(httpClient: HttpClient)(implicit system: ActorSystem, materializer: ActorMaterializer) extends JsonFormats {

  def this(user: String, password: String, host: String, port: Int)(implicit system: ActorSystem, materializer: ActorMaterializer) {
    this(new AkkaHttpClient(user, password, host, port))
  }

  private def unmarshalResponse[T <: CorrectResponse](httpResponse: HttpResponse)(implicit executionContext: ExecutionContext, reader: JsonReader[T]): Future[BitcoinResponse[T]] = {
    Unmarshal(httpResponse).to[String].map { r =>
      val responseObject = r.parseJson.asJsObject

      responseObject.fields("result") match {
        case JsNull =>
          Left(GeneralErrorResponse(responseObject.fields.get("error").map(_.toString).getOrElse("Unknown error")))
        case a: JsValue =>
          Right(a.convertTo[T])
      }
    }
  }

  def walletInfo(implicit executionContext: ExecutionContext): Future[BitcoinResponse[GetWalletInfo]] = {
    val request = httpClient.httpRequest("getwalletinfo")
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[GetWalletInfo])
  }

  def networkInfo(implicit executionContext: ExecutionContext): Future[BitcoinResponse[GetNetworkInfo]] = {
    val request = httpClient.httpRequest("getnetworkinfo")
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[GetNetworkInfo])
  }

  def miningInfo(implicit executionContext: ExecutionContext): Future[BitcoinResponse[GetMiningInfo]] = {
    val request = httpClient.httpRequest("getmininginfo")
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[GetMiningInfo])
  }

  def memPoolInfo(implicit executionContext: ExecutionContext): Future[BitcoinResponse[GetMemPoolInfo]] = {
    val request = httpClient.httpRequest("getmempoolinfo")
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[GetMemPoolInfo])
  }

  def blockchainInfo(implicit executionContext: ExecutionContext): Future[BitcoinResponse[GetBlockChainInfo]] = {
    val request = httpClient.httpRequest("getblockchaininfo")
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[GetBlockChainInfo])
  }

  def estimateFee(blocks: Option[Int] = None)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[EstimateFee]] = {
    val request = httpClient.httpRequestWithParams("estimatefee", Vector(blocks.getOrElse(6)))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[EstimateFee])
  }

  def listUnspentTransactions(minimumConfirmations: Option[Int] = None, maximumConfirmations: Option[Int] = None)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[UnspentTransactions]] = {
    val request = httpClient.httpRequestWithParams("listunspent", Vector(minimumConfirmations.getOrElse(1), maximumConfirmations.getOrElse(9999999)))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[UnspentTransactions])
  }

  def listAccounts(confirmations: Option[Int] = None)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[Accounts]] = {
    val request = httpClient.httpRequestWithParams("listaccounts", Vector(confirmations.getOrElse(0)))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[Accounts])
  }

  def getNewAddress(account: Option[String] = None)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[GetNewAddress]] = {
    val request = httpClient.httpRequestWithParams("getnewaddress", Vector(account).flatten)
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[GetNewAddress])
  }

  def addWitnessAddress(address: String)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[AddWitnessAddress]] = {
    val request = httpClient.httpRequestWithParams("addwitnessaddress", Vector(address))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[AddWitnessAddress])
  }

  def sendFrom(account: String, to: String, amount: BigDecimal, confirmations: Option[Int])(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SentTransactionId]] = {
    val request = httpClient.httpRequestWithParams("sendfrom", Vector(account, to, amount, confirmations.getOrElse(0)))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[SentTransactionId])
  }

  def sendToAddress(to: String, amount: BigDecimal, comment: String = "", commentTo: String = "")(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SentTransactionId]] = {
    val request = httpClient.httpRequestWithParams("sendtoaddress", Vector(to, amount, comment, commentTo))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[SentTransactionId])
  }

  def setTxFee(btcPerKb: BigDecimal)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SetTxFee]] = {
    val request = httpClient.httpRequestWithParams("settxfee", Vector(btcPerKb))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[SetTxFee])
  }

  def generate(number: Int)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[HeaderHashes]] = {
    val request = httpClient.httpRequestWithParams("generate", Vector(number))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[HeaderHashes])
  }

  def getTransaction(txid: String)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[Transaction]] = {
    val request = httpClient.httpRequestWithParams("gettransaction", Vector(txid))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[Transaction])
  }

  def listSinceBlock(headerHash: String, targetConfirmations: Int = 1, includeWatchOnly: Boolean = false)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[ListSinceBlockResponse]] = {
    val request = httpClient.httpRequestWithParams("listsinceblock", Vector(headerHash, targetConfirmations, includeWatchOnly))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[ListSinceBlockResponse])
  }

  def sendMany(account: String = "", recipients: ClientObjects.Recipients)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SentTransactionId]] = {
    val request = httpClient.httpRequestWithParams("sendmany", Vector(account, recipients))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[SentTransactionId])
  }

  def createRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[TransactionHex]] = {
    val request = httpClient.httpRequestWithParams("createrawtransaction", Vector(inputs, outputs))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[TransactionHex])
  }

  def signRawTransaction(transactionHex: String)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SignedRawTransaction]] = {
    val request = httpClient.httpRequestWithParams("signrawtransaction", Vector(transactionHex))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[SignedRawTransaction])
  }

  def sendRawTransaction(signedHex: String)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SentTransactionId]] = {
    val request = httpClient.httpRequestWithParams("sendrawtransaction", Vector(signedHex))
    val response = httpClient.performRequest(request)
    response.flatMap(unmarshalResponse[SentTransactionId])
  }

  def sendRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(implicit executionContext: ExecutionContext): Future[BitcoinResponse[SentTransactionId]] = {
    (for {
      rawTransaction <- BitcoinResponseT(createRawTransaction(inputs, outputs))
      signedTransaction <- BitcoinResponseT(signRawTransaction(rawTransaction.hex))
      sentTransactionId <- BitcoinResponseT(sendRawTransaction(signedTransaction.hex))
    } yield sentTransactionId).value
  }

}
