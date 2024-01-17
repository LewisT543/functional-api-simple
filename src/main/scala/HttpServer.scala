import cats.effect._
import cats.implicits._
import config.Config
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import repository.DoobieTodoRepository
import service.TodoService

import scala.concurrent.ExecutionContext.global

object HttpServer {
  def create[F[_]: Async](configFile: String = "application.conf"): F[ExitCode] = {
    resources(configFile).use { resources =>
      create(resources)
    }
  }

  private def resources[F[_]: Async](configFile: String): Resource[F, Resources[F]] = {
    for {
      config <- Config.load[F](configFile)
      ec <- ExecutionContexts.fixedThreadPool[F](config.database.threadPoolSize)
      transactor <- Database.transactor[F](config.database, ec)
    } yield Resources(transactor, config)
  }

  private def create[F[_]: Async](resources: Resources[F]): F[ExitCode] = {
    for {
      _ <- Database.initialize[F](resources.transactor)
      repository = new DoobieTodoRepository[F](resources.transactor)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(resources.config.server.port, resources.config.server.host)
        .withHttpApp(new TodoService[F](repository).routes.orNotFound).serve.compile.lastOrError
    } yield exitCode
  }

  case class Resources[F[_]](transactor: HikariTransactor[F], config: Config)
}