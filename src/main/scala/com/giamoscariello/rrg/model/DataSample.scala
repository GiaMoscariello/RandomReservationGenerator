package com.giamoscariello.rrg.model


import org.mongodb.scala.bson.{BsonDocument, BsonValue}

import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Success, Try}

case class DataSample(dataType: String, list: Seq[String])

object DataSample {
  def apply(doc: BsonDocument): DataSample = {
    Try {
      DataSample(
        toStringValue(doc.get("dataType")),
        doc.get("list").asArray.asScala.toList.map(x => x.asString().getValue))
    }.getOrElse(DataSample.empty)
  }

  val empty: DataSample = DataSample("empty", Seq())

  object Helper {
    implicit class ExtendBsonDocument(val value: BsonDocument) extends AnyVal {
      def asListScala: List[BsonValue] = value.asArray.asScala.toList
    }
  }

  private def toStringValue(bson: BsonValue): String = bson.asString.getValue
}
