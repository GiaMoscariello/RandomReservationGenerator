package rrg

import com.giamoscariello.rrg.model.{DataSample}
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.funsuite.AnyFunSuite


class DataSamplesTest extends AnyFunSuite {
  test("Format properly a Bson document") {
    val bson: BsonDocument = BsonDocument(
      s"""{
         |"dataType": "names",
         |    "list": ["Mario", "Aldo"]
         |}
         |""".stripMargin)

      val expected = DataSample("names", Seq("Mario", "Aldo"))
      val actual = DataSample(bson)

      assert(expected === actual)

  }

  test("A wrong Bson Document should return a empty DataSample") {
    val bson: BsonDocument = BsonDocument(
      s"""{
         |"wrongDataType": "names",
         |    "wrongList": ["Mario", "Aldo"]
         |}
         |""".stripMargin)

    val expected = DataSample.empty
    val actual = DataSample(bson)

    assert(expected === actual)

  }

}

