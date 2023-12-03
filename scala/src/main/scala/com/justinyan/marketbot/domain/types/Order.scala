package com.justinyan.marketbot.domain.types

import com.justinyan.marketbot.domain.types.Order.OrderId

sealed trait Order {
  def price: Double
  def quantity: Long
  def participant: Participant
  def oid: OrderId
  override def toString = s"(orderId=$oid, price=$price, quantity=$quantity, participant=$participant)"
}

case class Ask(price: Double, quantity: Long, participant: Participant, oid: OrderId) extends Order with Ordered[Ask] {
  def compare(that: Ask): Int = that.price compare this.price // Want MinHeap for asks, so larger asks should compare less favorably
}

case class Bid(price: Double, quantity: Long, participant: Participant, oid: OrderId) extends Order with Ordered[Bid] {
  def compare(that: Bid): Int = this.price compare that.price // Want MaxHeap for bids, so larger bids should compare favorably
}

object Order {
  type OrderId = String
}
