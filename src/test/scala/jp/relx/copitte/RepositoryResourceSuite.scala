package jp.relx.copitte
import java.net.URI

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

import org.apache.wink.client.Resource
import org.scalatest.fixture.FunSuite

import jp.relx.copitte.test.ResourceHandleFixture

class RepositoryResourceSuite extends FunSuite with ResourceHandleFixture {

  test("repository list test") { handler => pending
    val res = handler(new URI("/repos")).get()
    expect(200) { res.getStatusCode() }

    // TODO body check
  }

  test("repository create test") { handler => pending
    val res = handler(new URI("/repos")).post("") // TODO post body
    expect(201) { res.getStatusCode() }

    val loc = res.getHeaders().asScala.get("Location")
    expect("/repos/milm") {
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
  }
  
}
