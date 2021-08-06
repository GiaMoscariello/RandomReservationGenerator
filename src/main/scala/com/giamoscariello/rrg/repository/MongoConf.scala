package com.giamoscariello.rrg.repository

case class MongoConf(
                    servers: List[String],
                    port:    Int,
                    username: Option[String],
                    password: Option[String],
                    auth:     Boolean
                    )

