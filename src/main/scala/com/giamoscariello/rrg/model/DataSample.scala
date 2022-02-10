package com.giamoscariello.rrg.model

import io.circe.Decoder.Result
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.{Decoder, HCursor, Json}
import org.mongodb.scala.bson.BsonDocument

import scala.util.Random

case class DataSample(list: Seq[String]){
  def randomize(): Option[String] = Random.shuffle(list).headOption
}

object DataSample {
  implicit lazy val decoder: Decoder[DataSample] = (c: HCursor) =>
    c.downField("list").as[Array[String]]
      .map(list => DataSample(list))

  implicit val names: Name = Name()

  implicit val surnames: Surname = Surname()

  implicit val locations: Location = Location()
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

