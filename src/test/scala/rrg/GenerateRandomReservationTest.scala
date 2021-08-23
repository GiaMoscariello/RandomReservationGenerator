package rrg

import com.giamoscariello.rrg.model.DataSample
import com.giamoscariello.rrg.service.generator.GenerateRandomReservation
import org.scalatest.funsuite.AnyFunSuite

class GenerateRandomReservationTest extends AnyFunSuite{

  test("Random day should be in month range") {

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
