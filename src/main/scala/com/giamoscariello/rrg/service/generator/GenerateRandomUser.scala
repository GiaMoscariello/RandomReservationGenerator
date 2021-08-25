package com.giamoscariello.rrg.service.generator

import com.giamoscariello.rrg.model.{DataSample, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global


case class GenerateRandomUser(datas: List[DataSample]) {
  def make: Future[User] = {
    for {
      name <- randomDataFrom(dataSampleListOf("names"))
      surname <- randomDataFrom(dataSampleListOf("surnames"))
      mail  <- generateMail(name, surname)
      phone <- generateRandomPhone
    } yield User(name, surname, mail, phone)
  }

  private def dataSampleListOf(filter: String) = datas.find(x => x.dataType == filter)

  private def generateMail(name: String, surname: String): Future[String] = Future(name + "." + surname + "@mail.com")

  private def generateRandomPhone: Future[String] = Future("340" + Random.nextInt(999999).toString)

  private def randomDataFrom(samples: Option[DataSample]): Future[String] = samples.map {
    x =>
      Future(Random.shuffle(x.list).head)
  }.getOrElse(Future(""))

}