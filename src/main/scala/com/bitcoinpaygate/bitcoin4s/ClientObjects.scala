package com.bitcoinpaygate.bitcoin4s

object ClientObjects {
  case class Recipients(value: Map[String, BigDecimal])
  case class RawTransactionInputs(inputs: List[RawTransactionInput])
  case class RawTransactionInput(txid: String, vout: Int, sequence: Option[Int] = None)
  object EstimateMode extends Enumeration {
    val UNSET, ECONOMICAL, CONSERVATIVE = Value
  }
}
