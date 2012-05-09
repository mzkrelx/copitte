package jp.relx.commons
import scala.io.Source
import java.io.InputStream

object CommandExecuteUtil {

  /**
   * 外部コマンドを実行してその結果を返す
   *
   * @param 外部コマンド文字列
   * @return (終了コード, 標準出力, 標準エラー出力)
   */
  def execCommand(cmd: String): (Int, String, String) = {
    val process = Runtime.getRuntime().exec(cmd)
    try {
      using(process.getOutputStream())(s => ())
      val stdout = using(process.getInputStream()) { toString }
      val stderr = using(process.getErrorStream()) { toString }

      (process.waitFor(), stdout, stderr)
    } finally {
      process.destroy()
    }
  }

  /**
   * InputStream を文字列に変換する関数
   */
  def toString(is: InputStream): String =
    Source.fromInputStream(is).getLines() mkString "\n"

  /**
   * try-finally-close の処理を隠蔽する関数
   *
   * @param クローズ処理が必要なオブジェクト
   * @param 第一引数に対して行いたい処理
   * @return 第二引数の処理結果
   */
  def using[A <: { def close() }, B](res: A)(f: A => B): B =
    try {
      f(res)
    } finally {
      res.close()
    }
}