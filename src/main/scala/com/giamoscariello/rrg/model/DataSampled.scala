package com.giamoscariello.rrg.model

import org.mongodb.scala.bson.BsonDocument

import scala.util.Random

case class DataSampled[String](list: Seq[String]){
  def randomize(): Option[String] = Random.shuffle(list).headOption
}

object DataSampled {
  import scala.jdk.CollectionConverters.CollectionHasAsScala

  implicit val names: Name = Name()

  implicit val surnames: Surname = Surname()

  implicit val locations: Location = Location()

  def apply (doc: BsonDocument): DataSampled[String] = {
    DataSampled(
      doc.get("list")
      .asArray
      .asScala
      .toList
      .map(x => x.asString().getValue)
    )
  }
}

trait DataType{
  val id: String
}

 case class Name() extends DataType {
  override val id: String = "names"
}
case class Surname() extends DataType {
  override val id: String = "surnames"
}
case class Location() extends DataType {
  override val id: String = "locations"
}

