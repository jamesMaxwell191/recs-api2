package com.betgenius.model

/**
  * Created by douglas on 13/02/16.
  */
case class SportsFixture(id:String,name:String,marketSet:Option[Seq[Market]]) {

}

case class MarketSet(markets:Seq[Market])


case class Market(name:String)
