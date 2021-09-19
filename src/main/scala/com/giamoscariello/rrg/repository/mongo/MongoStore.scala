package com.giamoscariello.rrg.repository.mongo

import cats.effect.{ContextShift, IO}
import com.giamoscariello.rrg.model.DataSample
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.slf4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


//TODO: There is a lot of refactor available here
case class MongoStore(client: MongoClient)(implicit logger: Logger) {

  def getAllDataSamples: IO[Seq[DataSample]] = {
    val dbExecutionContext = ExecutionContext.global
    implicit val contextShift: ContextShift[IO] = IO.contextShift(dbExecutionContext)

    IO.fromFuture(findAll())
  }

  private def findAll(): IO[Future[Seq[DataSample]]] = {
      IO.delay(for {
        all <- collection.find[BsonDocument].toFuture
        list = all.map (doc => DataSample(doc))
      } yield list)
  }

  private def collection: MongoCollection[BsonDocument] =
    client.getDatabase("RandomReservationGens").getCollection("DataSamples")
}
