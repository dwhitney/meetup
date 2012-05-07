import com.amazonaws.services.simpledb._
import com.amazonaws.services.simpledb.model._
import com.amazonaws.auth._

class SimpleDBUserRepository extends UserRepository{
  
  val awsKey = readAwsKey
  val awsSecret = readAwsSecret
  lazy val awsCredentials: AWSCredentials = new BasicAWSCredentials(awsKey, awsSecret)
  
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
    
  def readAwsKey = scala.io.Source.fromFile(System.getProperty("user.home") + "/.awssecret").getLines.toList(0)
  def readAwsSecret = scala.io.Source.fromFile(System.getProperty("user.home") + "/.awssecret").getLines.toList(1)
  
}