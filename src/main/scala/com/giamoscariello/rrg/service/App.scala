package com.giamoscariello.rrg.service

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.giamoscariello.rrg.repository.mongo.{MongoDB, MongoStore}
import com.giamoscariello.rrg.service.generator.GenerateRandomReservation

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    program.use(_ => IO.unit).as(ExitCode.Success)
  }

  private def program: Resource[IO, Unit] = {
    for {
      client    <- MongoDB.makeClient
      collection = client.getDatabase("RandomReservationGens").getCollection("DataSamples")
      mongoStore = MongoStore(collection)
      _ <- Resource.eval {
        mongoStore
          .dataSamples
          .map(data => GenerateRandomReservation(data) make(100))
      }
    } yield ()
  }
}
