package jp.relx.copitte.servlet

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import java.io.File
import java.io.IOException
import org.slf4j.LoggerFactory
import sys.env
import jp.relx.copitte.Const

class ContextListener extends ServletContextListener {
  
  val logger = LoggerFactory.getLogger(getClass)
    
  override def contextInitialized(arg0: ServletContextEvent) {
    val homeDir = new File(Const.CopitteHome)
    if (!homeDir.exists()) {
      homeDir.mkdir() match {
        case false => {
          val msg = "Can't create Copitte home dir [" + homeDir + "]."
          logger.error(msg)
          throw new IOException(msg)
        }
        case true => logger.info("Copitte home dir was created.  [" + homeDir + "]")
      }
    }
    logger.info("Using [" + homeDir + "] as Coppite home dir.")
  }

  override def contextDestroyed(arg0: ServletContextEvent) {}
  
}