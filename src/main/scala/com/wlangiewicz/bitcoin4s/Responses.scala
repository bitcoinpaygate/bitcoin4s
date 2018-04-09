package com.wlangiewicz.bitcoin4s

object Responses {

  type BitcoinResponse[T <: CorrectResponse] = Either[ErrorResponse, T]

  trait ErrorResponse {
    def errorMessage: String
  }

  case class GeneralErrorResponse(errorMessage: String) extends ErrorResponse

  trait CorrectResponse

  case class GetWalletInfo(
      walletversion: Int,
      balance: BigDecimal,
      unconfirmed_balance: BigDecimal,
      txcount: Int,
      keypoololdest: Int,
      keypoolsize: Int,
      unlocked_until: Option[Int]) extends CorrectResponse

  case class GetNetworkInfo(
      version: Int,
      subversion: String,
      protocolversion: Int,
      timeoffset: Int,
      connections: Int,
      proxy: Option[String],
      relayfee: BigDecimal,
      localservices: String,
      networks: Vector[Network]) extends CorrectResponse

  case class GetMiningInfo(
      blocks: Int,
      currentblocksize: Int,
      currentblocktx: Int,
      difficulty: Int,
      errors: String,
      genproclimit: Option[Int],
      networkhashps: Int,
      pooledtx: Int,
      testnet: Boolean,
      chain: String,
      generate: Option[Boolean],
      hashespersec: Option[Int]) extends CorrectResponse

  case class GetMemPoolInfo(
      size: Int,
      bytes: Int,
      usage: Int,
      maxmempool: Int,
      mempoolminfee: Int) extends CorrectResponse

  case class GetBlockChainInfo(
      chain: String,
      blocks: Int,
      headers: Int,
      bestblockhash: String,
      difficulty: BigDecimal,
      mediantime: Int,
      verificationprogress: BigDecimal,
      chainwork: String,
      pruned: Boolean,
      pruneheight: Option[Int]) extends CorrectResponse

  case class EstimateFee(estimate: BigDecimal) extends CorrectResponse

  case class UnspentTransactions(unspentTransactions: Vector[UnspentTransaction]) extends CorrectResponse

  case class UnspentTransaction(
      txid: String,
      vout: Int,
      address: String,
      account: Option[String],
      scriptPubKey: String,
      amount: BigDecimal,
      confirmations: Long,
      spendable: Boolean,
      solvable: Boolean)

  case class Accounts(accounts: Vector[Account]) extends CorrectResponse

  case class Account(
      accountId: String,
      balance: BigDecimal) extends CorrectResponse

  case class GetNewAddress(address: String) extends CorrectResponse

  case class AddWitnessAddress(address: String) extends CorrectResponse

  case class SentTransactionId(id: String) extends CorrectResponse

  case class HeaderHashes(hashes: Seq[String]) extends CorrectResponse

  case class SetTxFee(result: Boolean) extends CorrectResponse

  case class ListSinceBlockResponse(transactions: List[Transaction], lastblock: String) extends CorrectResponse

  case class ListSinceBlockTransaction(
      address: String,
      category: String,
      amount: BigDecimal,
      vout: Option[Int],
      fee: Option[BigDecimal],
      confirmations: Option[Long],
      trusted: Option[Boolean],
      generated: Option[Boolean],
      blockhash: Option[String],
      blockindex: Option[Int],
      blocktime: Option[Long],
      txid: String,
      walletconflicts: List[String],
      time: Long,
      timereceived: Option[Long],
      comment: Option[String],
      to: Option[String],
      `bip125-replaceable`: String,
      abandoned: Option[Boolean])

  case class Transaction(
      amount: BigDecimal,
      fee: Option[BigDecimal],
      confirmations: Int,
      generated: Option[Boolean],
      blockhash: Option[String],
      blockindex: Option[Int],
      blocktime: Option[Int],
      txid: String,
      walletconflicts: List[String],
      time: Long,
      timereceived: Long,
      `bip125-replaceable`: String,
      comment: Option[String],
      to: Option[String],
      details: List[TransactionDetails],
      hex: String) extends CorrectResponse

  case class TransactionDetails(
      involvesWatchonly: Option[Boolean],
      account: String,
      address: Option[String],
      category: String,
      amount: BigDecimal,
      vout: Int,
      fee: Option[BigDecimal],
      abandoned: Option[Boolean])

}
