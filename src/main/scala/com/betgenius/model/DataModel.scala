package com.betgenius.model
import scala.xml.NodeSeq


object SportingFixture {
  def fromXml(node: NodeSeq) = {
    println("extracting a sporting fixture")
    val fixture = node \ "SportsFixture"

    if(fixture.length == 0) throw new IllegalArgumentException

    val id = (fixture \ "Id").text
    val name = (fixture \ "Name").text

    SportsFixture(id, name, None)
  }
}

object MarketSetExtractor {
    def fromXml(node:NodeSeq) ={
        println("extracting a market set")
        val marketSet = node \ "MarketSet"

        if(marketSet.length == 0) throw new IllegalArgumentException

        val items = (List[Market]() /: (marketSet \ "Market")){ (l,m) =>
            MarketExtractor.fromXml(m) :: l
        }
        new MarketSet(items)
    }
}

object MarketExtractor {
     def fromXml(node:NodeSeq) ={
         Market((node \ "Name").text)
     }
}