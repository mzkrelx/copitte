package jp.relx.copitte

import sys.env

object Const {

  lazy val CopitteHome = {
    env.getOrElse(
      "COPITTE_HOME", 
      env.getOrElse("HOME", ".") + "/.copitte" // "~/.copitte"としないのは"~"が入ると例外が発生してしまうため。
    )
  }
  
}