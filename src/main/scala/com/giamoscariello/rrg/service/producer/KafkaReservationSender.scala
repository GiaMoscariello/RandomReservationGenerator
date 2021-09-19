package com.giamoscariello.rrg.service.producer

import com.giamoscariello.rrg.model.{KafkaRecord, Key, Reservation}
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.slf4j.Logger

import java.util.concurrent.Future
import scala.language.postfixOps

//TODO: this can be a Object
case class KafkaReservationSender(producer: KafkaProducer[Key, Reservation]) {
  def sendRecords(records: List[KafkaRecord])(implicit logger: Logger): List[Future[RecordMetadata]] = {
    records.map { record =>
      val pr = new ProducerRecord[Key, Reservation]("reservationsMade", record.k, record.v)
      logger info s"Sending record with key: ${record.k} and body:${record.v.asJson}"
      producer send pr
    }
  }
}