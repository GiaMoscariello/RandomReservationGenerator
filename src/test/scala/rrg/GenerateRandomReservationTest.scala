package rrg

import com.giamoscariello.rrg.model.DataSample
import com.giamoscariello.rrg.service.generator.GenerateRandomReservation
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.funsuite.AnyFunSuite

class GenerateRandomReservationTest extends AnyFunSuite{

  lazy val dataSampleStub: BsonDocument =
    BsonDocument(
      s"""
         | {
         |  "user" : {
         |    "name" : "Salvatore",
         |    "surname" : "Giordano",
         |    "mail" : "Salvatore.Giordano@mail.com",
         |    "phone" : "340765734"
         |  },
         |  "dates" : {
         |    "dateIn" : "2017-09-14",
         |    "dateOut" : "2017-09-21"
         |  },
         |  "location" : {
         |    "name" : "Perla"
         |  }
         | }
         |"""
        .stripMargin)



  test("Correctly generate a reservation from data sample") {

    val dataSamle = List(
      DataSample(
        "names",
        Seq("Mario")
      ),
      DataSample(
        "surnames",
        Seq("Rossi")
      ),
      DataSample(
        "locations",
        Seq("Corallo")
      )
    )

  }
}
