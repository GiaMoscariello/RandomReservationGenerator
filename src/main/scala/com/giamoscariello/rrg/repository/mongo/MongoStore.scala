package com.giamoscariello.rrg.repository.mongo

import cats.effect.{ContextShift, IO}
import com.giamoscariello.rrg.model.{DataSample, DataType}
import io.circe.parser
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.slf4j.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

case class MongoStore(client: MongoClient)(implicit logger: Logger) {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val collection: MongoCollection[BsonDocument] =
    client
      .getDatabase("RandomReservationGens")
      .getCollection("DataSamples")

  def queryCollectionFor[T <: DataType](implicit dataType: T): IO[Either[Throwable, DataSample]] = {
    for {
      doc         <- getDataTypeDoc(collection)
      dataSample  <- toDataSample(doc)
    } yield dataSample
  }

  private def getDataTypeDoc[T <: DataType](collection: MongoCollection[BsonDocument])(implicit dataType: T): IO[BsonDocument] = {
    IO.fromFuture(
      IO.delay(collection
        .find(equal("dataType", dataType.id))
        .projection(Projections.fields(Projections.include("list"), Projections.excludeId()))
        .head()))
  }

  def toDataSample[T <: DataType](doc: BsonDocument): IO[Either[Throwable, DataSample]] = {
    IO {
      (for {
        json <- parser.parse(doc.toJson)
        data <- json.as[DataSample]
      } yield data) match {
        case Left(t)      => logger error t.getMessage; Left(t);
        case Right(data)  => logger debug s"Successful create data sample"; Right(data)
      }
    }
  }
}
