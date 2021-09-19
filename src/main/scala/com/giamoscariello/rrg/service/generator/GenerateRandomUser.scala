package com.giamoscariello.rrg.service.generator

import cats.effect.IO
import com.giamoscariello.rrg.model.{DataSample, User}

import scala.util.Random


case class GenerateRandomUser(datas: Seq[DataSample]) {
  def make: IO[User] = {
    IO.fromOption(for {
      name <- randomDataFrom(dataSampleListOf("names"))
      surname <- randomDataFrom(dataSampleListOf("surnames"))
      mail  <- generateMail(name, surname)
      phone <- generateRandomPhone
    } yield User(name, surname, mail, phone))(new Exception("No user made"))
  }

  private def dataSampleListOf(filter: String) = datas.find(x => x.dataType == filter)

  private def generateMail(name: String, surname: String): Option[String] = Option(name + "." + surname + "@mail.com")

  private def generateRandomPhone: Option[String] = Option("340" + Random.nextInt(999999).toString)

  private def randomDataFrom(samples: Option[DataSample]): Option[String] = samples.map {
    x =>
      Option(Random.shuffle(x.list).head)
  }.getOrElse(Option(""))

}