package rrg

import com.giamoscariello.rrg.model.DataSamples
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.funsuite.AnyFunSuite

class DataSamplesTest extends AnyFunSuite {

  test("Format properly a BSON document") {
    val doc = BsonDocument("{\"_id\":{\"$oid\":\"610bf6659658f61efbfc9ddd\"},\"dataType\":\"names\",\"list\":[\"Mario\",\"Aldo\",\"Franco\",\"Francesco\",\"Salvatore\",\"Alessandro\",\"Flavia\",\"Anna\",\"Maria\",\"Giulia\",\"Rosaria\"]}")

    println(DataSamples.from(doc).get)
  }
}
