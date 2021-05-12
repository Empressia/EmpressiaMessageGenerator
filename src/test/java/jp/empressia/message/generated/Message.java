package jp.empressia.message.generated;

import java.util.Locale;
import jakarta.annotation.Generated;

import jp.empressia.message.MessageTemplate;

/**
 * メッセージを表現します。
 * @author すふぃあ
 */
@Generated("jp.empressia.message.generator.MessageGenerator")
public class Message {

	/**
	 * メッセージのIDを表現します。
	 * @author すふぃあ
	 */
	@Generated("jp.empressia.message.generator.MessageGenerator")
	public static enum ID {
		/** 『{{}}{{}{}{{{}{}}}}』 */
		BALANCE01("BALANCE01"),
		/** {@literal 『@{@{@}@}@{@{@}@{@}@{@{@{@}@{@}@}@}@}@』} */
		BALANCE02("BALANCE02"),
		/** {@literal 『@}{{@literal @{@{@{@}@}@}@}{{@literal @{@}』} */
		EBRACE01("EBRACE01"),
		/** {@literal 『@}}{@literal @{@{@}@}@}}{@literal @}{{@literal @}{{@literal @}{{@literal @}{』 */
		EBRACE02("EBRACE02"),
		/** {@literal 『/*コメント*}{@literal /』} */
		ESCAPE01("ESCAPE01"),
		/** {@literal 『&amp;』} */
		ESCAPE02("ESCAPE02"),
		/** {@literal 『<div>』} */
		ESCAPE03("ESCAPE03"),
		/** {@literal 『<div>/*コメント&amp;*}{@literal /</div>』} */
		ESCAPE04("ESCAPE04"),
		/** 『{{{{}}}{{}』 */
		NBRACE01("NBRACE01"),
		/** 『}{{}}}{{{{』 */
		NBRACE02("NBRACE02"),
		/** 『テストメッセージです。』 */
		TEST000("TEST000"),
		/** 『テストメッセージです（{0}）。』 */
		TEST001("TEST001"),
		/** 『テストメッセージです（{0}）（{1}）。』 */
		TEST002("TEST002"),
		/** 『テストメッセージです（{0}）（{1}）（{2}）。』 */
		TEST003("TEST003"),
		;
		/** IDの文字列表現です。 */
		private String IDString;
		/** コンストラクタ。 */
		private ID(String IDString) {
			this.IDString = IDString;
		}
		/** IDの文字列表現を提供します。 */
		public String getAsString() {
			return this.IDString;
		}
	}

	/** 『{{}}{{}{}{{{}{}}}}』 */
	public static final MessageTemplateFor0Args BALANCE01 = new MessageTemplateFor0Args(ID.BALANCE01);
	/** {@literal 『@{@{@}@}@{@{@}@{@}@{@{@{@}@{@}@}@}@}@』} */
	public static final MessageTemplateFor0Args BALANCE02 = new MessageTemplateFor0Args(ID.BALANCE02);
	/** {@literal 『@}{{@literal @{@{@{@}@}@}@}{{@literal @{@}』} */
	public static final MessageTemplateFor0Args EBRACE01 = new MessageTemplateFor0Args(ID.EBRACE01);
	/** {@literal 『@}}{@literal @{@{@}@}@}}{@literal @}{{@literal @}{{@literal @}{{@literal @}{』 */
	public static final MessageTemplateFor0Args EBRACE02 = new MessageTemplateFor0Args(ID.EBRACE02);
	/** {@literal 『/*コメント*}{@literal /』} */
	public static final MessageTemplateFor0Args ESCAPE01 = new MessageTemplateFor0Args(ID.ESCAPE01);
	/** {@literal 『&amp;』} */
	public static final MessageTemplateFor0Args ESCAPE02 = new MessageTemplateFor0Args(ID.ESCAPE02);
	/** {@literal 『<div>』} */
	public static final MessageTemplateFor0Args ESCAPE03 = new MessageTemplateFor0Args(ID.ESCAPE03);
	/** {@literal 『<div>/*コメント&amp;*}{@literal /</div>』} */
	public static final MessageTemplateFor0Args ESCAPE04 = new MessageTemplateFor0Args(ID.ESCAPE04);
	/** 『{{{{}}}{{}』 */
	public static final MessageTemplateFor0Args NBRACE01 = new MessageTemplateFor0Args(ID.NBRACE01);
	/** 『}{{}}}{{{{』 */
	public static final MessageTemplateFor0Args NBRACE02 = new MessageTemplateFor0Args(ID.NBRACE02);
	/** 『テストメッセージです。』 */
	public static final MessageTemplateFor0Args TEST000 = new MessageTemplateFor0Args(ID.TEST000);
	/** 『テストメッセージです（{0}）。』 */
	public static final MessageTemplateFor1Args TEST001 = new MessageTemplateFor1Args(ID.TEST001);
	/** 『テストメッセージです（{0}）（{1}）。』 */
	public static final MessageTemplateFor2Args TEST002 = new MessageTemplateFor2Args(ID.TEST002);
	/** 『テストメッセージです（{0}）（{1}）（{2}）。』 */
	public static final MessageTemplateFor3Args TEST003 = new MessageTemplateFor3Args(ID.TEST003);

	/** このクラスに対応するメッセージの場所です。 */
	private static final String Location = "message";

	/**
	 * 埋め込みを0個持つメッセージです。
	 * @author すふぃあ
	 */
	@Generated("jp.empressia.message.generator.MessageGenerator")
	public static class MessageTemplateFor0Args extends MessageTemplate {
		/** コンストラクタ。 */
		private MessageTemplateFor0Args(ID ID) { super(ID.getAsString()); }
		/** メッセージを構築して提供します。 */
		public String format() {
			return this.format(Message.Location, new Object[] {});
		}
		/** メッセージを構築して提供します。 */
		public String format(Locale locale) {
			return this.format(Message.Location, new Object[] {}, locale);
		}
	}
	/**
	 * 埋め込みを1個持つメッセージです。
	 * @author すふぃあ
	 */
	@Generated("jp.empressia.message.generator.MessageGenerator")
	public static class MessageTemplateFor1Args extends MessageTemplate {
		/** コンストラクタ。 */
		private MessageTemplateFor1Args(ID ID) { super(ID.getAsString()); }
		/** メッセージを構築して提供します。 */
		public String format(Object arg0) {
			return this.format(Message.Location, new Object[] {arg0});
		}
		/** メッセージを構築して提供します。 */
		public String format(Object arg0, Locale locale) {
			return this.format(Message.Location, new Object[] {arg0}, locale);
		}
	}
	/**
	 * 埋め込みを2個持つメッセージです。
	 * @author すふぃあ
	 */
	@Generated("jp.empressia.message.generator.MessageGenerator")
	public static class MessageTemplateFor2Args extends MessageTemplate {
		/** コンストラクタ。 */
		private MessageTemplateFor2Args(ID ID) { super(ID.getAsString()); }
		/** メッセージを構築して提供します。 */
		public String format(Object arg0, Object arg1) {
			return this.format(Message.Location, new Object[] {arg0, arg1});
		}
		/** メッセージを構築して提供します。 */
		public String format(Object arg0, Object arg1, Locale locale) {
			return this.format(Message.Location, new Object[] {arg0, arg1}, locale);
		}
	}
	/**
	 * 埋め込みを3個持つメッセージです。
	 * @author すふぃあ
	 */
	@Generated("jp.empressia.message.generator.MessageGenerator")
	public static class MessageTemplateFor3Args extends MessageTemplate {
		/** コンストラクタ。 */
		private MessageTemplateFor3Args(ID ID) { super(ID.getAsString()); }
		/** メッセージを構築して提供します。 */
		public String format(Object arg0, Object arg1, Object arg2) {
			return this.format(Message.Location, new Object[] {arg0, arg1, arg2});
		}
		/** メッセージを構築して提供します。 */
		public String format(Object arg0, Object arg1, Object arg2, Locale locale) {
			return this.format(Message.Location, new Object[] {arg0, arg1, arg2}, locale);
		}
	}

}
