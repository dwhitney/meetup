import java.io.File
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth._

trait FileRepository{
  def saveFile(file: File): Unit
}

trait S3UtilsLayer{
  
  val awsKey: String
  val awsSecret: String
  
  lazy val awsCredentials: AWSCredentials = new BasicAWSCredentials(awsKey, awsSecret)
  
  class S3Utils extends FileRepository{
    
    lazy val client = new AmazonS3Client(awsCredentials)
    
    def saveFile(file: File): Unit = client.putObject("meetup-test-bucket", file.getName, file)
    
  }
  
}