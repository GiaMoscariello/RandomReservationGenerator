package com.giamoscariello.rrg.service.generator

import com.giamoscariello.rrg.model._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps

import java.time.LocalDate
import java.util.Date
import scala.util.Random

case class GenerateRandomReservation(datas: List[DataSample]) {

  def make(n: Int): Unit =
    for (_ <- 1 to n)
      makeReservation match {
        case Some(x) => println(x.asJson)
        case None => println("No reservation made")
      }

  def makeReservation: Option[Reservation] = for {
    user <- GenerateRandomUser(datas).make
    location <- generateRandomLocation
    dates = ReservationDates.apply
  } yield Reservation(user, dates, location)

  private def generateRandomLocation: Option[Location] = {
    val randomLocation: String = Random.shuffle(DataSamples.locations).head
    Some(Location(randomLocation))
  }
}
