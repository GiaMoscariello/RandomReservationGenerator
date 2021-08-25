package com.giamoscariello.rrg.repository.mongo

import cats.effect.{ContextShift, IO}
import com.giamoscariello.rrg.model.DataSample
import org.bson.BsonDocument
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoCollection}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

case class MongoStore(collection: MongoCollection[Document]) {
  val ec: ExecutionContextExecutor = ExecutionContext.global
  val cs: ContextShift[IO] = IO.contextShift(ec)

  def dataSamples: IO[List[DataSample]] = {
    val dbExecutionContext = ExecutionContext.global
    implicit val contextShift: ContextShift[IO] = IO.contextShift(dbExecutionContext)
    IO.fromFuture(findAllDataType)
  }

  def findAllDataType: IO[Future[List[DataSample]]] =
    IO.delay(for {
      names <- findDataTypeBy("names")
      surnames <- findDataTypeBy("surnames")
      locations <- findDataTypeBy("locations")
    } yield List(names, surnames, locations))

  def findDataTypeBy(filter: String): Future[DataSample] = {
    (for {
      docs <- collection.find[BsonDocument](equal("dataType", filter))
      data = DataSample(docs)
    } yield data).head

  }
}
