package com.giamoscariello.rrg.service

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.giamoscariello.rrg.configuration.KafkaReservationProducer
import com.giamoscariello.rrg.configuration.KafkaReservationProducer.{keyFormat, reflectionAvroSerializer4S, tvShowFormat}
import com.giamoscariello.rrg.model.{Key, Reservation}
import com.giamoscariello.rrg.repository.mongo.{MongoDB, MongoStore}
import com.giamoscariello.rrg.service.generator.GenerateRandomReservation
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.Serializer

import scala.jdk.CollectionConverters._
import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.jdk.javaapi.CollectionConverters
import scala.jdk.javaapi.CollectionConverters.asJava
import scala.util.Random
import scala.language.postfixOps

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    program
      .use(_ => IO.unit)
      .as(ExitCode.Success)
  }

  private def program: Resource[IO, Unit] = {
    for {
      client    <- MongoDB.makeClient
      // TODO: Can use exstension method here?
      collection = client.getDatabase("RandomReservationGens").getCollection("DataSamples")
      mongoStore = MongoStore(collection)
      _ <- Resource.eval {
        for {
          samples     <- mongoStore.dataSamples
          producer <- KafkaReservationProducer.createKafkaProducer[IO]
          generator = GenerateRandomReservation(samples)
          _ = {
            for (_ <- 1 to 100) {
              val reservation = Await.result(generator.makeReservation, 1.seconds)
              println(reservation.asJson)
              val record = new ProducerRecord[Key, Reservation]("reservationsMade", Key(Random.nextString(99)), reservation)
              producer send record
            }
            producer.flush()
            producer.close()
          }
        } yield ()
      }
    } yield ()
  }
}
