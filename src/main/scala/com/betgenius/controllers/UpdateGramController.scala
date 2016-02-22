package com.betgenius.controllers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.betgenius.model.UpdateGram
import com.betgenius.module.ActorModule

import scala.concurrent.Future
/**
  * Created by douglas on 21/02/16.
  */
class UpdateGramController(implicit val flow:Flow[UpdateGram,HttpResponse,NotUsed],implicit val mat:ActorMaterializer){

  def handle(updategram:UpdateGram):Future[HttpResponse] ={
    val theFlow = Source.single(updategram).via(flow)
    val result = theFlow.runWith(Sink.head[HttpResponse])
    result
  }

}

