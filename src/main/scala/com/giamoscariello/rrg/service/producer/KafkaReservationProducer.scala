package com.giamoscariello.rrg.service.producer

import cats.effect.{IO, Resource, Sync}
import com.giamoscariello.rrg.model.{Key, Reservation}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.Logger

import scala.jdk.CollectionConverters._
import scala.language.postfixOps

object KafkaReservationProducer extends HelperSerdes {
  def makeKafkaProducer[F[_]](config: Config)(implicit F: Sync[IO], logger: Logger): Resource[IO, KafkaProducer[Key, Reservation]] = {
    Resource.make(F.delay{
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

    })(producer => IO{
      producer.flush()
      producer.close()
      logger info s"closing producer"
    })
  }
}