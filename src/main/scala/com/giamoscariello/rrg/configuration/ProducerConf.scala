package com.giamoscariello.rrg.configuration

import scala.language.postfixOps

case class ProducerConf(acks: Int,
                        clientId: Option[String],
                        compressionType: String,
                        bootstrapServer: List[String]
                       )

