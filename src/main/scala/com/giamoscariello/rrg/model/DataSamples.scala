package com.giamoscariello.rrg.model

import org.mongodb.scala.bson.{BsonDocument, BsonValue}
import shapeless.Lazy.apply

import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try

object DataSamples {
  val locations = Seq("Corallo", "Perla", "Napoli1", "Napoli2", "Napoli3")

  def from(doc: BsonDocument): Try[DataSample] = {
    Try {
      (for {
        dt           <- doc.get("dataType").map(toStringValue)
        list        <- doc.get("list").map(x => x.asArray.asScala.toSeq)
      } yield DataSample(dt, list.map(toStringValue))).value
    }
  }

  private def toStringValue(bson: BsonValue): String = bson.asString.getValue
}

case class DataSample(dataType: String, list: Seq[String])
