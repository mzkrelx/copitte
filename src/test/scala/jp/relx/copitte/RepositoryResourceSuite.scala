package jp.relx.copitte

import java.io.File
import java.net.URI

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.apache.commons.io.FileUtils
import org.scalatest.fixture.FunSuite
import org.scalatest.BeforeAndAfterAll
import org.slf4j.LoggerFactory

import jp.relx.copitte.test.ResourceHandleFixture
import jp.relx.copitte.test.TestJsons
import net.liftweb.json.DefaultFormats

class RepositoryResourceSuite extends FunSuite
  with ResourceHandleFixture with BeforeAndAfterAll {
  
  val logger = LoggerFactory.getLogger(getClass)

  val WorkDir = new File("target/test/work")

  implicit val formats = DefaultFormats

  override def beforeAll {
    FileUtils.forceMkdir(WorkDir)
    val resourceDir = new File("src/test/resources")
    List("onecommit-src.git", "blank-dest.git") foreach { orig =>
      FileUtils.copyDirectoryToDirectory(new File(resourceDir, orig), WorkDir)
    }
  }

  override def afterAll {
    FileUtils.deleteDirectory(WorkDir)
  }

  test("list repositories") { handler => pending
    val res = handler(new URI("/repos")).get()
    expect(200) { res.getStatusCode() }

    // TODO body check
  }

  test("create repository") { handler => pending

    val res = handler(new URI("/repos")).post(TestJsons.registerRepo)
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

  // TODO
  test("sync repositories (pull and push)") { pending }

  test("remove repository") { handler => pending
    val res = handler(new URI("/repos/copitte")).delete()
    expect(200) { res.getStatusCode() }

    // TODO local filesystem check
  }
}
