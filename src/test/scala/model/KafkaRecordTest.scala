package model

import com.giamoscariello.rrg.model.Key
import org.scalatest.funsuite.AnyFunSuite

import scala.::
import scala.annotation.tailrec

class KafkaRecordTest extends AnyFunSuite {

  test("given n > 0 a list of key not should be empty") {
    val n = 10
    val keys = Key.batchKeyList(n)

    assert(twoStrings("art", "and") == "YES")
  }

  test("HackerRank") {
    println(twoStrings("cat", "art"))
}

  def twoStrings(s1: String, s2: String): String = {
    val set2 = s2.toSet
    val found = s1.toSet.fold(' ')((acc, curr) => if(set2.contains(curr)) curr else acc)
    if (found != ' ') "YES" else "NO"
  }
}

