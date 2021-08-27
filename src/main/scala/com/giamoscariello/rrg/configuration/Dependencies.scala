package com.giamoscariello.rrg.configuration

import com.giamoscariello.rrg.model.{Key, Reservation}
import org.apache.kafka.clients.producer.KafkaProducer
import org.mongodb.scala.MongoClient

case class Dependencies[K, V](mongoClient: MongoClient, kafkaProducer: KafkaProducer[Key, Reservation])
