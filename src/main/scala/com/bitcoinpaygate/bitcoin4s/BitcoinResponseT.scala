package com.bitcoinpaygate.bitcoin4s

import com.bitcoinpaygate.bitcoin4s.Responses.{BitcoinResponse, CorrectResponse}
import com.softwaremill.sttp.MonadError

final case class BitcoinResponseT[R[_], A <: CorrectResponse](
    value: R[BitcoinResponse[A]]
  )(implicit monadError: MonadError[R]) {
  import com.softwaremill.sttp.monadSyntax._

  def map[B <: CorrectResponse](f: A => B): BitcoinResponseT[R, B] =
    BitcoinResponseT(value.map(_.map(f)))

  def flatMap[B <: CorrectResponse](f: A => BitcoinResponseT[R, B]): BitcoinResponseT[R, B] =
    BitcoinResponseT(value.flatMap {
      case Right(a)    => f(a).value
      case Left(error) => monadError.unit(Left(error))
    })

}
