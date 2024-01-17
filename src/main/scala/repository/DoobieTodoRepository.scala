package repository

import cats.effect.Async
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import model.{Importance, Todo}
import org.slf4j.{Logger, LoggerFactory}
import service.TodoService

class DoobieTodoRepository[F[_]: Async](transactor: Transactor[F]) extends TodoRepository[F] {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  override def getAllTodos: F[Either[Throwable, List[Todo]]] = DoobieQueries.getAllTodos.transact(transactor).attempt
  override def getTodo(id: Long): F[Either[Throwable, Todo]] = DoobieQueries.getTodo(id).transact(transactor).attempt
  override def createTodo(todo: Todo): F[Either[Throwable, Todo]] = TodoService.validateTodo(todo).flatMap {
    case Left(error) => Async[F].pure(Left(error))
    case Right(validatedUsed) => DoobieQueries.createTodo(validatedUsed).transact(transactor).attempt.map {
      case Right(id) => Right(validatedUsed.copy(id = id.some))
      case Left(e) => Left(e)
    }
  }
  override def updateTodo(id: Long, updatedTodo: Todo): F[Either[Throwable, Todo]] = TodoService.validateTodo(updatedTodo).flatMap {
    case Left(error) => Async[F].pure(Left(error))
    case Right(validatedUser) => DoobieQueries.updateTodo(id, updatedTodo).transact(transactor).attempt.map {
      case Right(_) => Right(validatedUser.copy(id = id.some))
      case Left(e) => Left(e)
    }
  }
  override def deleteTodo(id: Long): F[Either[Throwable, Unit]] = DoobieQueries.deleteTodo(id).transact(transactor).attempt
}

