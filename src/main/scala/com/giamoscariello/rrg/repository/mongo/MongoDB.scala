package com.giamoscariello.rrg.repository.mongo

import cats.effect.{IO, Resource, Sync}
import com.mongodb.MongoClientSettings
import com.typesafe.config.ConfigFactory
import org.mongodb.scala.connection.ClusterSettings.Builder
import org.mongodb.scala.connection.{ClusterSettings, SocketSettings}
import org.mongodb.scala.{MongoClient, MongoCredential, ServerAddress}

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters._

object MongoDB {

  def makeClient[F[_]](implicit F: Sync[IO]): Resource[IO, MongoClient] =
    Resource.make(
      F.delay{
        val config = ConfigFactory.load("private.conf")
        createMongoClient(
          MongoConf(
            servers = config.getStringList("mongo-server-address").asScala.toList,
            port = config.getInt("mongo-server-port"),
            username = None,
            password = None,
            auth = config.getBoolean("mongo-auth-required")
          ))
      }
    )(x => IO(x.close()))

  private def createMongoClient(conf: MongoConf): MongoClient = {
    val builder = MongoClientSettings
      .builder()
      .applyToSocketSettings(ssb => ssb.applySettings(
        SocketSettings
          .builder()
          .readTimeout(5000, TimeUnit.SECONDS)
          .connectTimeout(5000, TimeUnit.SECONDS)
          .build
      ))
      .applyToClusterSettings((csb: Builder) => {
        csb.applySettings(
          ClusterSettings
            .builder()
            .hosts(conf.servers.map(addr => ServerAddress(host = addr, port = conf.port)).asJava)
            .build()
        )
      })
    if (conf.auth)
      builder.credential(
        MongoCredential.createScramSha1Credential(
          userName = conf.username.get,
          password = conf.password.get.toCharArray,
          source = "admin"
        )
      ).build()
    MongoClient(builder.build())
  }
}