
case class User(email: String)

trait UserRepository{
  def create(user: User): Unit
  def read(email: String): Option[User]
  def update(user: User): Unit
  def delete(user: User): Unit
}

class UserCrapMapRepository extends UserRepository{
  
  private var map = Map[String, User]()
  
  def create(user: User): Unit = map = map + (user.email -> user)
  def read(email: String): Option[User] = map.get(email)
  def update(user: User): Unit = map = map + (user.email -> user)
  def delete(user: User) = map = map - user.email
  
}

trait UserEndpointLayer{
  import unfiltered.filter._
  import unfiltered.request._
  import unfiltered.response._
  
  val userRepository: UserRepository
  
  class UserEndpoint extends Plan{
    def intent = {
      case Path(Seg("save" :: email :: Nil)) =>
        userRepository.create(User(email))
        Ok ~> ResponseString(email + " was saved")
      case Path(Seg("exists" :: email :: Nil)) =>
        if(userRepository.read(email).isDefined) Ok ~> ResponseString(email + " exists!")
        else Ok ~> ResponseString(email + " does not exist!")
    }
  }
  
}

object Cake extends UserEndpointLayer 
  with SimpleDBUserRepositoryLayer{
  
  lazy val awsKey: String = readAwsKey
  lazy val awsSecret: String = readAwsSecret
  
  lazy val userRepository = new UserCrapMapRepository
  lazy val userEndpoint = new UserEndpoint
  
  
  private def readAwsKey = scala.io.Source.fromFile(System.getProperty("user.home") + "/.awssecret").getLines.toList(0)
  private def readAwsSecret = scala.io.Source.fromFile(System.getProperty("user.home") + "/.awssecret").getLines.toList(1)

}

object Meetup extends App{
  import unfiltered.jetty.Http
  import Cake._
  
  Http(8080).plan(userEndpoint).run
  
}