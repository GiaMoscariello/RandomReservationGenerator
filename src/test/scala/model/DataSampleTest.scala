package model

import cats.effect.IO
import com.giamoscariello.rrg.model.{DataSample, DataType}
import com.giamoscariello.rrg.repository.mongo.MongoStore
import io.circe.parser
import io.circe.syntax.EncoderOps
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.funsuite.AnyFunSuite

class DataSampleTest extends AnyFunSuite {

  test("correct create DataSample model form Json document") {
    val input = BsonDocument(
      s"""{
                        |"dataType":"names",
                        |"list": [
                        | "Mario","Aldo","Franco"
                        | ]
                        |}
                        |""".stripMargin)

    val expected = DataSample(List("Mario", "Aldo", "Franco"))

    val actual   =  toDataSample(input).flatMap {
      case Left(error) => println("Error in decoding" + error.printStackTrace()) ;IO.raiseError(error)
      case Right(data) => IO(data)
    }

    assert(actual.unsafeRunSync() == expected)
  }

  test("input without list attribute") {
    val input = s"""{
                                |"dataType":"type"
                                |}
                                |""".stripMargin.asJson

    val expected = DataSample(List("Mario", "Aldo", "Franco"))
    val actual   = input.as[DataSample]

    assert(expected === actual)
  }

  def toDataSample[T <: DataType](doc: BsonDocument): IO[Either[Throwable, DataSample]] = {
    IO.delay(for {
      json <- parser.parse(doc.toJson)
      data <- json.as[DataSample]
    } yield data)
  }
}
