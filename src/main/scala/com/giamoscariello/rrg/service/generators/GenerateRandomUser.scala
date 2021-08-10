package com.giamoscariello.rrg.service.generators

import com.giamoscariello.rrg.model.{DataSample, User}

import scala.util.Random

case class GenerateRandomUser(datas: List[DataSample]) {
  def make: Option[User] = {
    for {
      name <- randomDataFrom(dataSampleListOf("names"))
      surname <- randomDataFrom(dataSampleListOf("surnames"))
      mail  <- generateMail(name, surname)
      phone <- generateRandomPhone
    } yield User(name, surname, mail, phone)
  }

  private def dataSampleListOf(filter: String) = datas.find(x => x.dataType == filter)

  private def generateMail(name: String, surname: String): Option[String] = Some(name + "." + surname + "@mail.com")

  private def generateRandomPhone: Option[String] = Some("340" + Random.nextInt(999999).toString)

  private def randomDataFrom(names: Option[DataSample]): Option[String] = names.map(x => Random.shuffle(x.list).head)
}
