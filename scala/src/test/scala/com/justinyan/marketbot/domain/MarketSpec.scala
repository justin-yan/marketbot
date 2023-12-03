package com.justinyan.marketbot.domain

import com.justinyan.marketbot.domain.types.Participant
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MarketSpec extends AnyFlatSpec with Matchers with BeforeAndAfter {

  var market: Market = _

  before {
    market = new Market()
  }

  "A Market" should "Instantiate cleanly from base slate" in {
    assert(market.startMarket("TEST").isSuccess)
  }

  "A Market" should "Fail instantiation on existing market" in {
    market.startMarket("TEST")
    assert(market.startMarket("TEST2").isFailure)
  }

  "A Market" should "Settle cleanly on open market" in {
    market.startMarket("TEST")
    assert(market.settleMarket("TEST", 0).isSuccess)
  }

  "A Market" should "Fail to settle with no existing market" in {
    assert(market.settleMarket("TEST", 0).isFailure)
  }

  "A Market" should "Instantiate cleanly once previous market is settled" in {
    market.startMarket("TEST")
    market.settleMarket("TEST", 0)
    assert(market.startMarket("TEST2").isSuccess)
  }

  "A Market" should "Transact a single bid and ask properly" in {
    market.startMarket("TEST")
    market.bid("TEST", Participant("justin1"), 1)
    market.ask("TEST", Participant("justin2"), 1)
    val orderList = market.listOrders("TEST")
    assert(orderList.get.asks.isEmpty)
    assert(orderList.get.bids.isEmpty)
    val positionList = market.listPositions("TEST")
    assert(positionList.get.positions.length == 2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin1").head.netPosition == 1)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin1").head.cash == -1)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin2").head.netPosition == -1)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin2").head.cash == 1)
  }

  "A Market" should "Transact two bids and a single ask properly" in {
    market.startMarket("TEST")
    market.bid("TEST", Participant("justin1"), 2)
    market.bid("TEST", Participant("justin1"), 3)
    market.ask("TEST", Participant("justin2"), 1, 2)
    val orderList = market.listOrders("TEST")
    assert(orderList.get.asks.isEmpty)
    assert(orderList.get.bids.isEmpty)
    val positionList = market.listPositions("TEST")
    assert(positionList.get.positions.length == 2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin1").head.netPosition == 2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin1").head.cash == -5)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin2").head.netPosition == -2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin2").head.cash == 5)
  }

  "A Market" should "Transact two bids and a single ask with remainder properly" in {
    market.startMarket("TEST")
    market.bid("TEST", Participant("justin1"), 2)
    market.bid("TEST", Participant("justin1"), 3)
    market.ask("TEST", Participant("justin2"), 1, 3)
    val orderList = market.listOrders("TEST")
    assert(orderList.get.asks.length == 1)
    assert(orderList.get.asks.head.quantity == 1)
    assert(orderList.get.bids.isEmpty)
    val positionList = market.listPositions("TEST")
    assert(positionList.get.positions.length == 2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin1").head.netPosition == 2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin1").head.cash == -5)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin2").head.netPosition == -2)
    assert(positionList.get.positions.filterNot(pos => pos.participant.id == "justin2").head.cash == 5)
  }

  "A Market" should "clear bids and asks properly" in {
    market.startMarket("TEST")
    market.bid("TEST", Participant("justin1"), 2)
    market.bid("TEST", Participant("justin1"), 3)
    market.ask("TEST", Participant("justin1"), 7)
    market.ask("TEST", Participant("justin2"), 6, 3)
    market.clear("TEST", Participant("justin1"))
    val orderList = market.listOrders("TEST")
    assert(orderList.get.asks.length == 1)
    assert(orderList.get.bids.isEmpty)
  }
}
