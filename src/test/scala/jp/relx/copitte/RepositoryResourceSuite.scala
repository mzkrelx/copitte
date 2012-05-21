package jp.relx.copitte

import java.io.File
import java.net.URI

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.sys.process.Process
import scala.util.matching.Regex
import scala.xml.XML

import org.apache.commons.io.FileUtils.copyDirectoryToDirectory
import org.apache.commons.io.FileUtils.deleteDirectory
import org.apache.commons.io.FileUtils.forceMkdir
import org.apache.commons.io.FileUtils.getTempDirectory
import org.scalatest.fixture.FunSuite
import org.scalatest.BeforeAndAfterAll
import org.slf4j.LoggerFactory

import jp.relx.copitte.test.ResourceHandleFixture
import jp.relx.copitte.test.TestJsons
import net.liftweb.json.DefaultFormats

class RepositoryResourceSuite extends FunSuite
  with ResourceHandleFixture with BeforeAndAfterAll {
  
  val logger = LoggerFactory.getLogger(getClass)

  val WorkDir = new File(getTempDirectory(), "copitte_test")
  
  val clonedRepo = new File(Const.CopitteHome, "repos/copitte")
  
  implicit val formats = DefaultFormats

  override def beforeAll {
    forceMkdir(WorkDir)
    val resourcesDir = new File("src/test/resources")
    List("onecommit-src.git", "blank-dest.git") foreach { orig =>
      copyDirectoryToDirectory(new File(resourcesDir, orig), WorkDir)
    }
    // TODO CopitteHome のバックアップをして、空にする
  }

  override def afterAll {
    deleteDirectory(WorkDir)
    // TODO CopitteHome をもとにもどす
  }

  test("create repository") { handler =>
    val srcRepo  = "file://" +
      new File(WorkDir, "onecommit-src.git").getCanonicalPath()
    val destRepo = "file://" +
      new File(WorkDir, "blank-dest.git").getCanonicalPath()

    val res = handler(new URI("/repos")).post(
        TestJsons.registerRepo(srcRepo, destRepo))

    expect(201) { res.getStatusCode() }

    expect("/repos/copitte") {
      res.getHeaders().asScala.get("Location") match {
        case None => "Location header not found."
        case Some(x) if x.length == 1 =>
          try {
            new URI(x.head).getPath()
          } catch {
            case e => "uri parse failed."
          }
        case Some(x) => "too many location headers."
      }
    }

    // TODO 他の場所に移したほうがよいかも
    val grepInRepo = (cmd: String, reg: Regex) => {
      val lins = Process(cmd, clonedRepo).lines.toList
      logger.info(lins.toString())
      lins filter {
        case reg(_) => true
        case _ => false
      } map { case reg(ptn) => ptn }
    }

    expect(srcRepo) {
      grepInRepo("git remote show origin ", """^\s+Fetch URL: (.+)$""".r).
        headOption.getOrElse("Fetch URL not found.")
    }

    expect(destRepo) {
      grepInRepo("git remote show -n copitte-push-repo ", """^\s+Push  URL: (.+)$""".r).
        headOption.getOrElse("Push URL not found.")
    }
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

    assert(!clonedRepo.exists())
  }
}
