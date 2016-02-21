package com.betgenius

import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.Timeout
import com.betgenius.model._
import com.betgenius.repository.ActorModule

import scala.concurrent.duration._
import scala.xml.NodeSeq

/**
  * Created by douglas on 06/02/16.
  */
trait BetGenius {
  this: ActorModule =>

  implicit val fixtureUnmarshaller = defaultNodeSeqUnmarshaller.map(UpdategramExtractor.fromXml(_))

  implicit val theTimeout = Timeout(5 seconds)

  val fixtureDataRoute = path("updategram") {
    get {
      complete {
        "house of fun"
      }
    } ~
      post {
        entity(as[UpdateGram]) { updategram =>
          complete {
            val flow = Source.single(updategram).via(updategramFlow)
            val result = flow.runWith(Sink.head[HttpResponse])
            result
          }
        }
      }
  } ~ path("heartbeat") {
    post {
      entity(as[NodeSeq]) { seq => {
        complete {
          "got a heartbeat"
        }
      }

      }
    }
  }

}
