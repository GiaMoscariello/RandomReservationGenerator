package com.giamoscariello.rrg.service.utils

import scala.util.{Failure, Success, Try}

object OptionUtils {
  implicit class OptionOps[A](val opt: Option[A]) {
    def toTry(msg: String): Try[A] = {
      opt
        .map(Success(_))
        .getOrElse(Failure(new NoSuchElementException(msg)))
    }
  }
}
