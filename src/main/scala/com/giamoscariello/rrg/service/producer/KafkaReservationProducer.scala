package com.giamoscariello.rrg.service.producer

import cats.effect.{IO, Resource, Sync}
import com.giamoscariello.rrg.model.{KafkaRecord, Key, Reservation}
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.{Logger, LoggerFactory}

import java.util.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.language.postfixOps


case class KafkaReservationSender(producer: KafkaProducer[Key, Reservation]) {
  def sendRecords(records: List[KafkaRecord])(implicit logger: Logger): List[Future[RecordMetadata]] = {
    records.map { record =>
      val pr = new ProducerRecord[Key, Reservation]("reservationsMade", record.k, record.v)
      logger info s"Sending record with key: ${record.k} and body:${record.v.asJson}"
      producer send pr
    }
  }
}

object KafkaReservationProducer extends HelperSerdes {

  def makeKafkaProducer[F[_]](implicit F: Sync[IO]): Resource[IO, KafkaProducer[Key, Reservation]] = {
    Resource.make(F.delay{
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

    })(producer => IO{
      producer.flush()
      producer.close()
      val logger: Logger = LoggerFactory.getLogger(this.getClass.getName)
      logger info s"closing producer"
    })
  }

}