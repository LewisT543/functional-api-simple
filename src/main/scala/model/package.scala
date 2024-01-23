import java.time.LocalDateTime

package object model {
  case class Todo(id: Option[Long], description: String, importance: String)

  case object TodoNotFoundError

  abstract sealed class TaskStatus(val value: String)
  case object Pending extends TaskStatus("pending")
  case object InProgress extends TaskStatus("in-progress")
  case object Completed extends TaskStatus("completed")

  case class TaskDescription(content: String, lastEditedOn: LocalDateTime, contentLength: Int)
  case class BigTask(id: Long, title: String, description: TaskDescription, status: TaskStatus)
}