package com.giamoscariello.rrg.model

case class Key(id: String) extends Serializable

case class KafkaRecord(k: Key, v: Reservation) extends Serializable

object Key {
  def batchKeyList(n: Int): List[Key] = {
    var list = List[Key]()
    for (i <- 1 to n)
      list = addKey(list, Key(i + "_" + java.util.UUID.randomUUID.toString))
    list
  }

  private def addKey(l: List[Key], k: Key): List[Key] = k :: l
}