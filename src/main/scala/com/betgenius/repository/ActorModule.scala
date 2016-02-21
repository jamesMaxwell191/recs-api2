package com.betgenius.repository

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.model.{HttpResponse, Uri, HttpMethods, HttpRequest}
import akka.routing.RoundRobinPool
import akka.stream.{FlowShape, ActorMaterializer}
import akka.stream.scaladsl.{Sink, Broadcast, GraphDSL, Flow}
import akka.util.Timeout
import com.betgenius.EchoActor
import com.betgenius.model.{UpdateGram, PersistenceResult}
import com.betgenius.repository.EntityManager.Persist
import akka.pattern.ask
import scala.concurrent.duration._

/**
  * Created by douglas on 13/02/16.
  */
trait ActorModule {

  implicit val actorSystem = ActorSystem("recs")

  implicit val materializer = ActorMaterializer()

  implicit val timeout = Timeout(10 seconds)

  lazy val echoActor = actorSystem.actorOf(RoundRobinPool(20).props(EchoActor.props).withDispatcher("echo-dispatcher"), "echoRouter")

  lazy val entityActor = actorSystem.actorOf(RoundRobinPool(5).props(EntityManager.props), "entityRouter")

  val broadcastPersistenceResult =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val broadcast = b.add(Broadcast[PersistenceResult](2))
      broadcast.out(0).map(convertPersistentEntity(_)) ~> Sink.actorRef(echoActor,"gtpRouter")
      FlowShape(broadcast.in, broadcast.out(1))
    })


  val updategramFlow = Flow[UpdateGram].mapAsync(2){
    case ug @ UpdateGram(_,_) => (entityActor ? Persist(ug)).mapTo[PersistenceResult]
  }.via(broadcastPersistenceResult).map(f => HttpResponse(200, entity="successfully persisted the fixture"))

  def convertPersistentEntity(result:PersistenceResult) ={
      "converted"
  }

}


