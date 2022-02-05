package com.giamoscariello.rrg.service

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.giamoscariello.rrg.configuration.Dependencies
import com.giamoscariello.rrg.model.DataSampled.{locations, names, surnames}
import com.giamoscariello.rrg.model.{Location, Name, Surname}
import com.giamoscariello.rrg.repository.mongo.{MongoDB, MongoStore}
import com.giamoscariello.rrg.service.generator.ReservationListGenerator
import com.giamoscariello.rrg.service.producer.{KafkaReservationProducer, KafkaSender}
import org.apache.kafka.streams.KafkaStreams
import org.slf4j.{Logger, LoggerFactory}

import scala.language.postfixOps

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
      implicit val logger: Logger = LoggerFactory.getLogger(this.getClass.getName)
      program.as(ExitCode.Success)
    }

  private def program(implicit logger: Logger) = {
    makeDeps.use {
      deps =>
        val mongoStore  = MongoStore(deps.mongoClient)
        val kafkaSender = KafkaSender(deps.kafkaProducer)

        for {
          names                 <- mongoStore.queryCollectionForDataType[Name]
          surnames              <- mongoStore.queryCollectionForDataType[Surname]
          locations             <- mongoStore.queryCollectionForDataType[Location]
          reservationRecords    = ReservationListGenerator(names, surnames, locations, 100).make()
        } yield reservationRecords
          .map(records => kafkaSender.sendRecords(records, "reservations"))
    }
  }

  private def makeDeps(implicit logger: Logger): Resource[IO, Dependencies] =
    for {
      mongoClient   <- MongoDB.makeMongoClient[IO]
      kafkaProducer <- KafkaReservationProducer.makeKafkaProducer[IO]
    } yield Dependencies(mongoClient, kafkaProducer)
}