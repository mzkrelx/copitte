/*
Copyright (c) 2012, Mizuki Yamanaka
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided 
that the following conditions are met:

1.Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2.Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/


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
