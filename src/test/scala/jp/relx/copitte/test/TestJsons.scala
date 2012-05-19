package jp.relx.copitte.test
import java.net.URL

object TestJsons {
  def registerRepo(pushUrl: String, pullUrl: String): String = ("""{
    |    "vcs": "git",
    |    "name": "copitte",
    |    "pullurl": """" + pullUrl + """",
    |    "pushurl": """" + pushUrl + """"
    |}""") stripMargin
}