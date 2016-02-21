package com.betgenius

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import akka.pattern.ask
import com.betgenius.model.{MarketSet, MarketSetExtractor, SportingFixture, SportsFixture}
import com.betgenius.repository.EntityManager.Persist
import com.betgenius.repository.{EntityManager, ActorModule}

import scala.concurrent.duration._

/**
  * Created by douglas on 06/02/16.
  */
trait BetGenius {

  implicit val fixtureUnmarshaller = defaultNodeSeqUnmarshaller.map(SportingFixture.fromXml(_))

  implicit val marketSetUnmarshaller = defaultNodeSeqUnmarshaller.map(MarketSetExtractor.fromXml(_))

  implicit val theTimeout = Timeout(5 seconds)

  val fixtureDataRoute = path("marketUpdate") {
      get {
        complete {
           "house of fun"
        }
      } ~
      post {
         entity(as[SportsFixture]) { fixture =>
             complete {
                 val request = actorSystem.actorOf(EntityManager.props)
                (request ? Persist(fixture)).mapTo[String]
             }
         }
      } ~
      post {
        entity(as[MarketSet]) { markets =>
          complete {
            s"posted a market set with ${markets.markets.size} markets"
          }
        }
      }
    }

  val persistFixtureFlow = Flow[SportsFixture].map(validate(_))

  def validate(fixture:SportsFixture) = fixture

}
