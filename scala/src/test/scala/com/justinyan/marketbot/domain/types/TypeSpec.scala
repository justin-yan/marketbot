package com.justinyan.marketbot.domain.types

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TypeSpec extends AnyFlatSpec with Matchers {

  "MarketBot Types" should "Display Correctly" in {
    println(Participant("test").toString)
    assert(true)
  }
}
