import cats.effect._
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

object Database {
  def transactor[F[_]: Async](config: DatabaseConfig, executionContext: ExecutionContext): Resource[F, HikariTransactor[F]] = {
    HikariTransactor.newHikariTransactor[F](
      config.driver,
      config.url,
      config.user,
      config.password,
      executionContext
    )
  }

  def initialize[F[_]: Async](transactor: HikariTransactor[F]): F[Unit] = {
    transactor.configure { dataSource =>
      Sync[F].delay {
        val flyWay = Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").load()
        flyWay.migrate()
        ()
      }
    }
  }
}
