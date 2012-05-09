package jp.relx.copitte

import dispatch.json.JsValue
import javax.ws.rs.POST
import javax.ws.rs.Path
import sjson.json.Serializer.SJSON
import sjson.json.Serializer
import javax.ws.rs.core.Response
import sjson.json.DefaultProtocol
import sjson.json.Format
import scala.reflect.BeanInfo
  
@BeanInfo
case class RepoInfo(vcs: String, name: String, pullurl: String, pushurl: String) {
  def this() = {
    this("", "", "", "")
  }
}

@Path("/repos")
class RepositoryResource {

  @POST
  def registerRepo(bodyStr: String): Response = {
    
    def getRepoInfo(): RepoInfo = {
      val repoInfo = SJSON.in[RepoInfo](JsValue.fromString(bodyStr))
      require(repoInfo.vcs != "")
      require(repoInfo.name != "")
      require(repoInfo.pullurl != "")
      require(repoInfo.pushurl != "")
      repoInfo
    }

    try {
      val repoInfo = getRepoInfo()
      val res =
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>Repogitory was registerd.{repoInfo}</body>
        </html>
      Response.ok(res.toString()).build()
    } catch {
      case e: IllegalArgumentException => Response.status(400).build()
    }
  }

}
