package com.bitcoinpaygate.bitcoin4s

import com.bitcoinpaygate.bitcoin4s.ClientObjects._
import com.bitcoinpaygate.bitcoin4s.Responses.{GetNewAddress, UnspentTransaction}
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import org.scalatest.{AsyncWordSpec, Matchers}

class BitcoinClientIntegrationTest extends AsyncWordSpec with Matchers {
  implicit val akkaHttpBackend = AkkaHttpBackend()
  implicit val monadError = akkaHttpBackend.responseMonad
  val bitcoinClient = BitcoinClient("user", "password", "localhost", 18443)

  "BitcoinClient" should {
    "get wallet info" in {
      bitcoinClient.walletInfo.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get network info" in {
      bitcoinClient.networkInfo.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get mining info" in {
      bitcoinClient.miningInfo.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get mem pool info" in {
      bitcoinClient.memPoolInfo.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get blockchain info" in {
      bitcoinClient.blockchainInfo.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "estimate smart fee" in {
      bitcoinClient.estimateSmartFee(6, Some(EstimateMode.CONSERVATIVE)).map { result =>
        result shouldBe Symbol("right")
      }
    }
    "list unspent transactions" in {
      bitcoinClient.listUnspentTransactions().map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get new address" in {
      bitcoinClient.getNewAddress().map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get new address with type" in {
      bitcoinClient.getNewAddress(None, Some(AddressType.LEGACY)).map { result =>
        result shouldBe Symbol("right")
      }
    }

    "send to address" in {
      val sendToAddress = (for {
        newAddress <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendToAddress <- BitcoinResponseT(bitcoinClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map { result =>
        result shouldBe Symbol("right")
      }
    }

    "set tx fee" in {
      bitcoinClient.setTxFee(0.05).map { result =>
        result shouldBe Symbol("right")
      }
    }
    "generate" in {
      bitcoinClient.generate(1).map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        transaction <- BitcoinResponseT(bitcoinClient.getTransaction(unspentTransaction.unspentTransactions.head.txid))
      } yield transaction).value

      transaction.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        transaction <- BitcoinResponseT(
          bitcoinClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "list since block" in {
      val listSinceBlock = (for {
        hash <- BitcoinResponseT(bitcoinClient.generate(1))
        listSinceBlock <- BitcoinResponseT(bitcoinClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendMany <- BitcoinResponseT(bitcoinClient.sendMany(recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        newAddress1 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        createRawTransaction <- BitcoinResponseT(
          bitcoinClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        newAddress1 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendRawTransaction <- BitcoinResponseT(
          bitcoinClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map { result =>
        result shouldBe Symbol("right")
      }
    }
    "validate address" in {
      bitcoinClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }

    "get change address" in {
      val result = bitcoinClient.getRawChangeAddress(Some(AddressType.BECH32))
      result.map(_ shouldBe Symbol("right"))
    }

    "create new wallet in" in {
      val newWalletName = System.nanoTime().toString
      val result = bitcoinClient.createWallet(newWalletName)
      result.map {
        case Left(_)          => throw new RuntimeException("test failed")
        case Right(newWallet) => newWallet.name shouldBe newWalletName
      }
    }
  }

  private def rawTransactionInputs(unspentTransaction: UnspentTransaction): RawTransactionInputs =
    RawTransactionInputs(List(RawTransactionInput(unspentTransaction.txid, unspentTransaction.vout)))

  private def recipients(amount: BigDecimal, addresses: GetNewAddress*): Recipients = {
    val amountToSplit = (amount - 0.01) / addresses.length
    Recipients(addresses.map { address =>
      address.address -> amountToSplit
    }.toMap)
  }
}
