package com.justinyan.marketbot

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Marketbot extends App {
  implicit val system           = ActorSystem("marketbot-server")
  implicit val executionContext = system.dispatcher
  implicit val materializer     = ActorMaterializer()

  val config = MarketbotConfig.load()
}
