package com.betgenius

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.betgenius.Application._
import com.betgenius.EchoActor.EchoMessage
import com.betgenius.model.{Market, SportingFixture}
import com.betgenius.repository.ActorModule
import com.betgenius.repository.EntityManager.Persist

import scala.collection.mutable
import scala.concurrent.duration._
import scala.xml.NodeSeq

/**
  * Created by douglas on 06/02/16.
  */
trait BetGenius  {
  this:ActorModule =>

  implicit val fixtureUnmarshaller = defaultNodeSeqUnmarshaller.map(toFixture(_))

  implicit val theTimeout = Timeout(5 seconds)


  def toFixture(nodeSeq:NodeSeq) :SportingFixture = nodeSeq match {
    case fixture @ <SportingFixture>{markets @ _*}</SportingFixture> => val buffer = mutable.MutableList[Market]()
      for(m @ <Market>{_*}</Market> <- markets){
          buffer += Market((m \ "@name").text)
    }
      SportingFixture((fixture \ "@name").text,buffer.toSeq)
  }

  val fixtureDataRoute = pathPrefix("betgenius") {
    get {
      path("tony") {
        complete {
          "hello world"
        }
      } ~
        path("ping") {
          complete {
            "pong"
          }
        } ~
        path("crash") {
          complete {
            "crash"
          }
        }
    }
  } ~
    pathSingleSlash {
      get {
        complete {
          (echoActor ? EchoMessage("joe")).mapTo[String]
        }
      } ~
      post {
         entity(as[SportingFixture]) { fixture =>
             complete {
                 s"got the entity ${fixture.name} with market ${fixture.markets(0).name} ${fixture.markets(1).name}"
               (entityActor ? Persist(fixture)).mapTo[String]
             }
         }
      }
    }

}
