package com.bitcoinpaygate.bitcoin4s

import com.bitcoinpaygate.bitcoin4s.Responses.{BitcoinResponse, CorrectResponse}

import scala.concurrent.{ExecutionContext, Future}

case class BitcoinResponseT[A <: CorrectResponse](value: Future[BitcoinResponse[A]]) {
  def map[B <: CorrectResponse](f: A => B)(implicit executionContext: ExecutionContext): BitcoinResponseT[B] = {
    BitcoinResponseT(value.map(_.map(f)))
  }

  def flatMap[B <: CorrectResponse](f: A => BitcoinResponseT[B])(implicit executionContext: ExecutionContext): BitcoinResponseT[B] = {
    BitcoinResponseT(
      value.flatMap {
        case Right(a)    => f(a).value
        case Left(error) => Future.successful(Left(error))
      })
  }

}
