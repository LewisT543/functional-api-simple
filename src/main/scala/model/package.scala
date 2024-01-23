package object model {
  case class Todo(id: Option[Long], description: String, importance: String)

  case object TodoNotFoundError
}