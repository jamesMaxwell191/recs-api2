package com.betgenius.model

import akka.http.javadsl.server.Unmarshaller

/**
  * Created by douglas on 06/02/16.
  */
case class SportingFixture(name:String , markets:Seq[Market]) {

}

case class Market(name:String)

