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
import javax.ws.rs.PathParam
import jp.relx.commons.CommandExecuteUtil
import org.slf4j.LoggerFactory
import jp.relx.commons.CommandFailedException
import java.io.File
  
@BeanInfo
case class RepoInfo(vcs: String, name: String, pullurl: String, pushurl: String) {
  def this() = {
    this("", "", "", "")
  }
}

@BeanInfo
case class Author(name: String, email: String) {
  def this() = {
    this("", "")
  }
}
@BeanInfo
case class Commit(id: String, message: String, timestamp: String, url: String,
  added: String, removed: String, modified: String, author: Author) {
  def this() = {
    this("", "", "", "", "", "", "", Author("", ""))
  }
}
@BeanInfo
case class Owner(name: String, email: String) {
  def this() = {
    this("", "")
  }
}
@BeanInfo
case class Repository(name: String, url: String, pledgie: String,
  description: String, homepage: String, watchers: Int, forks: Int,
  rprivate: Int, owner: Owner) {
  def this() = {
    this("", "", "", "", "", 0, 0, 0, Owner("", ""))
  }
}

@BeanInfo
case class PostReceiveInfo(before: String, after: String, ref: String,
  commits: List[Commit], repository: Repository) {
  def this() = {
    this("", "", "", List[Commit](), 
      Repository("", "", "", "", "", 0, 0, 0, Owner("", "")))
  }
}

@Path("/repos")
class RepositoryResource {

  // TODO プロパティファイルに定義を移行する
  val OutPath = "/home/charles/copitte"
    
  // .git/config に書く remote の名前
  val PushRepoName = "pushRepo"

  
  val logger = LoggerFactory.getLogger(getClass) 
  
  def getLocalRepoPath(repoName: String): String = {
    OutPath + "/" + repoName
  }
  
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
      
      // TODO OutPath ディレクトリが空であるかチェックする
      val localRepoPath = getLocalRepoPath(repoInfo.name)
      
      /**
       * git clone します。
       * 
       * @param repoInfo      リポジトリ情報
       * @param localRepoPath クローンするディレクトリのパス
       * @return  (終了コード, 標準出力, 標準エラー出力)
       */
      def gitClone(): (Int, String, String) = 
        CommandExecuteUtil.execCommand("git clone " + repoInfo.pullurl + " " + localRepoPath)

      gitClone() match {
        case (0, o, _) => logger.debug(o)
        case (_, _, e) => throw new CommandFailedException(e)
      }
      
      addRemoteConfig() match {
        case (0, o, _) => logger.debug(o)
        case (_, _, e) => throw new CommandFailedException(e)
      }
      
      /**
       * クローンしたリポジトリの設定に push 先リポジトリの情報を追加
       * git remote add [name] [pushurl] と同義
       */
      def addRemoteConfig(): (Int, String, String) = 
        CommandExecuteUtil.execCommand(
          "git remote add " + repoInfo.name + " " + repoInfo.pushurl, 30 * 1000L,
          new File(localRepoPath)
        )
      
      val res =
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>Repogitory was registerd.</body>
        </html>
      Response.ok(res.toString()).build()
    } catch {
      case e: IllegalArgumentException => Response.status(400).build()
    }
  }
  
  @POST
  @Path("{repoName}")
  def postReceive(@PathParam("repoName")repoName: String, bodyStr: String): Response = {
    def getPostReceiveInfo() = {
      val postReceiveInfo = SJSON.in[PostReceiveInfo](JsValue.fromString(bodyStr))
      require(postReceiveInfo.repository.name != "")
      postReceiveInfo
    }
    
    try {
      val postReceiveInfo = getPostReceiveInfo()
      val localRepoPath = getLocalRepoPath(postReceiveInfo.repository.name)
      
      CommandExecuteUtil.execCommand(
        "git pull origin master", 30 * 1000L,
        new File(localRepoPath)
      ) match {
        case (0, o, _) => logger.debug(o)
        case (_, _, e) => throw new CommandFailedException(e)
      }
      CommandExecuteUtil.execCommand(
        "git push " + PushRepoName + " master", 30 * 1000L,
        new File(localRepoPath)
      ) match {
        case (0, o, _) => logger.debug(o)
        case (_, _, e) => throw new CommandFailedException(e)
      }  
      val res = 
        <html xmlns="http://www.w3.org/1999/xhtml">
          <body>Thank you. Repositories ware merged.</body>
        </html>
      Response.ok(res.toString()).build()
    } catch {
      case e: IllegalArgumentException => {
        logger.error(e.getMessage())
        Response.status(400).build()
      }
      case e: CommandFailedException => {
        logger.error(e.getMessage())
        Response.status(500).build()
      }
    }
  }

}
