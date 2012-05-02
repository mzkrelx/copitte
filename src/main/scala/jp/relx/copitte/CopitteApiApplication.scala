package jp.relx.copitte

import javax.ws.rs.core.Application
import scala.collection.JavaConverters._

class CopitteApiApplication extends Application {
  override def getClasses(): java.util.Set[Class[_]] = {
    Set[Class[_]](
        classOf[HelloResource]
    ).asJava
  }
}