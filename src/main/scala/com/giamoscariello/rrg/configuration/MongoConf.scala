package com.giamoscariello.rrg.configuration

case class MongoConf(
                      servers: List[String],
                      port: Int,
                      username: Option[String],
                      password: Option[String],
                      auth: Boolean
                    )
