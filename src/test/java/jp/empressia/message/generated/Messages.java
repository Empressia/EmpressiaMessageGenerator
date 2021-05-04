package jp.empressia.message.generated;

import jakarta.annotation.Generated;

/**
 * メッセージの簡易呼び出しのためのアダプターです。
 * @author すふぃあ
 */
@Generated("jp.empressia.message.generator.MessageGenerator")
public class Messages {

	/**
	 * 『テストメッセージです。』
	 */
	public static String TEST000() {
		return jp.empressia.message.generated.Message.TEST000.format();
	}
	/**
	 * 『テストメッセージです（{0}）。』
	 * @param arg0 メッセージに埋め込む1個目のオブジェクト
	 */
	public static String TEST001(Object arg0) {
		return jp.empressia.message.generated.Message.TEST001.format(arg0);
	}
	/**
	 * 『テストメッセージです（{0}）（{1}）。』
	 * @param arg0 メッセージに埋め込む1個目のオブジェクト
	 * @param arg1 メッセージに埋め込む2個目のオブジェクト
	 */
	public static String TEST002(Object arg0, Object arg1) {
		return jp.empressia.message.generated.Message.TEST002.format(arg0, arg1);
	}
	/**
	 * 『テストメッセージです（{0}）（{1}）（{2}）。』
	 * @param arg0 メッセージに埋め込む1個目のオブジェクト
	 * @param arg1 メッセージに埋め込む2個目のオブジェクト
	 * @param arg2 メッセージに埋め込む3個目のオブジェクト
	 */
	public static String TEST003(Object arg0, Object arg1, Object arg2) {
		return jp.empressia.message.generated.Message.TEST003.format(arg0, arg1, arg2);
	}

}
