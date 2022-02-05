package com.giamoscariello.rrg.repository.mongo

import cats.effect.{ContextShift, IO}
import com.giamoscariello.rrg.model.{DataSampled, DataType}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.slf4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

case class MongoStore(client: MongoClient)(implicit logger: Logger) {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val collection: MongoCollection[BsonDocument] =
    client
      .getDatabase("RandomReservationGens")
      .getCollection("DataSamples")

  def queryCollectionForDataType[T <: DataType](implicit dataType: T): IO[DataSampled[String]] = {
     IO.fromFuture(
       IO(collection
        .find(equal("dataType", dataType.id))
        .projection(Projections.fields(Projections.include("list"), Projections.excludeId()))
        .head()
       .recoverWith { case e: Throwable => logger error e.getMessage; Future.failed(e) }
       .map[DataSampled[String]](doc => DataSampled(doc))
     ))
  }
}
