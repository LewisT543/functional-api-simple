import cats.effect._
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

package object config {
  case class ServerConfig(host: String, port: Int)
  case class DatabaseConfig(driver: String, url: String, user: String, password: String, threadPoolSize: Int)
  case class Config(server: ServerConfig, database: DatabaseConfig)

  object Config {
    def load[F[_]: Async](configFile: String = "application.conf"): Resource[F, Config] = {
      Resource.eval(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[F, Config]())
    }
  }
}