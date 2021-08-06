package com.giamoscariello.rrg.service

import com.giamoscariello.rrg.model.{DataSample, DataSamples, Location, Reservation, ReservationDate, User}
import com.giamoscariello.rrg.repository.MongoDB

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

case class GenerateRandomReservation(datas: List[DataSample]) {

  def makeReservation: Option[Reservation] = for {
    dates <- GenerateRandomDate.generate
    user <- GenerateRandomUser(datas).make
    location <- generateRandomLocation
  } yield Reservation(user, dates._1, dates._2, location)


  def generateRandomLocation: Option[Location] = {
    val randomLocation: String = Random.shuffle(DataSamples.locations).head
    Some(Location(randomLocation))
  }
}
