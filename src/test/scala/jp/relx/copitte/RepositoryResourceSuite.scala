package jp.relx.copitte

import java.io.File
import java.net.URI

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.commons.io.FileUtils._
import org.scalatest.fixture.FunSuite
import org.scalatest.BeforeAndAfterAll
import org.slf4j.LoggerFactory

import jp.relx.copitte.test.ResourceHandleFixture
import jp.relx.copitte.test.TestJsons
import net.liftweb.json.DefaultFormats
import scala.xml.XML

class RepositoryResourceSuite extends FunSuite
  with ResourceHandleFixture with BeforeAndAfterAll {
  
  val logger = LoggerFactory.getLogger(getClass)

  val WorkDir = new File(getTempDirectory(), "copitte_test")
  
  implicit val formats = DefaultFormats

  override def beforeAll {
    forceMkdir(WorkDir)
    val resourcesDir = new File("src/test/resources")
    List("onecommit-src.git", "blank-dest.git") foreach { orig =>
      copyDirectoryToDirectory(new File(resourcesDir, orig), WorkDir)
    }
  }

  override def afterAll {
    deleteDirectory(WorkDir)
  }

  test("create repository") { handler =>
    val srcRepo  = "file://" +
      new File(WorkDir, "onecommit-src.git").getCanonicalPath()
    val destRepo = "file://" +
      new File(WorkDir, "blank-dest.git").getCanonicalPath()

    val res = handler(new URI("/repos")).post(
        TestJsons.registerRepo(srcRepo, destRepo))
    expect(201) { res.getStatusCode() }

    val loc = res.getHeaders().asScala.get("Location")
    expect("/repos/copitte") {
      loc match {
        case None => "no Location header"
        case Some(x) if x.length == 1 =>
          try {
            new URI(x.head).getPath()
          } catch {
            case e => "uri parse failed"
          }
        case Some(x) => "too many location header"
      }
    }

    // TODO local filesystem check
  }
  
  test("list repositories") { handler =>
    val res = handler(new URI("/repos")).get()
    expect(200) { res.getStatusCode() }

    val body = XML.loadString(res.getEntity(classOf[String]))
    expect("copitte") {
      body \ "body" \ "ul" \ "li" text
    }
  }

  // TODO
  test("sync repositories (pull and push)") { pending }

  test("remove repository") { handler =>
    val res = handler(new URI("/repos/copitte")).delete()
    expect(200) { res.getStatusCode() }

    // TODO local filesystem check
  }
}
