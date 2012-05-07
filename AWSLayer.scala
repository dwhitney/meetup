import com.amazonaws.auth._

trait AWSLayer{
  val awsKey: String
  val awsSecret: String
  lazy val awsCredentials: AWSCredentials = new BasicAWSCredentials(awsKey, awsSecret)
}