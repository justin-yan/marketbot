package com.justinyan.marketbot.domain

import com.justinyan.marketbot.domain.types.Order.OrderId
import com.justinyan.marketbot.domain.types._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class Market() {
  var bids: mutable.PriorityQueue[Bid]                    = mutable.PriorityQueue.empty[Bid]
  var asks: mutable.PriorityQueue[Ask]                    = mutable.PriorityQueue.empty[Ask]
  var positions: mutable.Map[Participant, PositionAmount] = new mutable.HashMap().withDefaultValue(PositionAmount(0, 0))
  var lastTradePrice                                      = 0.0
  var currentTicker: Option[Ticker]                       = Option.empty // Should be present if a market cannot be started

  private def zeroMarket(): Unit = {
    bids.clear()
    asks.clear()
    positions.clear()
    lastTradePrice = 0.0
    currentTicker = Option.empty
  }

  private def marketOpen: Boolean = currentTicker.isDefined

  def startMarket(ticker: Ticker): Try[Ticker] = {
    if (!marketOpen) {
      currentTicker = Option(ticker)
      Success(ticker)
    } else {
      Failure(new Exception("TODO"))
    }
  }

  def settleMarket(ticker: Ticker, finalPrice: Double): Try[PositionBook] = {
    if (marketOpen) {
      val finalPos = PositionBook(positions.map(tup => Position(tup._2.netPosition * finalPrice + tup._2.cash, 0, tup._1)).toList)
      zeroMarket()
      return Success(finalPos)
    } else {
      Failure(new Exception("TODO"))
    }
  }

  private def transact(price: Double, quantity: Long, buyer: Participant, seller: Participant): Unit = {
    val buyerPos = positions(buyer)
    positions(buyer) = PositionAmount(buyerPos.cash - price * quantity, buyerPos.netPosition + quantity)
    val sellerPos = positions(seller)
    positions(seller) = PositionAmount(sellerPos.cash + price * quantity, sellerPos.netPosition - quantity)
  }

  private def peekBestBid(): Option[Bid] = bids.headOption

  private def clearBestBid(): Try[Bid] = Try(bids.dequeue())

  private def peekBestAsk(): Option[Ask] = asks.headOption

  private def clearBestAsk(): Try[Ask] = Try(asks.dequeue())

  def bid(ticker: Ticker, participant: Participant, price: Double): Try[OrderId] = bid(ticker, participant, price, 1)

  def bid(ticker: Ticker, participant: Participant, price: Double, quantity: Long): Try[OrderId] = {
    var orderQuantity = quantity
    var optCurAsk     = peekBestAsk()
    while (optCurAsk.isDefined && optCurAsk.get.price <= price) {
      var curAsk         = optCurAsk.get
      var curAskQuantity = curAsk.quantity
      var tradeQuantity  = math.min(orderQuantity, curAskQuantity)
      transact(curAsk.price, tradeQuantity, participant, curAsk.participant)
      lastTradePrice = curAsk.price
      orderQuantity -= tradeQuantity
      curAskQuantity -= tradeQuantity
      clearBestAsk()
      if (curAskQuantity > 0) {
        asks.addOne(Ask(curAsk.price, curAskQuantity, curAsk.participant, curAsk.oid))
      }
      if (orderQuantity == 0) {
        return Success("")
      }
      optCurAsk = peekBestAsk()
    }
    bids.addOne(Bid(price, orderQuantity, participant, ""))
    Success("")
  }

  def ask(ticker: Ticker, participant: Participant, price: Double): Try[OrderId] = ask(ticker, participant, price, 1)

  def ask(ticker: Ticker, participant: Participant, price: Double, quantity: Long): Try[OrderId] = {
    var orderQuantity = quantity
    var optCurBid     = peekBestBid()
    while (optCurBid.isDefined && optCurBid.get.price >= price) {
      var curBid         = optCurBid.get
      var curBidQuantity = curBid.quantity
      var tradeQuantity  = math.min(orderQuantity, curBid.quantity)
      transact(curBid.price, tradeQuantity, participant, curBid.participant)
      lastTradePrice = curBid.price
      orderQuantity -= tradeQuantity
      curBidQuantity -= tradeQuantity
      clearBestBid()
      if (curBidQuantity > 0) {
        bids.addOne(Bid(curBid.price, curBidQuantity, curBid.participant, curBid.oid))
      }
      if (orderQuantity == 0) {
        return Success("")
      }
      optCurBid = peekBestBid()
    }
    asks.addOne(Ask(price, orderQuantity, participant, ""))
    Success("")
  }

  def clear(ticker: Ticker, participant: Participant): Try[OrderBook] = {
    bids = bids.filterNot(bid => bid.participant.id == participant.id)
    asks = asks.filterNot(ask => ask.participant.id == participant.id)
    Success(OrderBook(bids.toList, asks.toList))
  }

  def listOrders(ticker: Ticker): Try[OrderBook] = Success(OrderBook(bids.toList, asks.toList))

  def listOrders(ticker: Ticker, participant: Participant): Try[OrderBook] = {
    Success(
      OrderBook(
        bids.filter(bid => bid.participant.id == participant.id).toList,
        asks.filter(ask => ask.participant.id == participant.id).toList
      )
    )
  }

  def listPositions(ticker: Ticker): Try[PositionBook] =
    Success(PositionBook(positions.map(tup => Position(tup._2.cash, tup._2.netPosition, tup._1)).toList))

  def listPositions(ticker: Ticker, participant: Participant): Try[PositionBook] = {
    Success(
      PositionBook(
        positions
          .filter(tup => tup._1.id == participant.id)
          .map(tup => Position(tup._2.cash, tup._2.netPosition, tup._1))
          .toList
      )
    )
  }
}
