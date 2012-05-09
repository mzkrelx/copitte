package jp.relx.commons

/**
 * I/O関連ユーティリティ
 */
object IoUtil {

  /**
   * try-finally-close の処理を隠蔽する関数
   *
   * @param closeable クローズ処理が必要なオブジェクト
   * @param f 第一引数に対して行いたい処理
   * @return 第二引数の処理結果
   */
  def using[A <: { def close() }, B](closeable: A)(f: A => B): B =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
}