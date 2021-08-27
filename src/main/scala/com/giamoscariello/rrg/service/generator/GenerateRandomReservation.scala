package com.giamoscariello.rrg.service.generator

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.giamoscariello.rrg.model._

import scala.util.Random

case class GenerateRandomReservation(datas: List[DataSample], keys: List[Key]) {

  def mkReservationRecords: IO[List[KafkaRecord]] =
    keys.map { k => makeReservation.map(r => KafkaRecord(k,r))}
      .sequence

  def makeReservation: IO[Reservation] = (for {
    user      <- GenerateRandomUser(datas).make
    location  <- generateRandomLocation
    dates     = ReservationDates.generate
  } yield Reservation(user, dates, location))

  private def generateRandomLocation: IO[Location] = {
    val randomLocation: String = Random.shuffle(DataSamples.locations).head
    IO(Location(randomLocation))
  }
}