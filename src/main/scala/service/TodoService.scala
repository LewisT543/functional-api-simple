package service


import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import model.{Importance, Todo, TodoNotFoundError}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.{HttpRoutes, Request, Response, Uri}
import org.slf4j.{Logger, LoggerFactory}
import repository.DoobieTodoRepository

class TodoService[F[_]: Async](repository: DoobieTodoRepository[F]) extends Http4sDsl[F] {
  private implicit val encodeImportance: Encoder[Importance] = Encoder.encodeString.contramap[Importance](_.value)
  private implicit val decodeImportance: Decoder[Importance] = Decoder.decodeString.map[Importance](Importance.unsafeFromString)

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "todos" => getTodos
    case GET -> Root / "todos" / LongVar(id) => getTodoById(id: Long)
    case req @ POST -> Root / "todos" => createTodo(req)
    case req @ PUT -> Root / "todos" / LongVar(id) => updateTodo(req, id)
    case DELETE -> Root / "todos" / LongVar(id) => deleteTodo(id)
  }

  private def getTodos: F[Response[F]] = repository.getAllTodos.flatMap {
    case Right(todos) => Ok(todos.asJson)
    case Left(error) => handleTodoErrors(error)
  }

  private def getTodoById(id: Long): F[Response[F]] = repository.getTodo(id).flatMap {
    case Right(todo) => Ok(todo.asJson)
    case Left(error) => handleTodoErrors(error)
  }

  private def createTodo(req: Request[F]): F[Response[F]] = for {
    todo <- req.decodeJson[Todo]
    result <- repository.createTodo(todo)
    x = logger.error(s"resultOfCreate $result")
    response <- result match {
      case Right(createdTodo) => Created(createdTodo.asJson, Location(Uri.unsafeFromString(s"/todos/${createdTodo.id.get}")))
      case Left(error) => handleTodoErrors(error)
    }
  } yield response

  private def updateTodo(req: Request[F], id: Long): F[Response[F]] = for {
    todo <- req.decodeJson[Todo]
    result <- repository.updateTodo(id, todo)
    response <- result match {
      case Right(updatedTodo) => Ok(updatedTodo.asJson)
      case Left(error) => handleTodoErrors(error)
    }
  } yield response

  private def deleteTodo(id: Long): F[Response[F]] = repository.deleteTodo(id).flatMap {
    case Right(_) => NoContent()
    case Left(error) => handleTodoErrors(error)
  }

  private def handleTodoErrors(error: Throwable): F[Response[F]] = error match {
    case _ => InternalServerError("An unexpected error has occurred <MANUAL>")
  }
}

object TodoService {
  // Validation goes in the companion object
  def validateTodo[F[_]: Async](todo: Todo): F[Either[Throwable, Todo]] = Async[F].delay {
    if (todo.description.trim.isEmpty) Left(new IllegalArgumentException("Description cannot be empty."))
    else Right(todo)
  }
}