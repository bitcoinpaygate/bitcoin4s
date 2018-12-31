package com.bitcoinpaygate.bitcoin4s

import com.bitcoinpaygate.bitcoin4s.ClientObjects.{AddressType, RawTransactionInput, RawTransactionInputs}
import com.bitcoinpaygate.bitcoin4s.Responses.GeneralErrorResponse
import com.softwaremill.sttp._
import com.softwaremill.sttp.testing.SttpBackendStub
import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class BitcoinClientTest extends FlatSpec with Matchers with TestDataHelper {
  val user = "user"
  val password = "password"
  val host = "localhost"
  val port = 1337

  implicit val stubBackend = SttpBackendStub.synchronous.whenRequestMatchesPartial {
    case RequestT(Method.POST, uri, body: StringBody, _, _, _, _) if uri == uri"http://$host:$port/" =>
      Response.ok(loadJsonResponseFromTestData(extractMethod(body.s)))
  }
  val bitcoinClient = new BitcoinClient(user, password, host, port)

  it should "return walletinfo" in {
    bitcoinClient.walletInfo match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(walletInfo) =>
        walletInfo.balance shouldBe BigDecimal("1.65751751")
        walletInfo.unconfirmed_balance shouldBe BigDecimal(0.01)
    }
  }

  it should "generate blocks" in {
    bitcoinClient.generate(2) match {
      case Left(x) => throw new RuntimeException("unexpected bitcoind response " + x)
      case Right(generated) =>
        generated.hashes should contain theSameElementsAs Seq(
          "36252b5852a5921bdfca8701f936b39edeb1f8c39fffe73b0d8437921401f9af",
          "5f2956817db1e386759aa5794285977c70596b39ea093b9eab0aa4ba8cd50c06")
    }
  }

  it should "return networkinfo" in {
    bitcoinClient.networkInfo match {
      case Left(_)            => throw new RuntimeException("unexpected bitcoind response")
      case Right(networkInfo) => networkInfo.connections shouldBe 8
    }
  }

  it should "return mininginfo" in {
    bitcoinClient.miningInfo match {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(miningInfo) => miningInfo.blocks shouldBe 1089632
    }
  }

  it should "return memPoolInfo" in {
    bitcoinClient.memPoolInfo match {
      case Left(_)            => throw new RuntimeException("unexpected bitcoind response")
      case Right(memPoolInfo) => memPoolInfo.size shouldBe 4
    }
  }

  it should "return blockchainInfo" in {
    bitcoinClient.blockchainInfo match {
      case Left(_)               => throw new RuntimeException("unexpected bitcoind response")
      case Right(blockchainInfo) => blockchainInfo.chain shouldBe "test"
    }
  }

  it should "estimate smart fee" in {
    bitcoinClient.estimateSmartFee(6) match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(fee) =>
        fee.feerate shouldBe Some(0.00010244)
        fee.blocks shouldBe 6
    }
  }

  it should "return unspent transactions" in {
    bitcoinClient.listUnspentTransactions(minimumConfirmations = Some(0), maximumConfirmations = Some(99999999)) match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(unspentTransactions) =>
        unspentTransactions.unspentTransactions.size shouldBe 2
        unspentTransactions.unspentTransactions.head.address shouldBe "mxC1MksGZQAARADNQutrT5FPVn76bqmgZW"
    }
  }

  it should "return accounts" in {
    bitcoinClient.listAccounts() match {
      case Left(_)         => throw new RuntimeException("unexpected bitcoind response")
      case Right(accounts) => accounts.accounts.size shouldBe 3
    }
  }

  it should "return new address for default account" in {
    bitcoinClient.getNewAddress() match {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(newAddress) => newAddress.address should have size 34
    }
  }

  it should "return new address" in {
    bitcoinClient.getNewAddress(Some("testaccount")) match {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(newAddress) => newAddress.address should have size 34
    }
  }

  it should "return new address for p2sh-segwit address type" in {
    bitcoinClient.getNewAddress(None, Some(AddressType.P2SH_SEGWIT)) match {
      case Left(_)           => throw new RuntimeException("unexpected bitcoind response")
      case Right(newAddress) => newAddress.address should have size 34
    }
  }

  it should "sendfrom should send and return transation id" in {
    bitcoinClient.sendFrom("testaccount", "nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001, None) match {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id should have size 64
    }
  }

  it should "sendfrom should insufficient handle errors" in {
    bitcoinClient.sendFrom("insufficientFunds", "nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001, None) match {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.insufficientFundsResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  it should "new address should handle parse error" in {
    bitcoinClient.getNewAddress(Some("parseError")) match {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.parseErrorResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  "sendtoaddress" should "send and return transation id" in {
    bitcoinClient.sendToAddress("nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001) match {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id should have size 64
    }
  }

  it should "handle insufficient funds errors" in {
    bitcoinClient.sendToAddress("nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 101) match {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.insufficientFundsResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  it should "set transaction fee" in {
    bitcoinClient.setTxFee(BigDecimal(0.0003)) match {
      case Left(_)         => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) => response.result shouldBe true
    }
  }

  it should "respond with error for out of range tx fee" in {
    bitcoinClient.setTxFee(BigDecimal(-1)) match {
      case Left(err) =>
        err shouldBe a[GeneralErrorResponse]
        err.errorMessage.parseJson shouldBe TestData.setTxFeeOutOfRangeResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid bitcoind response")
    }
  }

  it should "get transaction by id" in {
    val txid = "4528087ee62cc971be2d8dcf6c4b39d5603a0bc66cfb16c6f2448ea52f3cda3c"
    bitcoinClient.getTransaction(txid) match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) =>
        response.fee shouldBe Some(BigDecimal(-0.1))
        response.details should have size 2
    }
  }

  it should "get raw transaction by id" in {
    val txid = "4528087ee62cc971be2d8dcf6c4b39d5603a0bc66cfb16c6f2448ea52f3cda3c"
    bitcoinClient.getRawTransaction(txid) match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) =>
        response.confirmations shouldBe 374
        response.vin should have size 1
    }
  }

  "listsinceblock" should "return hash of last block and list of transactions since given block" in {
    val blockhash = "4fed3588db4a6e40597620bd957beb959eacf502291e83a39898a740211727b8"
    val targetConfirmations = 3
    val includeWatchOnly = false
    bitcoinClient.listSinceBlock(blockhash, targetConfirmations, includeWatchOnly) match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(response) =>
        response.lastblock shouldBe "4fed3588db4a6e40597620bd957beb959eacf502291e83a39898a740211727b8"
        response.transactions should have size 2
    }
  }

  "sendmany" should "return transaction id" in {
    val txId = "b5d1a82d7fd1f0e566bb0aabed172019854e2dff0ae729dc446beefd17c5c0cc"
    val sendManyMap = ClientObjects.Recipients(Map("address1" -> 0.1, "address2" -> 0.3))
    bitcoinClient.sendMany(recipients = sendManyMap) match {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id shouldBe txId
    }
  }

  "createrawtransaction" should "return transaction hex" in {
    val hex = "02000000000180969800000000001976a914f5b32cc7579d678b60780846128b0f98f74cd10e88ac00000000"
    val inputs = RawTransactionInputs(
      List(RawTransactionInput("b5d1a82d7fd1f0e566bb0aabed172019854e2dff0ae729dc446beefd17c5c0cc", 1, None)))
    val outputs = ClientObjects.Recipients(Map("address1" -> 0.1, "address2" -> 0.3))
    bitcoinClient.createRawTransaction(inputs, outputs) match {
      case Left(_)               => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionHex) => transactionHex.hex shouldBe hex
    }
  }

  "signrawtransaction" should "return signed transaction" in {
    val hex = "02000000000180969800000000001976a914f5b32cc7579d678b60780846128b0f98f74cd10e88ac00000000"
    val signedHex =
      "02000000010205704f11711b204e691c257ac7ab84a0014e38dda5c35e1936d11fc7030432000000004948304502210087962368e1f03ddc03b96ef934d2058abe080e9f551f69929c75f2fe7324036e02201369850a0b1c4f7b3148631f3c50063644d7959abb5e97cda9db43dd9b6e867d01ffffffff0160720195000000001976a914835328a1b2103387912fcf054cc138c38064b08b88ac00000000"
    bitcoinClient.signRawTransaction(hex) match {
      case Left(_) => throw new RuntimeException("unexpected bitcoind response")
      case Right(signedRawTransaction) =>
        signedRawTransaction.hex shouldBe signedHex
        signedRawTransaction.complete shouldBe true
    }
  }

  "sendrawtransaction" should "return signed transaction" in {
    val txId = "abd8d0a5f6c7ca5836ada0aa214fdc7c6e9488281f0369d01527b7a57eaf7fb0"
    val signedHex =
      "02000000010205704f11711b204e691c257ac7ab84a0014e38dda5c35e1936d11fc7030432000000004948304502210087962368e1f03ddc03b96ef934d2058abe080e9f551f69929c75f2fe7324036e02201369850a0b1c4f7b3148631f3c50063644d7959abb5e97cda9db43dd9b6e867d01ffffffff0160720195000000001976a914835328a1b2103387912fcf054cc138c38064b08b88ac00000000"
    bitcoinClient.sendRawTransaction(signedHex) match {
      case Left(_)              => throw new RuntimeException("unexpected bitcoind response")
      case Right(transactionId) => transactionId.id shouldBe txId

    }
  }

  "validateaddress" should "return if address is valid" in {
    val addr = "bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k"
    bitcoinClient.validateAddress(addr) match {
      case Left(_)             => throw new RuntimeException("unexpected bitcoind response")
      case Right(validAddress) => validAddress.isvalid shouldBe true
    }
  }

}
