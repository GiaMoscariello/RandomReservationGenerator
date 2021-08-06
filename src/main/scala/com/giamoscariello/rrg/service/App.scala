package com.giamoscariello.rrg.service

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.giamoscariello.rrg.repository.MongoStore
import io.circe.generic.auto._
import io.circe.syntax._

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    IO {
      (for {
        collection      <- IO.fromFuture(MongoStore.findAllDataSamples) //TODO: CLOSE THIS RESOURCE
        dataSample      <- collection.map(x => IO.fromTry(x)).sequence
        generator        = GenerateRandomReservation(dataSample)
      } yield generator).map {
        gens: GenerateRandomReservation =>
          for (_ <- 1 to args.head.toInt)
            gens.makeReservation match {
              case Some(x) => println(x.asJson)
              case None => println("No reservation made")
            }
      }
    }.unsafeRunSync.as(ExitCode(1))
  }
}
