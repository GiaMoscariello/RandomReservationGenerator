package model

import com.giamoscariello.rrg.model.Key
import org.scalatest.funsuite.AnyFunSuite

class KafkaRecordTest extends AnyFunSuite {

  test("given n > 0 a list of key not should be empty") {
    val n = 10
    val keys = Key.batchKeyList(n)

    assert(keys.size == 9)
  }
}
