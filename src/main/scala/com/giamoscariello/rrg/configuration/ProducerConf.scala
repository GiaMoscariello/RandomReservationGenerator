package com.giamoscariello.rrg.configuration

import org.apache.kafka.common.serialization.Serializer

case class ProducerConf(acks: String,
                        clientId: Option[String],
                        compressionType: String,
                        bootstrapServer: List[String],
                        serializers: List[Serializer[_]]
                       )
