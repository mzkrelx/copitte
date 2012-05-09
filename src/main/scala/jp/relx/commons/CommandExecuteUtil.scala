package jp.relx.commons
import java.io.InputStream
import java.util.concurrent.TimeoutException

import scala.actors.Futures.awaitAll
import scala.actors.Futures.future
import scala.io.Source

import jp.relx.commons.IoUtil.using

/**
 * 外部コマンド実行関連ユーリティリティ
 */
object CommandExecuteUtil {

  /**
   * 外部コマンドを実行してその結果を返す
   *
   * @param cmd 外部コマンド文字列
   * @param timeout 外部コマンドの終了待ち時間
   * @return (終了コード, 標準出力, 標準エラー出力)
   * @throws TimeoutException
   */
  def execCommand(cmd: String, timeout: Long = 0L): (Int, String, String) = {
    val process = Runtime.getRuntime().exec(cmd)
    try {
      process.getOutputStream().close()
      val stdout = future { using(process.getInputStream()) { toString } }
      val stderr = future { using(process.getErrorStream()) { toString } }

      timeout match {
        case t if t > 0L => {
          awaitAll(t, future { process.waitFor() }).head match {
            case None => throw new TimeoutException(
                "command '" + cmd + "' has timeout. (" + timeout + "ms)")
            case Some(status: Int) => (status, stdout(), stderr())
          }
        }
        case _ => (process.waitFor(), stdout(), stderr())
      }
    } finally {
      process.destroy()
    }
  }

  /**
   * InputStream を文字列に変換する関数
   */
  private def toString(is: InputStream): String =
    Source.fromInputStream(is).getLines() mkString "\n"
}