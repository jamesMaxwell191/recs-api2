package com.betgenius.module

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink}
import akka.stream.{ActorMaterializer, FlowShape}
import akka.util.Timeout
import com.betgenius.EchoActor
import com.betgenius.model.{PersistenceResult, UpdateGram}
import com.betgenius.repository.EntityManager
import com.betgenius.repository.EntityManager.Persist

import scala.concurrent.duration._

/**
  * Created by douglas on 13/02/16.
  */
trait ActorModule {

  implicit val actorSystem = ActorSystem("recs")

  implicit val materializer = ActorMaterializer()

  implicit val timeout = Timeout(5 seconds)

  lazy val gtpRouter = actorSystem.actorOf(RoundRobinPool(20).props(EchoActor.props).withDispatcher("echo-dispatcher"), "gtpRouter")

  lazy val entityActor = actorSystem.actorOf(RoundRobinPool(5).props(EntityManager.props), "entityRouter")

  lazy val broadcastPersistenceResult =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val broadcast = b.add(Broadcast[PersistenceResult](2))
      broadcast.out(0).map(convertPersistentEntity(_)) ~> Sink.actorRef(gtpRouter,"gtpRouter")
      FlowShape(broadcast.in, broadcast.out(1))
    })


  lazy val updategramFlow:Flow[UpdateGram,HttpResponse,NotUsed] = Flow[UpdateGram].mapAsync(2){
    case ug @ UpdateGram(_,_,_) => (entityActor ? Persist(ug)).mapTo[PersistenceResult]
  }.via(broadcastPersistenceResult).map(f => HttpResponse(200, entity="successfully persisted the fixture"))

  def convertPersistentEntity(result:PersistenceResult) ={
      "converted"
  }

}


