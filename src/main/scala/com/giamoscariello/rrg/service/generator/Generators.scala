package com.giamoscariello.rrg.service.generator

import cats.implicits.toTraverseOps
import com.giamoscariello.rrg.model.{DataSampled, KafkaRecord, Key, Reservation, ReservationDates, Room, User}

import scala.collection.immutable._
import scala.util.Random

trait Generator {
  def make(): Option[_]
}

case class ReservationListGenerator(names: DataSampled[String], surnames: DataSampled[String], locations: DataSampled[String], size: Int)  extends Generator {
  override def make(): Option[List[KafkaRecord]] = Key
    .batchKeyList(size)
    .map { key =>
      for {
        user        <- UserGenerator(names, surnames).make()
        reservation <- ReservationGenerator(user, locations).make()
      } yield KafkaRecord(key, reservation)
    }
    .sequence
}

case class UserGenerator(names: DataSampled[String], surnames: DataSampled[String]) extends Generator {
  def make(): Option[User] = {
    for {
      name      <- names.randomize()
      surname   <- surnames.randomize()
      mail      = generateMail(name, surname)
      phone     = generateRandomPhone
    } yield User(name, surname, mail, phone)
  }

  private def generateMail(name: String, surname: String): String = name + "." + surname + "@mail.com"
  private def generateRandomPhone: String = "340" + Random.nextInt(999999).toString
}

case class ReservationGenerator(user: User, locations: DataSampled[String]) extends Generator {
  override def make(): Option[Reservation] = {
    for {
      location  <- locations.randomize()
      dates     = ReservationDates.generate
    } yield Reservation(user, dates, Room(location))
  }
}