package repository

import doobie.ConnectionIO
import doobie.implicits._
import model.Todo

object DoobieQueries {
  def getAllTodos: ConnectionIO[List[Todo]] = sql"SELECT id, description, importance FROM todo".query[Todo].to[List]
  def getTodo(id: Long): ConnectionIO[Todo] = sql"SELECT id, description, importance FROM todo WHERE id = $id".query[Todo].unique
  def createTodo(todo: Todo): ConnectionIO[Long] = sql"INSERT INTO todo (description, importance) VALUES (${todo.description}, ${todo.importance})".update.withUniqueGeneratedKeys[Long]("id")
  def updateTodo(id: Long, todo: Todo): ConnectionIO[Unit] = sql"UPDATE todo SET description = ${todo.description}, importance = ${todo.importance} WHERE id = $id".update.run.map(_ => ())
  def deleteTodo(id: Long): ConnectionIO[Unit] = sql"DELETE FROM todo WHERE id = $id".update.run.map(_ => ())
}
