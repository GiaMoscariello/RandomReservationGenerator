package com.giamoscariello.rrg.service.producer

import cats.effect.{ContextShift, IO}
import com.giamoscariello.rrg.model.{KafkaRecord, Key, Reservation}
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.slf4j.Logger

import scala.concurrent.ExecutionContext
import scala.collection.immutable._
import scala.language.postfixOps

case class KafkaSender(producer: KafkaProducer[Key, Reservation]) {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  def sendRecords(records: List[KafkaRecord], topic: String)(implicit logger: Logger): IO[List[RecordMetadata]] = {
      IO.delay( records.map { record =>
        val producerRecord = new ProducerRecord[Key, Reservation](topic, record.k, record.v)
        logger info s"Try to sending record with key: ${record.k.id} and body:${record.v.asJson}"
        producer.send(producerRecord, new Callback() {
          def onCompletion(metadata: RecordMetadata,
                           exception: Exception): Unit = {
            if (exception == null) {
              logger info s"Successful message sent to topic ${topic} with key ${record.k.id}"
            } else {
              logger error s" Error sending message: ${exception.printStackTrace()}"
            }
          }
        }).get()
      })
    }
}