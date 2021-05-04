package jp.empressia.message.generated;

import java.util.Locale;
import jakarta.annotation.Generated;

import jp.empressia.message.MessageTemplate;
import jp.empressia.message.generated.Message.MessageTemplateFor0Args;
import jp.empressia.message.generated.Message.MessageTemplateFor1Args;
import jp.empressia.message.generated.Message.MessageTemplateFor2Args;
import jp.empressia.message.generated.Message.MessageTemplateFor3Args;

/**
 * アプリケーションドメインにおける例外を表現します。
 * @author すふぃあ}
 */
@Generated("jp.empressia.message.generator.MessageGenerator")
public class MessageException extends RuntimeException {

	/** メッセージ、および、メッセージのテンプレートです。 */
	private MessageTemplate Message;
	/** メッセージ引数です。 */
	private Object[] MessageArgs;
	
	/** メッセージIDを取得します。 */
	public String getMessageID() { return this.Message.getID(); }
	/** メッセージ引数を取得します。 */
	public Object[] getMessageArgs() { return this.MessageArgs; }
	
	private Formatter Formatter;
	
	/** メッセージを取得します。 */
	public String getMessage() {
		String message = this.Formatter.format(null);
		return message;
	}
	
	/** メッセージを取得します。 */
	public String getMessage(Locale locale) {
		String message = this.Formatter.format(locale);
		return message;
	}
	
	/**
	 * コンストラクタです。
	 * @param Message messages.propertiesにあるメッセージIDに対応するMessage定数
	 * @param MessageArgs メッセージに埋め込む引数
	 * @param Formatter メッセージをフォーマットする関数
	 */
	private MessageException(MessageTemplate Message, Object[] MessageArgs, Formatter Formatter) {
		super(Message.getID());
		this.Message = Message;
		this.MessageArgs = MessageArgs;
		this.Formatter = Formatter;
	}

	/**
	 * コンストラクタです。
	 * @param message messages.propertiesにあるメッセージIDに対応するMessage定数
	 */
	public MessageException(MessageTemplateFor0Args message) {
		this(message, new Object[] {}, (l) -> message.format(l));
	}
	/**
	 * コンストラクタです。
	 * @param message messages.propertiesにあるメッセージIDに対応するMessage定数
	 * @param arg0 メッセージに埋め込む1個目のオブジェクト
	 */
	public MessageException(MessageTemplateFor1Args message, Object arg0) {
		this(message, new Object[] {arg0}, (l) -> message.format(arg0, l));
	}
	/**
	 * コンストラクタです。
	 * @param message messages.propertiesにあるメッセージIDに対応するMessage定数
	 * @param arg0 メッセージに埋め込む1個目のオブジェクト
	 * @param arg1 メッセージに埋め込む2個目のオブジェクト
	 */
	public MessageException(MessageTemplateFor2Args message, Object arg0, Object arg1) {
		this(message, new Object[] {arg0, arg1}, (l) -> message.format(arg0, arg1, l));
	}
	/**
	 * コンストラクタです。
	 * @param message messages.propertiesにあるメッセージIDに対応するMessage定数
	 * @param arg0 メッセージに埋め込む1個目のオブジェクト
	 * @param arg1 メッセージに埋め込む2個目のオブジェクト
	 * @param arg2 メッセージに埋め込む3個目のオブジェクト
	 */
	public MessageException(MessageTemplateFor3Args message, Object arg0, Object arg1, Object arg2) {
		this(message, new Object[] {arg0, arg1, arg2}, (l) -> message.format(arg0, arg1, arg2, l));
	}

	/**
	 * メッセージをフォーマットするためのインターフェースです。
	 * @author すふぃあ
	 */
	@FunctionalInterface
	public interface Formatter {
		/**
		 * メッセージをフォーマットします。
		 * @param locale 指定がない場合はnullです。
		 */
		public String format(Locale locale);
	}

}
