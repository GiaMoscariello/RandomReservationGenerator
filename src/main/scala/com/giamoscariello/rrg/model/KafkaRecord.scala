package com.giamoscariello.rrg.model

case class Key(id: String) extends Serializable

case class KafkaRecord(k: Key, v: Reservation) extends Serializable

object Key {
  def batchKeyList(n: Int): List[Key] = {
    Range(1, n)
      .map(i => Key(i + "_" + java.util.UUID.randomUUID.toString.subSequence(0, 8)))
      .toList
  }
}