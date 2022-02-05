package com.giamoscariello.rrg.service.producer

import cats.effect.{IO, Resource, Sync}
import com.giamoscariello.rrg.model.{KafkaRecord, Key, Reservation}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.{Serdes, Serializer}
import org.apache.kafka.streams.StreamsBuilder
import org.slf4j.{Logger, LoggerFactory}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import java.util.Properties
import scala.jdk.CollectionConverters._
import scala.language.postfixOps

object KafkaReservationProducer extends HelperSerdes {
  def makeKafkaProducer[F[_]](implicit F: Sync[IO], logger: Logger): Resource[IO, KafkaProducer[Key, Reservation]] = {
    Resource.make(F.delay{
      val config: Config = ConfigFactory.load("kafka.conf")
      val configMap = config.
        entrySet()
        .asScala
        .map(pair => (pair.getKey, config.getAnyRef(pair.getKey)))
        .toMap

      val props = new Properties
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "orders-application")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
      props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,  Serdes.String().getClass.getName)

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