package repository
import model.Todo

trait TodoRepository[F[_]] {
  def createTodo(todo: Todo): F[Either[Throwable, Todo]]
  def getTodo(id: Long): F[Either[Throwable, Todo]]
  def updateTodo(id: Long, updatedTodo: Todo): F[Either[Throwable, Todo]]
  def deleteTodo(id: Long): F[Either[Throwable, Unit]]
  def getAllTodos: F[Either[Throwable, List[Todo]]]
}