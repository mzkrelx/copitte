package jp.relx.copitte.test
import org.scalatest.AbstractSuite
import org.scalatest.fixture.Suite
import java.net.URI
import org.apache.wink.client.RestClient
import org.apache.wink.client.Resource

trait ResourceHandleFixture extends AbstractSuite {
  this: Suite =>
  type FixtureParam = (URI) => Resource
  
  override def withFixture(test: OneArgTest) {
    test { (path: URI) =>
      new RestClient().resource(
        TestConfigs.AppContextUri.resolve(path))
    }
  }
}
