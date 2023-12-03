[![Build Status](https://travis-ci.org/justin-yan/marketbot.svg?branch=master)](https://travis-ci.org/justin-yan/marketbot)[![codecov](https://codecov.io/gh/justin-yan/marketbot/branch/master/graph/badge.svg)](https://codecov.io/gh/justin-yan/marketbot)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=marketbot&metric=alert_status)](https://sonarcloud.io/dashboard?id=marketbot)

# marketbot

Marketbot makes it easy to run basic markets for private use, such as having a prediction market in slack.

## Quickstart

- sbt assembly

## Domain

- An `Asset` is a unit of trade, such as `farms_in_the_us` or `S&P500_index`.
- `Participant`s trade assets.
- A `Market` is the venue of trade for a specific `Asset`.
- Participants can place `Order`s such as `Bid` and `Offer` that can be made to buy and sell assets (respectively), and must specify a `Price` and a `Quantity`.
    - There are a variety of other orders that can be supported, such as `Hit`, `Lift`.
    - Orders can have a variety of other features, such as `synthetic-shorts`, `fill-or-kill`, `all-or-nothing`.
- A `Trade` is consummated at a particular price and for a particular quantity when a bid and an offer `Cross`.
- A market maintains all outstanding positions and open orders in the `PositionBook` and `OrderBook` respectively.
- A market can be `Settled`, at which point all open positions are immediately `Closed` at the current valuation of the asset, as specified by the market's administrator.
    - As an example, if the `S&P500_index` market is settled, then the current price of the S&P500 would serve as the final price at which all long and short positions are closed out.
- Marketbot runs the `Exchange`, which manages the full list of all available markets.

## Developer

## User

## Roadmap

### Slackslash

What are the core commands for V1?

```
/marketbot (help)
/mstart (TICKER)
/msettle (TICKER, PRICE)
/bid (TICKER, PRICE, QTY:opt)
/ask (TICKER, PRICE, QTY:opt)
/clr (TICKER, ORDER_ID:opt)
/ord (TICKER, PARTICIPANT_ID:opt)
/pos (TICKER, PARTICIPANT_ID:opt)
```
