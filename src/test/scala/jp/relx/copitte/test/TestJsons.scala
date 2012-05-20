package jp.relx.copitte.test
import java.net.URL

object TestJsons {
  def registerRepo(pullUrl: String, pushUrl: String): String = ("""{
    |    "vcs": "git",
    |    "name": "copitte",
    |    "pullurl": """" + pullUrl + """",
    |    "pushurl": """" + pushUrl + """"
    |}""") stripMargin
}