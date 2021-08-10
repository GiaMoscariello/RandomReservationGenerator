package com.giamoscariello.rrg.service

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.giamoscariello.rrg.repository.mongo.{MongoDB, MongoStore}
import com.giamoscariello.rrg.service.generators.GenerateRandomReservation
import io.circe.generic.auto._
import io.circe.syntax._

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    program.use(_ => IO.unit).as(ExitCode.Success)
  }

  private def program: Resource[IO, Unit] = {
    for {
        client      <- MongoDB.makeClient
        collection  = client.getDatabase("RandomReservationGens").getCollection("DataSamples")
        mongoStore  = MongoStore(collection)
        _       <- Resource.eval{
          (for {
            dataCollection <- mongoStore.dataSamples
            generator = GenerateRandomReservation(dataCollection)
          } yield generator).map {
            generator =>
              for (_ <- 1 to 100)
                generator.makeReservation match {
                  case Some(x)  => println(x.asJson)
                  case None     => println("No reservation made")
                }
          }
        }
    } yield()
  }
}
