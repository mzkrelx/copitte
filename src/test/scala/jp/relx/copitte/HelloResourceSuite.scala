package jp.relx.copitte
import org.scalatest.fixture.FunSuite
import org.apache.wink.client.RestClient
import org.apache.wink.client.Resource
import java.net.URI
import scala.xml.XML
import jp.relx.copitte.test.TestConfigs

class RepositoryResourceSuite extends FunSuite {

  type FixtureParam = (URI) => Resource

  override def withFixture(test: OneArgTest) {
    test { (path: URI) =>
      new RestClient().resource(
        TestConfigs.AppContextUri.resolve(path))
    }
  }

  test("hello test") { handler =>
    val res = handler(new URI("/")).get()
    expect(200) { res.getStatusCode() }

    val body = XML.loadString(res.getEntity(classOf[String]))
    expect("Hello, World") {
      body \ "body" text
    }
  }
}