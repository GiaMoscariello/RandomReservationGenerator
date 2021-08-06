package com.giamoscariello.rrg.service

import scala.concurrent.Future

trait Generator[T] {
  def generate(times: Int): Future[T]
}
