package com.betgenius.routes

import akka.NotUsed
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Flow
import com.betgenius.BetGenius
import com.betgenius.controllers.{UpdateGramControllerIf, UpdateGramController}
import com.betgenius.model.{UpdateGram, MarketSetExtractor, SportingFixtureExtractor}
import com.betgenius.module.{ControllerModule, ActorModule}
import com.softwaremill.macwire._
import org.scalatest.{Matchers, WordSpec}
import org.scalamock.scalatest.MockFactory

/**
  * Created by douglas on 05/02/16.
  */
class RoutesTest extends WordSpec with Matchers with ScalatestRouteTest with BetGenius with MockFactory {

  override val datagramHandler = mock[UpdateGramControllerIf]

  "the main route" should {
    "respond with hello world" in {
      val heartbeat = HttpEntity(ContentTypes.`text/xml(UTF-8)`, s"<?xml version='1.0' ?><HeartBeat><Id>123</Id><Name>Hibs vs Hearts</Name></HeartBeat>")
      Post("/heartbeat", heartbeat) ~> fixtureDataRoute ~> check {
        responseAs[String] shouldEqual "got a heartbeat"
      }
    }
    "extract a fixture" in {
      datagramHandler
      val xml = HttpEntity(ContentTypes.`text/xml(UTF-8)`, s"<?xml version='1.0' ?><UpdateGram><CreateFixtureCmd><SportsFixture><Id>123</Id><Name>Hibs vs Hearts</Name></SportsFixture></CreateFixtureCmd></UpdateGram>")
      Post("/updategram", xml) ~> fixtureDataRoute ~> check {
        responseAs[String] shouldEqual "posted a fixture Hibs vs Rangers"
      }
    }
    //    "extract a market set" in {
    //      val xml = HttpEntity(ContentTypes.`text/xml(UTF-8)`, s"<?xml version='1.0' ?><Feed><MarketSet><Market><Name>Win Draw Win</Name></Market><Market><Name>Match Winner</Name></Market></MarketSet></Feed>")
    //      Post("/marketUpdate", xml) ~> fixtureDataRoute -> check {
    //        responseAs[String] shouldEqual "posted a market set with 2 markets"
    //      }
    //    }

  }


}
