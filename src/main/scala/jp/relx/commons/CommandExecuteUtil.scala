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

package jp.relx.commons
import java.io.InputStream
import java.util.concurrent.TimeoutException
import scala.actors.Futures.awaitAll
import scala.actors.Futures.future
import scala.io.Source
import jp.relx.commons.IoUtil.using
import java.io.File

/**
 * 外部コマンド実行関連ユーリティリティ
 */
object CommandExecuteUtil {

  /**
   * 外部コマンドを実行してその結果を返す
   *
   * @param cmd 外部コマンド文字列
   * @param timeout 外部コマンドの終了待ち時間
   * @param dir 作業ディレクトリ
   * @return (終了コード, 標準出力, 標準エラー出力)
   * @throws TimeoutException
   */
  def execCommand(cmd: String, timeout: Long = 0L, dir: File = null): (Int, String, String) = {
    val process = Runtime.getRuntime().exec(cmd, null, dir)
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