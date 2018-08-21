package com.bitcoinpaygate.bitcoin4s

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.bitcoinpaygate.bitcoin4s.ClientObjects.{AddressType, RawTransactionInput, RawTransactionInputs}
import com.bitcoinpaygate.bitcoin4s.Responses.GeneralErrorResponse
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class BitcoinClientTest extends FlatSpec with Matchers with ScalaFutures {
  implicit val system = ActorSystem("unit-tests")
  implicit val materializer = ActorMaterializer()

  val testHttpClient = new BitcoinTestClient("user", "password", "localhost", 1337)
  val bitcoinClient = new BitcoinClient(testHttpClient)

  it should "return walletinfo" in {
    whenReady(bitcoinClient.walletInfo) {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(walletInfo) =>
        walletInfo.balance shouldBe BigDecimal("1.65751751")
        walletInfo.unconfirmed_balance shouldBe BigDecimal(0.01)
    }
  }

  it should "generate blocks" in {
    whenReady(bitcoinClient.generate(2)) {
      case Left(x) => throw new RuntimeException("unexpected bitcoind response " + x)
      case Right(generated) => generated.hashes should contain theSameElementsAs Seq(
        "36252b5852a5921bdfca8701f936b39edeb1f8c39fffe73b0d8437921401f9af",
        "5f2956817db1e386759aa5794285977c70596b39ea093b9eab0aa4ba8cd50c06")
    }
  }

  it should "return networkinfo" in {
    whenReady(bitcoinClient.networkInfo) {
      case Left(_)            => throw new RuntimeException("unexpected bitcoind response")
      case Right(networkInfo) => networkInfo.connections shouldBe 8
    }
  }

  it should "return mininginfo" in {
    whenReady(bitcoinClient.miningInfo) {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(miningInfo) => miningInfo.blocks shouldBe 1089632
    }
  }

  it should "return memPoolInfo" in {
    whenReady(bitcoinClient.memPoolInfo) {
      case Left(_)            => throw new RuntimeException("unexpected bitcoind response")
      case Right(memPoolInfo) => memPoolInfo.size shouldBe 4
    }
  }

  it should "return blockchainInfo" in {
    whenReady(bitcoinClient.blockchainInfo) {
      case Left(_)               => throw new RuntimeException("unexpected bitcoind response")
      case Right(blockchainInfo) => blockchainInfo.chain shouldBe "test"
    }
  }

  it should "estimate fee" in {
    whenReady(bitcoinClient.estimateFee(Some(6))) {
      case Left(_)    => throw new RuntimeException("unexpected bitcoind response")
      case Right(fee) => fee.estimate shouldBe BigDecimal("0.00010244")
    }
  }

  it should "estimate smart fee" in {
    whenReady(bitcoinClient.estimateSmartFee(6)) {
      case Left(_)    => throw new RuntimeException("unexpected bitcoind response")
      case Right(fee) =>
        fee.feerate shouldBe Some(0.00010244)
        fee.blocks shouldBe 6
    }
  }

  it should "return unspent transactions" in {
    whenReady(bitcoinClient.listUnspentTransactions(minimumConfirmations = Some(0), maximumConfirmations = Some(99999999))) {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(unspentTransactions) =>
        unspentTransactions.unspentTransactions.size shouldBe 2
        unspentTransactions.unspentTransactions.head.address shouldBe "mxC1MksGZQAARADNQutrT5FPVn76bqmgZW"
    }
  }

  it should "return accounts" in {
    whenReady(bitcoinClient.listAccounts()) {
      case Left(_)         => throw new RuntimeException("unexpected bitcoind response")
      case Right(accounts) => accounts.accounts.size shouldBe 3
    }
  }

  it should "return new address for default account" in {
    whenReady(bitcoinClient.getNewAddress()) {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(newAddress) => newAddress.address should have size 34
    }
  }

  it should "return new address" in {
    whenReady(bitcoinClient.getNewAddress(Some("testaccount"))) {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(newAddress) => newAddress.address should have size 34
    }
  }

  it should "return new address for p2sh-segwit address type" in {
    whenReady(bitcoinClient.getNewAddress(None, Some(AddressType.P2SH_SEGWIT))) {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(newAddress) => newAddress.address should have size 34
    }
  }

  it should "add new witness address" in {
    whenReady(bitcoinClient.addWitnessAddress("mhFaYEiuBV4Nc53PGh1FFGEjj7xrjnQYnB")) {
      case Left(_)               => throw new RuntimeException("unexpected bitcoind response")
      case Right(witnessAddress) => witnessAddress.address shouldBe "2N9pJLCWbaGbfvgD2vYFL3d7NP6ZmCPf6f8"
    }
  }

  it should "sendfrom should send and return transation id" in {
    whenReady(bitcoinClient.sendFrom("testaccount", "nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001, None)) {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id should have size 64
    }
  }

  it should "sendfrom should insufficient handle errors" in {
    whenReady(bitcoinClient.sendFrom("insufficientFunds", "nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001, None)) {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.insufficientFundsResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  it should "new address should handle parse error" in {
    whenReady(bitcoinClient.getNewAddress(Some("parseError"))) {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.parseErrorResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  "sendtoaddress" should "send and return transation id" in {
    whenReady(bitcoinClient.sendToAddress("nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001)) {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id should have size 64
    }
  }

  it should "handle insufficient funds errors" in {
    whenReady(bitcoinClient.sendToAddress("nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 101)) {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.insufficientFundsResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  it should "set transaction fee" in {
    whenReady(bitcoinClient.setTxFee(BigDecimal(0.0003))) {
      case Left(_)         => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) => response.result shouldBe true
    }
  }

  it should "respond with error for out of range tx fee" in {
    whenReady(bitcoinClient.setTxFee(BigDecimal(-1))) {
      case Left(err) =>
        err shouldBe a[GeneralErrorResponse]
        err.errorMessage.parseJson shouldBe TestData.setTxFeeOutOfRangeResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  it should "get transaction by id" in {
    val txid = "4528087ee62cc971be2d8dcf6c4b39d5603a0bc66cfb16c6f2448ea52f3cda3c"
    whenReady(bitcoinClient.getTransaction(txid)) {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) =>
        response.fee shouldBe Some(BigDecimal(-0.1))
        response.details should have size 2
    }
  }

  "listsinceblock" should "return hash of last block and list of transactions since given block" in {
    val blockhash = "4fed3588db4a6e40597620bd957beb959eacf502291e83a39898a740211727b8"
    val targetConfirmations = 3
    val includeWatchOnly = false
    whenReady(bitcoinClient.listSinceBlock(blockhash, targetConfirmations, includeWatchOnly)) {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) =>
        response.lastblock shouldBe "4fed3588db4a6e40597620bd957beb959eacf502291e83a39898a740211727b8"
        response.transactions should have size 2
    }
  }

  "sendmany" should "return transaction id" in {
    val txId = "b5d1a82d7fd1f0e566bb0aabed172019854e2dff0ae729dc446beefd17c5c0cc"
    val sendManyMap = ClientObjects.Recipients(Map("address1" -> 0.1, "address2" -> 0.3))
    whenReady(bitcoinClient.sendMany(recipients = sendManyMap)) {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id shouldBe txId
    }
  }

  "createrawtransaction" should "return transaction hex" in {
    val hex = "02000000000180969800000000001976a914f5b32cc7579d678b60780846128b0f98f74cd10e88ac00000000"
    val inputs = RawTransactionInputs(List(RawTransactionInput("b5d1a82d7fd1f0e566bb0aabed172019854e2dff0ae729dc446beefd17c5c0cc", 1, None)))
    val outputs = ClientObjects.Recipients(Map("address1" -> 0.1, "address2" -> 0.3))
    whenReady(bitcoinClient.createRawTransaction(inputs, outputs)) {
      case Left(_)               => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionHex) => transactionHex.hex shouldBe hex
    }
  }

  "signrawtransaction" should "return signed transaction" in {
    val hex = "02000000000180969800000000001976a914f5b32cc7579d678b60780846128b0f98f74cd10e88ac00000000"
    val signedHex = "02000000010205704f11711b204e691c257ac7ab84a0014e38dda5c35e1936d11fc7030432000000004948304502210087962368e1f03ddc03b96ef934d2058abe080e9f551f69929c75f2fe7324036e02201369850a0b1c4f7b3148631f3c50063644d7959abb5e97cda9db43dd9b6e867d01ffffffff0160720195000000001976a914835328a1b2103387912fcf054cc138c38064b08b88ac00000000"
    whenReady(bitcoinClient.signRawTransaction(hex)) {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(signedRawTransaction) =>
        signedRawTransaction.hex shouldBe signedHex
        signedRawTransaction.complete shouldBe true
    }
  }

  "sendrawtransaction" should "return signed transaction" in {
    val txId = "abd8d0a5f6c7ca5836ada0aa214fdc7c6e9488281f0369d01527b7a57eaf7fb0"
    val signedHex = "02000000010205704f11711b204e691c257ac7ab84a0014e38dda5c35e1936d11fc7030432000000004948304502210087962368e1f03ddc03b96ef934d2058abe080e9f551f69929c75f2fe7324036e02201369850a0b1c4f7b3148631f3c50063644d7959abb5e97cda9db43dd9b6e867d01ffffffff0160720195000000001976a914835328a1b2103387912fcf054cc138c38064b08b88ac00000000"
    whenReady(bitcoinClient.sendRawTransaction(signedHex)) {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id shouldBe txId

    }
  }

}
