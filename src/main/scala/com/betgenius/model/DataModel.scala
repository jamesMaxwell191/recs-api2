package com.betgenius.model

import java.util.UUID

import org.joda.time.DateTime

import scala.xml.NodeSeq


object ModelHelper {
     def idNamePair(node:NodeSeq) = {
       (((node \ "Id").text), (node \ "Name").text)
     }
}


object UpdategramExtractor {
     def fromXml(node:NodeSeq) = {
       val header = HeaderExtractor.fromXml(node \ "Header")
       val cmdOption = CreateFixtureCmdExtractor.fromXml(node \ "CreateFixtureCmd")
       UpdateGram(header,cmdOption, None)
     }
}

object HeaderExtractor {
     def fromXml(node:NodeSeq) = {
        val uuid = UUID.fromString( (node \ "MessageId").text)
        val dateTime = DateTime.parse((node \ "DateTime").text)
        val retry = (node \ "Retry").text.toInt
        Header(uuid,dateTime,retry)
     }
}

object CreateFixtureCmdExtractor {
     def fromXml(node:NodeSeq) = {
         val elem = node \ "SportsFixture"
         if(elem.size == 0) None else Some(CreateFixtureCmd(SportingFixtureExtractor.fromXml(elem)))
     }
}


object SportingFixtureExtractor {
  def fromXml(node: NodeSeq) = {
    val (id,name) = ModelHelper.idNamePair(node)
    SportsFixture(id, name,SportExtractor.fromXml(node \ "Sport"),CompetitionExtractor.fromXml(node \ "Competition"))
  }
}

object SportExtractor {
     def fromXml(node:NodeSeq) = {
        val (id,name) = ModelHelper.idNamePair(node)
        Sport(id,name)
     }
}

object CompetitionExtractor {
  def fromXml(node:NodeSeq) = {
    val (id,name) = ModelHelper.idNamePair(node)
    Competition(id,name)
  }
}

object MarketSetExtractor {
    def fromXml(node:NodeSeq) ={

        val items = (List[Market]() /: (node \ "Market")){ (l,m) =>
            MarketExtractor.fromXml(m) :: l
        }
        new MarketSet(items)
    }
}

object MarketExtractor {
     def fromXml(node:NodeSeq) ={
         val (id,name) = ModelHelper.idNamePair(node)
         Market(id,name)
     }
}