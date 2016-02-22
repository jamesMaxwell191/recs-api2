package com.betgenius.routes

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.betgenius.BetGenius
import com.betgenius.model.{MarketSetExtractor, SportingFixtureExtractor}
import com.betgenius.module.ActorModule
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by douglas on 05/02/16.
  */
class RoutesTest extends WordSpec with Matchers with ScalatestRouteTest with BetGenius with ActorModule {

  "the main route" should {
    "respond with hello world" in {
      val heartbeat = HttpEntity(ContentTypes.`text/xml(UTF-8)`, s"<?xml version='1.0' ?><HeartBeat><Id>123</Id><Name>Hibs vs Hearts</Name></HeartBeat>")
      Post("/heartbeat", heartbeat) ~> fixtureDataRoute ~> check {
        responseAs[String] shouldEqual "house of fun"
      }
    }
    //    "extract a fixture" in {
    //      val xml = HttpEntity(ContentTypes.`text/xml(UTF-8)`, s"<?xml version='1.0' ?><Feed><SportsFixture><Id>123</Id><Name>Hibs vs Hearts</Name></SportsFixture></Feed>")
    //      Post("/marketUpdate", xml) ~> fixtureDataRoute -> check {
    //        responseAs[String] shouldEqual "posted a fixture Hibs vs Hearts"
    //      }
    //    }
    //    "extract a market set" in {
    //      val xml = HttpEntity(ContentTypes.`text/xml(UTF-8)`, s"<?xml version='1.0' ?><Feed><MarketSet><Market><Name>Win Draw Win</Name></Market><Market><Name>Match Winner</Name></Market></MarketSet></Feed>")
    //      Post("/marketUpdate", xml) ~> fixtureDataRoute -> check {
    //        responseAs[String] shouldEqual "posted a market set with 2 markets"
    //      }
    //    }

  }


}
