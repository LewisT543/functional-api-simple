package repository

import doobie.ConnectionIO
import doobie.implicits._
import model.{BigTask, Todo}

object DoobieQueries {
  def getAllTodos: ConnectionIO[List[Todo]] = sql"SELECT id, description, importance FROM todo".query[Todo].to[List]
  def getTodo(id: Long): ConnectionIO[Todo] = sql"SELECT id, description, importance FROM todo WHERE id = $id".query[Todo].unique
  def createTodo(todo: Todo): ConnectionIO[Long] = sql"INSERT INTO todo (description, importance) VALUES (${todo.description}, ${todo.importance})".update.withUniqueGeneratedKeys[Long]("id")
  def updateTodo(id: Long, todo: Todo): ConnectionIO[Unit] = sql"UPDATE todo SET description = ${todo.description}, importance = ${todo.importance} WHERE id = $id".update.run.map(_ => ())
  def deleteTodo(id: Long): ConnectionIO[Unit] = sql"DELETE FROM todo WHERE id = $id".update.run.map(_ => ())


//  def getAllBigTasks: ConnectionIO[List[BigTask]] = sql"SELECT * FROM big_tasks".query[BigTask].to[List]
//  def getBigTask(id: Long): ConnectionIO[BigTask] = sql"SELECT * FROM big_tasks WHERE id = $id".query[BigTask].unique
//  def createBigTask(bigTask: BigTask): ConnectionIO[Long] = sql"INSERT INTO big_tasks (title, description_content, description_last_edited_on, description_content_length, status) VALUES (${bigTask.title}, ${bigTask.description.content}, ${bigTask.description.lastEditedOn}, ${bigTask.description.contentLength}, ${bigTask.status})".update.withUniqueGeneratedKeys[Long]("id")
//  def updateBigTask(id: Long, bigTask: BigTask): ConnectionIO[Unit] = sql"UPDATE big_tasks SET title = ${bigTask.title}, description_content = ${bigTask.description.content}, description_last_edited_on = ${bigTask.description.lastEditedOn}, description_content_length = ${bigTask.description.contentLength}, status = ${bigTask.status} where id=${bigTask.id}".update.run.map(_ => ())
//  def deleteBigTask(id: Long): ConnectionIO[Unit] = sql"DELETE FROM big_tasks WHERE id = $id".update.run.map(_ => ())
}
