package jp.relx.copitte
import org.scalatest.fixture.FunSuite
import jp.relx.copitte.test.ResourceHandleFixture
import scala.xml.XML
import java.net.URI

class ScalateSuite  extends FunSuite with ResourceHandleFixture {

  test("hello test") { handler =>
    val res = handler(new URI("/repos/create-form")).get()
    expect(200) { res.getStatusCode() }

    val body = XML.loadString(res.getEntity(classOf[String]))
    println(body)
  }
}