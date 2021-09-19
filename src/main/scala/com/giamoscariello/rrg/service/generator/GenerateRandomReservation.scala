package com.giamoscariello.rrg.service.generator

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.giamoscariello.rrg.model._

import scala.util.Random


//TODO: why using IO???
case class GenerateRandomReservation(datas: Seq[DataSample], keys: List[Key]) {

  def mkReservationRecords: IO[List[KafkaRecord]] =
    keys.map { k => makeReservation.map(r => KafkaRecord(k,r))}
      .sequence

  def makeReservation: IO[Reservation] = (for {
    user      <- GenerateRandomUser(datas).make
    location  <- generateRandomLocation
    dates     = ReservationDates.generate
  } yield Reservation(user, dates, location))

  private def generateRandomLocation: IO[Location] = {
    val locations = datas.find((dt: DataSample) => dt.dataType == "locations").getOrElse(DataSample.empty)

    val randomLocation: String = Random.shuffle(locations.list).head
    IO(Location(randomLocation))
  }
}