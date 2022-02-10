package com.giamoscariello.rrg.configuration

import cats.effect.{IO, Resource}
import com.giamoscariello.rrg.model.{Key, Reservation}
import com.giamoscariello.rrg.repository.mongo.MongoDB
import com.giamoscariello.rrg.service.producer.KafkaReservationProducer
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.KafkaProducer
import org.mongodb.scala.MongoClient
import org.slf4j.Logger

case class Dependencies(mongoClient: MongoClient, kafkaProducer: KafkaProducer[Key, Reservation])

object Dependencies {
  def make(conf: Config)(implicit logger: Logger): Resource[IO, Dependencies] =
    for {
      mongoClient     <- MongoDB.makeMongoClient(conf)
      kafkaProducer   <- KafkaReservationProducer.makeKafkaProducer(conf)
    } yield Dependencies(mongoClient, kafkaProducer)
}