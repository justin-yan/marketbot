package com.justinyan.marketbot

import pureconfig.loadConfig
import pureconfig.generic.auto._

case class MarketbotConfig()
object MarketbotConfig {
  def load(): MarketbotConfig = loadConfig[MarketbotConfig] match {
    case Right(config) => config
    case Left(error) =>
      throw new RuntimeException("Cannot read config file, errors:\n" + error.toList.mkString("\n"))
  }
}
