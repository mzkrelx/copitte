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