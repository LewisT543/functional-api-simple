package service

import cats.effect.IO
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import model.{Importance, Todo, TodoNotFoundError}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{Location, `Content-Type`}
import org.http4s.{HttpRoutes, MediaType, Request, Response, Uri}
import repository.TodoRepository
import io.circe.syntax._
import org.http4s.circe.CirceEntityEncoder._

class TodoService(repository: TodoRepository) extends Http4sDsl[IO] {
  private implicit val encodeImportance: Encoder[Importance] = Encoder.encodeString.contramap[Importance](_.value)
  private implicit val decodeImportance: Decoder[Importance] = Decoder.decodeString.map[Importance](Importance.unsafeFromString)

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "todos" => getTodos
    case GET -> Root / "todos" / LongVar(id) => getTodoById(id: Long)
    case req @ POST -> Root / "todos" => createTodo(req)
    case req @ PUT -> Root / "todos" / LongVar(id) => updateTodo(req, id)
    case DELETE -> Root / "todos" / LongVar(id) => deleteTodo(id)
  }

  private def getTodos: IO[Response[IO]] = repository.getTodos.map(_.asJson).compile.toVector.flatMap(todos => Ok(todos.asJson))

  private def getTodoById(id: Long): IO[Response[IO]] = for {
    getResult <- repository.getTodo(id)
    response <- todoResult(getResult)
  } yield response

  private def createTodo(req: Request[IO]): IO[Response[IO]] = for {
    todo <- req.decodeJson[Todo]
    createdTodo <- repository.createTodo(todo)
    response <- Created(createdTodo.asJson, Location(Uri.unsafeFromString(s"/todos/${createdTodo.id.get}")))
  } yield response

  private def updateTodo(req: Request[IO], id: Long): IO[Response[IO]] = for {
    todo <- req.decodeJson[Todo]
    updateResult <- repository.updateTodo(id, todo)
    response <- todoResult(updateResult)
  } yield response

  private def deleteTodo(id: Long): IO[Response[IO]] = repository.deleteTodo(id).flatMap {
    case Left(TodoNotFoundError) => NotFound()
    case Right(_) => NoContent()
  }

  private def todoResult(result: Either[TodoNotFoundError.type, Todo]): IO[Response[IO]] = {
    result match {
      case Left(TodoNotFoundError) => NotFound()
      case Right(todo) => Ok(todo.asJson)
    }
  }
}