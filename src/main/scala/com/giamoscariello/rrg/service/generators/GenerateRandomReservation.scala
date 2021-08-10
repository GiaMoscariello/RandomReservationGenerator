package com.giamoscariello.rrg.service.generators

import com.giamoscariello.rrg.model.{DataSample, DataSamples, Location, Reservation, ReservationDate}

import scala.util.Random

case class GenerateRandomReservation(datas: List[DataSample]) {

  def makeReservation: Option[Reservation] = for {
    dates <- makeDates
    user <- GenerateRandomUser(datas).make
    location <- generateRandomLocation
  } yield Reservation(user, dates._1, dates._2, location)


  def generateRandomLocation: Option[Location] = {
    val randomLocation: String = Random.shuffle(DataSamples.locations).head
    Some(Location(randomLocation))
  }

  def makeDates: Option[(ReservationDate, ReservationDate)] = {
    generateDateOut(for {
      month <- generateMonth
      day <- generateDay(month)
      year <- generateYear
    } yield ReservationDate(day, month, year)) match {
      case Some(toReturn) => toReturn
      case None => None
    }
  }

  private def generateDateOut(dateIn: Option[ReservationDate]): Option[Option[(ReservationDate, ReservationDate)]] = {
    if (dateIn.isDefined) {
      val dayOut = dateIn.get.day + 7
      val daysInMonthOut: Int = ReservationDate.daysInAMonth(dateIn.get.month)
      if (dayOut > daysInMonthOut) {
        return Some(Some(dateIn.get, ReservationDate((daysInMonthOut + dayOut) % daysInMonthOut, dateIn.get.month + 1, generateYear.get)))
      }
      else return Some(Some(dateIn.get, ReservationDate(dayOut + dateIn.get.day, dateIn.get.month, generateYear.get)))
    }
    None
  }

  private def generateMonth: Option[Int] = Some(Random.nextInt(11) + 1)

  private def generateDay(month: Int): Option[Int] =
    Some(Random.nextInt(ReservationDate.daysInAMonth(month)) + 1)

  private def generateYear: Option[Int] = Some(2021)
}
