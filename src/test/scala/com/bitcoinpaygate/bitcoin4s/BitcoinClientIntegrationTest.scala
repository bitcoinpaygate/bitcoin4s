package com.bitcoinpaygate.bitcoin4s

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.bitcoinpaygate.bitcoin4s.ClientObjects._
import com.bitcoinpaygate.bitcoin4s.Responses.{GetNewAddress, UnspentTransaction}
import org.scalatest.{AsyncWordSpec, Ignore, Matchers}

@Ignore
class BitcoinClientIntegrationTest extends AsyncWordSpec with Matchers {
  implicit val actorSystem = ActorSystem("test")
  implicit val materializer = ActorMaterializer()
  val bitcoinClient = new BitcoinClient("user", "password", "localhost", 18332)

  "BitcoinClient" should {
    "get wallet info" in {
      bitcoinClient.walletInfo.map { result =>
        result shouldBe 'right
      }
    }
    "get network info" in {
      bitcoinClient.networkInfo.map { result =>
        result shouldBe 'right
      }
    }
    "get mining info" in {
      bitcoinClient.miningInfo.map { result =>
        result shouldBe 'right
      }
    }
    "get mem pool info" in {
      bitcoinClient.memPoolInfo.map { result =>
        result shouldBe 'right
      }
    }
    "get blockchain info" in {
      bitcoinClient.blockchainInfo.map { result =>
        result shouldBe 'right
      }
    }
    "estimate fee" in {
      bitcoinClient.estimateFee().map { result =>
        result shouldBe 'right
      }
    }
    "estimate smart fee" in {
      bitcoinClient.estimateSmartFee(6, Some(EstimateMode.CONSERVATIVE)).map { result =>
        result shouldBe 'right
      }
    }
    "list unspent transactions" in {
      bitcoinClient.listUnspentTransactions().map { result =>
        result shouldBe 'right
      }
    }
    "list accounts" in {
      bitcoinClient.listAccounts().map { result =>
        result shouldBe 'right
      }
    }
    "get new address" in {
      bitcoinClient.getNewAddress().map { result =>
        result shouldBe 'right
      }
    }
    "get new address for account" in {
      bitcoinClient.getNewAddress(Some("account")).map { result =>
        result shouldBe 'right
      }
    }
    "get new address with type" in {
      bitcoinClient.getNewAddress(None, Some(AddressType.LEGACY)).map { result =>
        result shouldBe 'right
      }
    }
    "add witness address" in {
      val addWitnessAddress = (for {
        newAddress <- BitcoinResponseT(bitcoinClient.getNewAddress())
        witnessAddress <- BitcoinResponseT(bitcoinClient.addWitnessAddress(newAddress.address))
      } yield witnessAddress).value

      addWitnessAddress.map { result =>
        result shouldBe 'right
      }
    }

    "send from" in {
      val sendFrom = (for {
        newAddress <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendFrom <- BitcoinResponseT(bitcoinClient.sendFrom("", newAddress.address, 10, Some(2)))
      } yield sendFrom).value

      sendFrom.map { result =>
        result shouldBe 'right
      }
    }

    "send to address" in {
      val sendToAddress = (for {
        newAddress <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendToAddress <- BitcoinResponseT(bitcoinClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map { result =>
        result shouldBe 'right
      }
    }

    "set tx fee" in {
      bitcoinClient.setTxFee(0.05).map { result =>
        result shouldBe 'right
      }
    }
    "generate" in {
      bitcoinClient.generate(1).map { result =>
        result shouldBe 'right
      }
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        transaction <- BitcoinResponseT(bitcoinClient.getTransaction(unspentTransaction.unspentTransactions.head.txid))
      } yield transaction).value

      transaction.map { result =>
        result shouldBe 'right
      }
    }
    "list since block" in {
      val listSinceBlock = (for {
        hash <- BitcoinResponseT(bitcoinClient.generate(1))
        listSinceBlock <- BitcoinResponseT(bitcoinClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map { result =>
        result shouldBe 'right
      }
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendMany <- BitcoinResponseT(bitcoinClient.sendMany("", recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map { result =>
        result shouldBe 'right
      }
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        newAddress1 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        createRawTransaction <-
          BitcoinResponseT(
            bitcoinClient.createRawTransaction(
              rawTransactionInputs(input.unspentTransactions.head),
              recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
            )
          )
      } yield createRawTransaction).value

      createRawTransaction.map { result =>
        result shouldBe 'right
      }
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- BitcoinResponseT(bitcoinClient.listUnspentTransactions())
        newAddress1 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- BitcoinResponseT(bitcoinClient.getNewAddress())
        sendRawTransaction <-
          BitcoinResponseT(
            bitcoinClient.sendRawTransaction(
              rawTransactionInputs(input.unspentTransactions.head),
              recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
            )
          )
      } yield sendRawTransaction).value

      sendRawTransaction.map { result =>
        result shouldBe 'right
      }
    }
  }

  private def rawTransactionInputs(unspentTransaction: UnspentTransaction): RawTransactionInputs =
    RawTransactionInputs(List(RawTransactionInput(unspentTransaction.txid, unspentTransaction.vout)))

  private def recipients(amount: BigDecimal, addresses: GetNewAddress*): Recipients = {
    val amountToSplit = (amount - 0.01) / addresses.length
    Recipients(
      addresses.map { address =>
        address.address -> amountToSplit
      }.toMap
    )
  }
}
