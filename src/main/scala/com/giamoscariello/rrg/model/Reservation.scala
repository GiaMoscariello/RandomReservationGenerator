package com.giamoscariello.rrg.model

import cats.effect.IO

import java.time.LocalDate
import scala.::
import scala.util.Random

case class Reservation(user: User, dates: ReservationDates, location: Location) extends Serializable

case class User(name: String, surname: String, mail: String, phone: String) extends Serializable

case class ReservationDate(day: Int, month: Int, year: Int) extends Serializable

case class ReservationDates(dateIn: LocalDate, dateOut: LocalDate)

case class Location(name: String) extends Serializable

case class Person(name: String, surname: String) extends Serializable

case class Key(id: String) extends Serializable

case class KafkaRecord(k: Key, v: Reservation) extends Serializable

object ReservationDates {
  def generate: ReservationDates = {
    val start = LocalDate.of(2017, 1, 20)
    val end = LocalDate.now
    val dateIn = LocalDate.ofEpochDay(Random.between(start.toEpochDay, end.toEpochDay))
    val dateOut = dateIn.plusWeeks(1)
    ReservationDates(dateIn, dateOut)
  }
}

object Key {
  def batchKeyList: List[Key] = {
    var list = List[Key]()
    for (i <- 1 to 100)
      list = addKey(list, Key(i + "_" + java.util.UUID.randomUUID.toString))
    list
  }

  private def addKey(l: List[Key], k: Key): List[Key] = k :: l
}