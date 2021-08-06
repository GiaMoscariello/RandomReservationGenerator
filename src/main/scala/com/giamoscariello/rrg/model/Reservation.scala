package com.giamoscariello.rrg.model

case class Reservation(user: User, in: ReservationDate, out: ReservationDate, location: Location)

case class User(name: String, surname: String, mail: String, phone: String)

case class ReservationDate(day: Int, month: Int, year: Int)

case class Location(name: String)

case class Person(name: String, surname: String)

object ReservationDate {
  val daysInAMonth: Map[Int, Int] = Map(
    1 -> 31,
    2 -> 29,
    3 -> 31,
    4 -> 30,
    5 -> 31,
    6 -> 30,
    7 -> 31,
    8 -> 31,
    9 -> 30,
    10 -> 31,
    11 -> 30,
    12 -> 31
  )
}

