package com.giamoscariello.rrg.service.generator

import com.giamoscariello.rrg.model._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps

import java.time.LocalDate
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success, Try}

case class GenerateRandomReservation(datas: List[DataSample]) {

  def make(n: Int): Unit =
    for (_ <- 1 to n) {
      makeReservation.onComplete {
        Thread.sleep(1)

        {
          case Success(y) => println(y.asJson)
          case Failure(e) => (e.printStackTrace())
        }

      }
    }

  def makeReservation: Future[Reservation] = for {
    user      <- GenerateRandomUser(datas).make
    location  <- generateRandomLocation
    dates     = ReservationDates.generate
  } yield Reservation(user, dates, location)

  private def generateRandomLocation: Future[Location] = {
    val randomLocation: String = Random.shuffle(DataSamples.locations).head
    Future(Location(randomLocation))
  }
}
