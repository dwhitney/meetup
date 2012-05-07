import com.amazonaws.services.simpledb._
import com.amazonaws.services.simpledb.model._
import com.amazonaws.auth._

trait SimpleDBUserRepositoryLayer{
  
  val awsKey: String
  val awsSecret: String
  lazy val awsCredentials: AWSCredentials = new BasicAWSCredentials(awsKey, awsSecret)
  
  class SimpleDBUserRepository extends UserRepository{
  
    protected lazy val client = new AmazonSimpleDBClient(awsCredentials)
  
    def create(user: User): Unit = {
      val attributes = new java.util.LinkedList[ReplaceableAttribute]()
      attributes.add(new ReplaceableAttribute("email", user.email, true))
      val request = new PutAttributesRequest("meetup_users", user.email, attributes)
      client.putAttributes(request)
    }

    def read(email: String): Option[User] = {
      import scala.collection.JavaConversions._
      val result = client.getAttributes(new GetAttributesRequest("meetup_users", email))
      if(result.getAttributes.size == 0) None
      else{
        var map = Map[String, String]()
        val iterator = result.getAttributes.iterator
        val attribute = iterator.next
        Some(User(attribute.toString))
      }
    }

    def update(user: User): Unit = create(user)

    def delete(user: User): Unit = client.deleteAttributes(new DeleteAttributesRequest("meetup_users", user.email))
  
  }
}