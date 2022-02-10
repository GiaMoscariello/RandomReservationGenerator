package com.giamoscariello.rrg.service

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import com.giamoscariello.rrg.configuration.Dependencies
import com.giamoscariello.rrg.model.DataSample.{locations, names, surnames}
import com.giamoscariello.rrg.model._
import com.giamoscariello.rrg.repository.mongo.MongoStore
import com.giamoscariello.rrg.service.generator.ReservationListGenerator
import com.giamoscariello.rrg.service.producer.KafkaSender
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.{Logger, LoggerFactory}

import scala.language.postfixOps

object App extends IOApp {
  implicit val logger: Logger = LoggerFactory.getLogger(this.getClass.getName)

  val program: Config => IO[List[RecordMetadata]] = (conf: Config) => Dependencies
    .make(conf)
    .use {
      deps =>
        val mongoStore = MongoStore(deps.mongoClient)
        val kafkaSender = KafkaSender(deps.kafkaProducer)
        (for {
          nameData      <- mongoStore.queryCollectionFor[Name]
          surnameData   <- mongoStore.queryCollectionFor[Surname]
          locationData  <- mongoStore.queryCollectionFor[Location]
          comp = for {
            names <- nameData
            surnames <- surnameData
            locations <- locationData
            randomReservations = ReservationListGenerator(names, surnames, locations, 100).make()
          } yield randomReservations
        } yield comp).flatMap {
          case Left(t) => IO.raiseError(t)
          case Right(records) => kafkaSender.sendRecords(records, "reservationsMade")
        }
    }
  val computation = Kleisli[IO, Config, List[RecordMetadata]](program)

  override def run(args: List[String]): IO[ExitCode] = {
    val config = ConfigFactory.load("application.conf")

    Kleisli[IO, Config, List[RecordMetadata]](program)
      .run(config) *> IO{ExitCode.Success}
  }
}