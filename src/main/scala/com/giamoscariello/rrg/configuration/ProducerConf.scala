package com.giamoscariello.rrg.configuration

import cats.effect.{IO, Resource, Sync}
import com.giamoscariello.rrg.model.{Key, Reservation}
import com.giamoscariello.rrg.service.producer.HelperSerdes
import io.circe.generic.auto._
import io.circe.syntax._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Serializer

import scala.jdk.CollectionConverters._
import scala.language.postfixOps

case class ProducerConf(acks: Int,
                        clientId: Option[String],
                        compressionType: String,
                        bootstrapServer: List[String]
                       )

object KafkaReservationProducer extends HelperSerdes {

  def createKafkaProducer[F[_]](implicit F: Sync[IO]): IO[KafkaProducer[Key, Reservation]] = {
      F.delay{
        val config: Config = ConfigFactory.load("kafka.conf")
        val configMap = config.
          entrySet()
          .asScala
          .map(pair => (pair.getKey, config.getAnyRef(pair.getKey)))
          .toMap

        val keySerializer: Serializer[Key] = reflectionAvroSerializer4S[Key]
        val reservationsSerializer: Serializer[Reservation] = reflectionAvroSerializer4S[Reservation]

        reservationsSerializer.configure(configMap asJava, false)
        keySerializer.configure(configMap asJava, true )

        new KafkaProducer[Key, Reservation](
          configMap asJava,
          keySerializer,
          reservationsSerializer)
      }
  }
}