package com.giamoscariello.rrg.service

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.giamoscariello.rrg.configuration.Dependencies
import com.giamoscariello.rrg.model.{Key, Reservation}
import com.giamoscariello.rrg.repository.mongo.{MongoDB, MongoStore}
import com.giamoscariello.rrg.service.generator.GenerateRandomReservation
import com.giamoscariello.rrg.service.producer.{KafkaReservationProducer, KafkaReservationSender}
import org.slf4j.{Logger, LoggerFactory}

import scala.language.postfixOps

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    {
      implicit val logger: Logger = LoggerFactory.getLogger(this.getClass.getName)
      program.as(ExitCode.Success)
    }

  private def program(implicit logger: Logger) = {
    makeDeps.use {
      deps =>
        for {
          dataSamples     <- MongoStore(deps.mongoClient).getAllDataSamples
          records         <- GenerateRandomReservation(dataSamples, Key.batchKeyList).mkReservationRecords
          _               = KafkaReservationSender(deps.kafkaProducer).sendRecords(records)
        } yield ()
    }
  }

  private def makeDeps(implicit logger: Logger): Resource[IO, Dependencies] =
    for {
      mongoClient   <- MongoDB.makeMongoClient[IO]
      kafkaProducer <- KafkaReservationProducer.makeKafkaProducer[IO]
    } yield Dependencies(mongoClient, kafkaProducer)
}