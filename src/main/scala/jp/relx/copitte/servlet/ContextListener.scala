package jp.relx.copitte.servlet

import java.io.File
import scala.sys.env
import org.slf4j.LoggerFactory
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import jp.relx.copitte.Const
import java.io.IOException

class ContextListener extends ServletContextListener {
  
  val logger = LoggerFactory.getLogger(getClass)
    
  override def contextInitialized(arg0: ServletContextEvent) {
    mkdir(Const.CopitteHome)
    logger.info("Using [" + Const.CopitteHome + "] as Coppite home dir.")
    
    mkdir(Const.ReposDirPath)

    def mkdir(path: String) {
      val dir = new File(path)
      if (!dir.exists()) {
        dir.mkdir() match {
          case false => {
            val msg = "Can't create dir [" + path + "]."
            logger.error(msg)
            throw new IOException(msg)
          }
          case true => logger.info("Directory was created.  [" + path + "]")
        }
      }
    }
  }

  override def contextDestroyed(arg0: ServletContextEvent) {}
  
}