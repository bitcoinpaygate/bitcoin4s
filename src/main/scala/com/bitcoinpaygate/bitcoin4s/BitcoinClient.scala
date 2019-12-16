package com.bitcoinpaygate.bitcoin4s

import com.bitcoinpaygate.bitcoin4s.ClientObjects.{AddressType, EstimateMode, RawTransactionInputs, Recipients}
import com.bitcoinpaygate.bitcoin4s.Responses._
import com.softwaremill.sttp._
import spray.json._

import scala.util.{Failure, Success, Try}

final case class BitcoinClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int,
    wallet: Option[String] = None
  )(implicit sttpBackend: SttpBackend[R, Nothing])
    extends JsonFormats {
  implicit private val monadError = sttpBackend.responseMonad

  private val request = {
    val defaultWalletName = ""
    val walletName = wallet.getOrElse(defaultWalletName)
    val uri = uri"http://$host:$port/wallet/$walletName"
    sttp.auth
      .basic(user, password)
      .post(uri)
  }

  private def as[T <: CorrectResponse](implicit reader: JsonReader[T]): ResponseAs[BitcoinResponse[T], Nothing] =
    asString.map { r =>
      val responseObject = r.parseJson.asJsObject
      responseObject.fields("result") match {
        case JsNull =>
          Left(GeneralErrorResponse(responseObject.fields.get("error").map(_.toString).getOrElse("Unknown error")))
        case json: JsValue =>
          Try(json.convertTo[T]) match {
            case Success(success) => Right(success)
            case Failure(_)       => Left(GeneralErrorResponse(s"Error parsing JSON, got: ${json.compactPrint}"))
          }
      }
    }

  def walletInfo: R[BitcoinResponse[GetWalletInfo]] =
    request.body(method("getwalletinfo")).response(as[GetWalletInfo]).send()

  def networkInfo: R[BitcoinResponse[GetNetworkInfo]] =
    request.body(method("getnetworkinfo")).response(as[GetNetworkInfo]).send()

  def miningInfo: R[BitcoinResponse[GetMiningInfo]] =
    request.body(method("getmininginfo")).response(as[GetMiningInfo]).send()

  def memPoolInfo: R[BitcoinResponse[GetMemPoolInfo]] =
    request.body(method("getmempoolinfo")).response(as[GetMemPoolInfo]).send()

  def blockchainInfo: R[BitcoinResponse[GetBlockChainInfo]] =
    request.body(method("getblockchaininfo")).response(as[GetBlockChainInfo]).send()

  def estimateSmartFee(
      confTarget: Int,
      estimateMode: Option[EstimateMode.Value] = None
    )(
    ): R[BitcoinResponse[EstimateSmartFee]] =
    request
      .body(method("estimatesmartfee", confTarget +: estimateMode.map(_.toString).toVector))
      .response(as[EstimateSmartFee])
      .send()

  def listUnspentTransactions(
      minimumConfirmations: Option[Int] = None,
      maximumConfirmations: Option[Int] = None
    )(
    ): R[BitcoinResponse[UnspentTransactions]] =
    request
      .body(method("listunspent", Vector(minimumConfirmations.getOrElse(1), maximumConfirmations.getOrElse(9999999))))
      .response(as[UnspentTransactions])
      .send()

  def getNewAddress(): R[BitcoinResponse[GetNewAddress]] =
    request
      .body(method("getnewaddress"))
      .response(as[GetNewAddress])
      .send()

  def getNewAddress(
      label: Option[String],
      addressType: Option[AddressType.Value]
    )(
    ): R[BitcoinResponse[GetNewAddress]] =
    request
      .body(method("getnewaddress", label.getOrElse("") +: addressType.map(_.toString).toVector))
      .response(as[GetNewAddress])
      .send()

  def getRawChangeAddress(addressType: Option[AddressType.Value] = None): R[BitcoinResponse[GetRawChangeAddress]] =
    request
      .body(method("getrawchangeaddress", addressType.map(_.toString).toVector))
      .response(as[GetRawChangeAddress])
      .send()

  def sendToAddress(
      to: String,
      amount: BigDecimal,
      comment: String = "",
      commentTo: String = ""
    )(
    ): R[BitcoinResponse[SentTransactionId]] =
    request
      .body(method("sendtoaddress", Vector(to, amount, comment, commentTo)))
      .response(as[SentTransactionId])
      .send()

  def setTxFee(btcPerKb: BigDecimal)(): R[BitcoinResponse[SetTxFee]] =
    request
      .body(method("settxfee", Vector(btcPerKb)))
      .response(as[SetTxFee])
      .send()

  def generate(number: Int)(): R[BitcoinResponse[HeaderHashes]] =
    request
      .body(method("generate", Vector(number)))
      .response(as[HeaderHashes])
      .send()

  def getTransaction(txid: String)(): R[BitcoinResponse[Transaction]] =
    request
      .body(method("gettransaction", Vector(txid)))
      .response(as[Transaction])
      .send()

  def getRawTransactionVerbose(txid: String)(): R[BitcoinResponse[RawTransaction]] =
    request
      .body(method("getrawtransaction", Vector(txid, true)))
      .response(as[RawTransaction])
      .send()

  def listSinceBlock(
      headerHash: String,
      targetConfirmations: Int = 1,
      includeWatchOnly: Boolean = false
    )(
    ): R[BitcoinResponse[ListSinceBlockResponse]] =
    request
      .body(method("listsinceblock", Vector(headerHash, targetConfirmations, includeWatchOnly)))
      .response(as[ListSinceBlockResponse])
      .send()

  def sendMany(recipients: ClientObjects.Recipients)(): R[BitcoinResponse[SentTransactionId]] =
    request
      .body(method("sendmany", Vector("", recipients)))
      .response(as[SentTransactionId])
      .send()

  def createRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(): R[BitcoinResponse[TransactionHex]] =
    request
      .body(method("createrawtransaction", Vector(inputs, outputs)))
      .response(as[TransactionHex])
      .send()

  def signRawTransaction(transactionHex: String)(): R[BitcoinResponse[SignedRawTransaction]] =
    request
      .body(method("signrawtransactionwithwallet", Vector(transactionHex)))
      .response(as[SignedRawTransaction])
      .send()

  def sendRawTransaction(signedHex: String)(): R[BitcoinResponse[SentTransactionId]] =
    request
      .body(method("sendrawtransaction", Vector(signedHex)))
      .response(as[SentTransactionId])
      .send()

  def sendRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(): R[BitcoinResponse[SentTransactionId]] =
    (for {
      rawTransaction <- BitcoinResponseT(createRawTransaction(inputs, outputs))
      signedTransaction <- BitcoinResponseT(signRawTransaction(rawTransaction.hex))
      sentTransactionId <- BitcoinResponseT(sendRawTransaction(signedTransaction.hex))
    } yield sentTransactionId).value

  def validateAddress(address: String)(): R[BitcoinResponse[ValidateAddress]] =
    request
      .body(method("validateaddress", Vector(address)))
      .response(as[ValidateAddress])
      .send()

  implicit private def flatten[T <: CorrectResponse](
      response: R[Response[BitcoinResponse[T]]]
    ): R[BitcoinResponse[T]] = {
    import com.softwaremill.sttp.monadSyntax._
    response.map { response =>
      response.body.left.map(error => GeneralErrorResponse(error)).joinRight
    }
  }

  private def method(methodName: String, params: Vector[Any] = Vector.empty) =
    if (params.isEmpty) {
      s"""{"method": "$methodName"}"""
    } else {
      val formattedParams = HttpParamsConverter.rpcParamsToJson(params)
      s"""{"method": "$methodName", "params": [${formattedParams.mkString(",")}]}"""
    }

  def createWallet(walletName: String): R[BitcoinResponse[CreateWallet]] =
    request
      .body(method("createwallet", Vector(walletName)))
      .response(as[CreateWallet])
      .send()

}
