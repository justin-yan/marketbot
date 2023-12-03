package com.justinyan.marketbot.domain.types

case class Position(cash: Double, netPosition: Long, participant: Participant) {
  override def toString = s"(cash=$cash, net_position=$netPosition, participant=$participant)"
}
