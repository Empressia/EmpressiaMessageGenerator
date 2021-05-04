package jp.empressia.message.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.PropertyResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jp.empressia.picocog.writer.EPicoWriter;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * メッセージクラスを生成します。
 * @author すふぃあ
 */
public class MessageGenerator {

	/** 入力となるメッセージプロパティファイルの候補です。 */
	public static final String[] DEFAULT_FILEPATHS = { "src/main/resources/message.properties", "src/main/resources/messages.properties" };
	/** 著作者の初期値です。 */
	public static final String DEFAULT_AUTHOR = "すふぃあ";
	/** 出力先となるソースディレクトリの初期値です。 */
	public static final String DEFAULT_SOuRTH_DIRECTORY_PATH = "src/main/java/";
	/** パッケージの名前の初期値です。 */
	public static final String DEFAULT_PACKAGE_NAME = "jp.empressia.message.generated";
	/** メッセージクラスの名前の初期値です。 */
	public static final String DEFAULT_MESSAGE_CLASS_NAME = "Message";
	/** 例外クラスの名前の初期値です。 */
	public static final String DEFAULT_EXCEPTION_CLASS_NAME = "MessageException";
	/** アダプタークラスの名前の初期値です。 */
	public static final String DEFAULT_ADAPTOR_CLASS_NAME = "Messages";

	/** Generatorようの設定です。 */
	private Configuration Configuration;

	/** entry point。 */
	public static void main(String[] args) {
		var c = new CommandLine(new Configuration());
		c.parseArgs(args);
		Configuration configuration = c.getCommand();
		if(configuration.Help) {
			c.getCommandSpec().usageMessage().sortOptions(false);
			c.usage(System.out);
			return;
		}
		MessageGenerator generator = new MessageGenerator(configuration);
		generator.perform();
	}
	/** コンストラクタ。 */
	public MessageGenerator() {
		this(null);
	}
	/** コンストラクタ。 */
	public MessageGenerator(Configuration Configuration) {
		Configuration c = Configuration;
		if(Configuration == null) {
			c = new Configuration();
		}
		this.Configuration = c;
	}
	/** Generatorを実行します。 */
	public void perform() {
		Collection<MessageInformation> MessageClassInformations;
		try {
			MessageClassInformations = MessageGenerator.loadMessageInformations(this.Configuration);
		} catch(FailedToAutoDetectException ex) {
			// ファイルの自動検出で対象が見つからなかった。
			// 管理する対象がないので、特に何もする必要はない。
			return;
		}
		MessageClassInformation MessageClassInformation = MessageGenerator.loadMessageClassInformation(this.Configuration);
		{
			this.outputMessageClass(MessageClassInformation, MessageClassInformations);
		}
		if(this.Configuration.SuppressOutputExceptionClass == false) {
			ExceptionClassInformation ExceptionClassInformation = MessageGenerator.loadExceptionClassInformation(this.Configuration);
			this.outputExceptionClass(ExceptionClassInformation, MessageClassInformation, MessageClassInformations);
		}
		if(this.Configuration.SuppressOutputAdaptorClass == false) {
			AdaptorClassInformation AdaptorClassInformation = MessageGenerator.loadAdaptorClassInformation(this.Configuration);
			this.outputAdaptorClass(AdaptorClassInformation, MessageClassInformation, MessageClassInformations);
		}
	}

	/** メッセージの一覧を読み込みます。 */
	private static Collection<MessageInformation> loadMessageInformations(Configuration configuration) {
		Path[] filePaths = Utilities.getMessagePropertyFilePaths(configuration);
		// 全ファイルの内容を取得する。
		Stream<MessageInformation> allMessages = Stream.empty();
		for(Path filePath : filePaths) {
			PropertyResourceBundle b;
			try(Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
				b = new PropertyResourceBundle(reader);
			} catch(IOException ex) {
				throw new UncheckedIOException(ex);
			}
			// PropertyResourceBundleでは特にソート順は決まっていないみたいだから、適当にソートする。
			allMessages = Stream.concat(allMessages, b.keySet().stream().sorted().map(key -> new MessageInformation(key, b.getString(key))));
		}
		// すべてのメッセージから、同じキーのものをマージする。
		LinkedHashMap<String, MessageInformation> messageMap = allMessages.collect(
			// キーごとにまとめて、原則、先のものを有効にするけど、
			// 先のものよりもArgCountが大きいものがあったらそちらを有効にします。
			Collectors.groupingBy(mi -> mi.Key, LinkedHashMap<String, MessageInformation>::new, Collectors.reducing(null, (l, r) -> ((l != null) && (l.ArgCount >= r.ArgCount)) ? l : r))
		);
		Collection<MessageInformation> messages = messageMap.values();
		return messages;
	}
	/** メッセージクラスの情報を読み込みます。 */
	private static MessageClassInformation loadMessageClassInformation(Configuration configuration) {
		ClassInformation classInformation;
		try {
			classInformation = Utilities.createClassInformation(configuration.MessageClassName, configuration.PackageName, DEFAULT_PACKAGE_NAME, DEFAULT_MESSAGE_CLASS_NAME);
		} catch(EmptyClassNameException ex) {
			throw new EmptyClassNameException("メッセージクラスの名前が空文字になっています。", ex);
		}
		MessageClassInformation MessageClassInformation = new MessageClassInformation(classInformation);
		MessageClassInformation.SourceDirectoryPath = Utilities.getSourceDirectoryPath(configuration, DEFAULT_SOuRTH_DIRECTORY_PATH);
		MessageClassInformation.Author = (configuration.Author != null) ? configuration.Author : DEFAULT_AUTHOR;
		MessageClassInformation.CommentoutGeneratedAnnotation = configuration.CommentoutGeneratedAnnotation;
		MessageClassInformation.CreateMessageIDConstants = configuration.CreateMessageIDConstants;
		return MessageClassInformation;
	}
	/** 例外クラスの情報を読み込みます。 */
	private static ExceptionClassInformation loadExceptionClassInformation(Configuration configuration) {
		ClassInformation classInformation;
		try {
			classInformation = Utilities.createClassInformation(configuration.ExceptionClassName, configuration.PackageName, DEFAULT_PACKAGE_NAME, DEFAULT_EXCEPTION_CLASS_NAME);
		} catch(EmptyClassNameException ex) {
			throw new EmptyClassNameException("例外クラスの名前が空文字になっています。", ex);
		}
		ExceptionClassInformation ExceptionClassInformation = new ExceptionClassInformation(classInformation);
		ExceptionClassInformation.SourceDirectoryPath = Utilities.getSourceDirectoryPath(configuration, DEFAULT_SOuRTH_DIRECTORY_PATH);
		ExceptionClassInformation.Author = (configuration.Author != null) ? configuration.Author : DEFAULT_AUTHOR;
		ExceptionClassInformation.CommentoutGeneratedAnnotation = configuration.CommentoutGeneratedAnnotation;
		return ExceptionClassInformation;
	}
	/** アダプタークラスの情報を読み込みます。 */
	private static AdaptorClassInformation loadAdaptorClassInformation(Configuration configuration) {
		ClassInformation classInformation;
		try {
			classInformation = Utilities.createClassInformation(configuration.AdaptorClassName, configuration.PackageName, DEFAULT_PACKAGE_NAME, DEFAULT_ADAPTOR_CLASS_NAME);
		} catch(EmptyClassNameException ex) {
			throw new EmptyClassNameException("アダプタークラスの名前が空文字になっています。", ex);
		}
		AdaptorClassInformation AdaptorClassInformation = new AdaptorClassInformation(classInformation);
		AdaptorClassInformation.SourceDirectoryPath = Utilities.getSourceDirectoryPath(configuration, DEFAULT_SOuRTH_DIRECTORY_PATH);
		AdaptorClassInformation.Author = (configuration.Author != null) ? configuration.Author : DEFAULT_AUTHOR;
		AdaptorClassInformation.CommentoutGeneratedAnnotation = configuration.CommentoutGeneratedAnnotation;
		return AdaptorClassInformation;
	}

	/** メッセージクラスを出力します。 */
	private void outputMessageClass(MessageClassInformation MessageClassInformation, Collection<MessageInformation> messageInformations) {
		String generatedAnnotattionCommentOutPrefix = MessageClassInformation.CommentoutGeneratedAnnotation ? "// " : "";
		EPicoWriter classWriter = new EPicoWriter();
		classWriter.writeln_n("package {0};", MessageClassInformation.PackageName);
		classWriter.writeln_n("");
		classWriter.writeln_n("import java.util.Locale;");
		classWriter.writeln_n("{0}import jakarta.annotation.Generated;", generatedAnnotattionCommentOutPrefix);
		classWriter.writeln_n("");
		classWriter.writeln_n("import jp.empressia.message.MessageTemplate;");
		classWriter.writeln_n("");
		classWriter.writeln_n("/**");
		classWriter.writeln_n(" * メッセージを表現します。");
		classWriter.writeln_n(" * @author {0}", MessageClassInformation.Author);
		classWriter.writeln_n(" */");
		classWriter.writeln_n("{1}@Generated(\"{0}\")", this.getClass().getName(), generatedAnnotattionCommentOutPrefix);
		classWriter.writeln_o("public class {0} {", MessageClassInformation.ClassName);
		classWriter.writeln_n("");
		EPicoWriter innerIDClassWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		EPicoWriter messagesWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		EPicoWriter messageTemplateClassesWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		classWriter.writeln_c("}");

		innerIDClassWriter.writeln_n("/**");
		innerIDClassWriter.writeln_n(" * メッセージのIDを表現します。");
		innerIDClassWriter.writeln_n(" * @author {0}", MessageClassInformation.Author);
		innerIDClassWriter.writeln_n(" */");
		innerIDClassWriter.writeln_n("{1}@Generated(\"{0}\")", this.getClass().getName(), generatedAnnotattionCommentOutPrefix);
		innerIDClassWriter.writeln_o("public static enum ID {");
		for(MessageInformation mi : messageInformations) {
			innerIDClassWriter.writeln_n("/** 『{0}』*/", mi.Message);
			innerIDClassWriter.writeln_n("{0}(\"{1}\"),", mi.ID, mi.Key);
		}
		innerIDClassWriter.writeln_n(";");
		innerIDClassWriter.writeln_n("/** IDの文字列表現です。 */");
		innerIDClassWriter.writeln_n("private String IDString;");
		innerIDClassWriter.writeln_n("/** コンストラクタ。 */");
		innerIDClassWriter.writeln_n("private ID(String IDString) {");
		innerIDClassWriter.writeln_n("	this.IDString = IDString;");
		innerIDClassWriter.writeln_n("}");
		innerIDClassWriter.writeln_n("/** IDの文字列表現を提供します。 */");
		innerIDClassWriter.writeln_n("public String getAsString() {");
		innerIDClassWriter.writeln_n("	return this.IDString;");
		innerIDClassWriter.writeln_n("}");
		EPicoWriter constantsClassWriter = innerIDClassWriter.createDeferredEPicoWriter();
		innerIDClassWriter.writeln_c("}");

		if(MessageClassInformation.CreateMessageIDConstants) {
			constantsClassWriter.writeln_n("/**");
			constantsClassWriter.writeln_n(" * メッセージのIDの文字列表現を定数で表現します。");
			constantsClassWriter.writeln_n(" * @author {0}", MessageClassInformation.Author);
			constantsClassWriter.writeln_n(" */");
			constantsClassWriter.writeln_n("{1}@Generated(\"{0}\")", this.getClass().getName(), generatedAnnotattionCommentOutPrefix);
			constantsClassWriter.writeln_o("public static class Constants {");
			for(MessageInformation mi : messageInformations) {
				constantsClassWriter.writeln_n("/** 『{0}』*/", mi.Message);
				constantsClassWriter.writeln_n("public static final String {0} = \"{1}\";", mi.ID, mi.Key);
			}
			constantsClassWriter.writeln_c("}");
		}

		for(MessageInformation mi : messageInformations) {
			messagesWriter.writeln_n("/** 『{0}』 */", mi.Message);
			messagesWriter.writeln_n("public static final MessageTemplateFor{1}Args {0} = new MessageTemplateFor{1}Args(ID.{0});", mi.ID, mi.ArgCount);
		}

		// 存在しない場合は-1になる。
		int maxArgCount = messageInformations.stream().mapToInt(mi -> mi.ArgCount).max().orElse(-1);
		for(int argCount = 0; argCount <= maxArgCount; ++argCount) {
			// 『Object arg*, Object arg*』
			String argsDefinitionString = IntStream.range(0, argCount).mapToObj(i -> "Object arg" + i).collect(Collectors.joining(", "));
			// 『Object arg*, 』
			String frontArgsDefinitionString = IntStream.range(0, argCount).mapToObj(i -> "Object arg" + i + ", ").collect(Collectors.joining(""));
			// 『arg*, arg*』
			String argsString = IntStream.range(0, argCount).mapToObj(i -> "arg" + i).collect(Collectors.joining(", "));
			messageTemplateClassesWriter.writeln_n("/**");
			messageTemplateClassesWriter.writeln_n(" * 埋め込みを{0}個持つメッセージです。", argCount);
			messageTemplateClassesWriter.writeln_n(" * @author {0}", MessageClassInformation.Author);
			messageTemplateClassesWriter.writeln_n(" */");
			messageTemplateClassesWriter.writeln_n("{1}@Generated(\"{0}\")", this.getClass().getName(), generatedAnnotattionCommentOutPrefix);
			messageTemplateClassesWriter.writeln_n("public static class MessageTemplateFor{0}Args extends MessageTemplate {", argCount);
			messageTemplateClassesWriter.writeln_n("	/** コンストラクタ。 */");
			messageTemplateClassesWriter.writeln_n("	private MessageTemplateFor{0}Args(ID ID) { super(ID.getAsString()); }", argCount);
			messageTemplateClassesWriter.writeln_n("	/** メッセージを構築して提供します。 */");
			messageTemplateClassesWriter.writeln_n("	public String format({0}) {", argsDefinitionString);
			messageTemplateClassesWriter.writeln_n("		return this.format(new Object[] {{0}});", argsString);
			messageTemplateClassesWriter.writeln_n("	}");
			messageTemplateClassesWriter.writeln_n("	/** メッセージを構築して提供します。 */");
			messageTemplateClassesWriter.writeln_n("	public String format({0}Locale locale) {", frontArgsDefinitionString);
			messageTemplateClassesWriter.writeln_n("		return this.format(new Object[] {{0}}, locale);", argsString);
			messageTemplateClassesWriter.writeln_n("	}");
			messageTemplateClassesWriter.writeln_n("	/** メッセージを構築して提供します。 */");
			messageTemplateClassesWriter.writeln_n("	public String toString({0}) {", argsDefinitionString);
			messageTemplateClassesWriter.writeln_n("		return this.toString(new Object[] {{0}});", argsString);
			messageTemplateClassesWriter.writeln_n("	}");
			messageTemplateClassesWriter.writeln_n("	/** メッセージを構築して提供します。 */");
			messageTemplateClassesWriter.writeln_n("	public String toString({0}Locale locale) {", frontArgsDefinitionString);
			messageTemplateClassesWriter.writeln_n("		return this.toString(new Object[] {{0}}, locale);", argsString);
			messageTemplateClassesWriter.writeln_n("	}");
			messageTemplateClassesWriter.writeln_n("}");
		}

		Path filePath = MessageClassInformation.getSourceFilePath();
		MessageGenerator.outputClass(filePath, classWriter);
	}

	/** 例外クラスを出力します。 */
	private void outputExceptionClass(ExceptionClassInformation ExceptionClassInformation, MessageClassInformation MessageClassInformation, Collection<MessageInformation> messageInformations) {
		String generatedAnnotattionCommentOutPrefix = ExceptionClassInformation.CommentoutGeneratedAnnotation ? "// " : "";
		EPicoWriter classWriter = new EPicoWriter();
		classWriter.writeln_n("package {0};", ExceptionClassInformation.PackageName);
		classWriter.writeln_n("");
		classWriter.writeln_n("import java.util.Locale;");
		classWriter.writeln_n("{0}import jakarta.annotation.Generated;", generatedAnnotattionCommentOutPrefix);
		classWriter.writeln_n("");
		classWriter.writeln_n("import jp.empressia.message.MessageTemplate;");
		// 存在しない場合は-1になる。
		int maxArgCount = messageInformations.stream().mapToInt(mi -> mi.ArgCount).max().orElse(-1);
		for(int argCount = 0; argCount <= maxArgCount; ++argCount) {
			classWriter.writeln_n("import {0}.{1}.MessageTemplateFor{2}Args;", MessageClassInformation.PackageName, MessageClassInformation.ClassName, argCount);
		}
		classWriter.writeln_n("");

		classWriter.writeln_n("/**");
		classWriter.writeln_n(" * アプリケーションドメインにおける例外を表現します。");
		classWriter.writeln_n(" * @author {0}}", ExceptionClassInformation.Author);
		classWriter.writeln_n(" */");
		classWriter.writeln_n("{1}@Generated(\"{0}\")", this.getClass().getName(), generatedAnnotattionCommentOutPrefix);
		classWriter.writeln_o("public class {0} extends RuntimeException {", ExceptionClassInformation.ClassName);
		classWriter.writeln_n("");
		EPicoWriter membersWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		EPicoWriter constructorsWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		EPicoWriter functionWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		classWriter.writeln_c("}");

		membersWriter.writeln_n("/** メッセージ、および、メッセージのテンプレートです。 */");
		membersWriter.writeln_n("private MessageTemplate Message;");
		membersWriter.writeln_n("/** メッセージ引数です。 */");
		membersWriter.writeln_n("private Object[] MessageArgs;");
		membersWriter.writeln_n("");
		membersWriter.writeln_n("/** メッセージIDを取得します。 */");
		membersWriter.writeln_n("public String getMessageID() { return this.Message.getID(); }");
		membersWriter.writeln_n("/** メッセージ引数を取得します。 */");
		membersWriter.writeln_n("public Object[] getMessageArgs() { return this.MessageArgs; }");
		membersWriter.writeln_n("");
		membersWriter.writeln_n("private Formatter Formatter;");
		membersWriter.writeln_n("");
		membersWriter.writeln_n("/** メッセージを取得します。 */");
		membersWriter.writeln_n("public String getMessage() {");
		membersWriter.writeln_n("	String message = this.Formatter.format(null);");
		membersWriter.writeln_n("	return message;");
		membersWriter.writeln_n("}");
		membersWriter.writeln_n("");
		membersWriter.writeln_n("/** メッセージを取得します。 */");
		membersWriter.writeln_n("public String getMessage(Locale locale) {");
		membersWriter.writeln_n("	String message = this.Formatter.format(locale);");
		membersWriter.writeln_n("	return message;");
		membersWriter.writeln_n("}");
		membersWriter.writeln_n("");
		membersWriter.writeln_n("/**");
		membersWriter.writeln_n(" * コンストラクタです。");
		membersWriter.writeln_n(" * @param Message messages.propertiesにあるメッセージIDに対応するMessage定数");
		membersWriter.writeln_n(" * @param MessageArgs メッセージに埋め込む引数");
		membersWriter.writeln_n(" * @param Formatter メッセージをフォーマットする関数");
		membersWriter.writeln_n(" */");
		membersWriter.writeln_n("private {0}(MessageTemplate Message, Object[] MessageArgs, Formatter Formatter) {", ExceptionClassInformation.ClassName);
		membersWriter.writeln_n("	super(Message.getID());");
		membersWriter.writeln_n("	this.Message = Message;");
		membersWriter.writeln_n("	this.MessageArgs = MessageArgs;");
		membersWriter.writeln_n("	this.Formatter = Formatter;");
		membersWriter.writeln_n("}");

		functionWriter.writeln_n("/**");
		functionWriter.writeln_n(" * メッセージをフォーマットするためのインターフェースです。");
		functionWriter.writeln_n(" * @author すふぃあ");
		functionWriter.writeln_n(" */");
		functionWriter.writeln_n("@FunctionalInterface");
		functionWriter.writeln_n("public interface Formatter {");
		functionWriter.writeln_n("	/**");
		functionWriter.writeln_n("	 * メッセージをフォーマットします。");
		functionWriter.writeln_n("	 * @param locale 指定がない場合はnullです。");
		functionWriter.writeln_n("	 */");
		functionWriter.writeln_n("	public String format(Locale locale);");
		functionWriter.writeln_n("}");

		for(int argCount = 0; argCount <= maxArgCount; ++argCount) {
			// 『, Object arg*』
			String additionalArgsDefinitionString = IntStream.range(0, argCount).mapToObj(i -> ", Object arg" + i).collect(Collectors.joining(""));
			// 『arg*, arg*』
			String argsString = IntStream.range(0, argCount).mapToObj(i -> "arg" + i).collect(Collectors.joining(", "));
			// 『arg*, 』
			String frontArgsString = IntStream.range(0, argCount).mapToObj(i -> "arg" + i + ", ").collect(Collectors.joining(""));
			constructorsWriter.writeln_n("/**");
			constructorsWriter.writeln_n(" * コンストラクタです。");
			constructorsWriter.writeln_n(" * @param message messages.propertiesにあるメッセージIDに対応するMessage定数");
			EPicoWriter paramInformationWriter = constructorsWriter.createDeferredEPicoWriter();
			constructorsWriter.writeln_n(" */");
			constructorsWriter.writeln_n("public {0}(MessageTemplateFor{1}Args message{2}) {", ExceptionClassInformation.ClassName, argCount, additionalArgsDefinitionString);
			constructorsWriter.writeln_n("	this(message, new Object[] {{0}}, (l) -> message.format({1}l));", argsString, frontArgsString);
			constructorsWriter.writeln_n("}");

			for(int c = 0; c <= argCount; ++c) {
				paramInformationWriter.writeln_n(" * @param arg{0} メッセージに埋め込む{1}個目のオブジェクト", c, c+1);
			}
		}

		Path filePath = ExceptionClassInformation.getSourceFilePath();
		MessageGenerator.outputClass(filePath, classWriter);
	}

	/** アダプタークラスを出力します。 */
	private void outputAdaptorClass(AdaptorClassInformation AdaptorClassInformation, MessageClassInformation MessageClassInformation, Collection<MessageInformation> messageInformations) {
		String generatedAnnotattionCommentOutPrefix = AdaptorClassInformation.CommentoutGeneratedAnnotation ? "// " : "";
		EPicoWriter classWriter = new EPicoWriter();
		classWriter.writeln_n("package {0};", AdaptorClassInformation.PackageName);
		classWriter.writeln_n("");
		classWriter.writeln_n("{0}import jakarta.annotation.Generated;", generatedAnnotattionCommentOutPrefix);
		classWriter.writeln_n("");

		classWriter.writeln_n("/**");
		classWriter.writeln_n(" * メッセージの簡易呼び出しのためのアダプターです。");
		classWriter.writeln_n(" * @author {0}", AdaptorClassInformation.Author);
		classWriter.writeln_n(" */");
		classWriter.writeln_n("{1}@Generated(\"{0}\")", this.getClass().getName(), generatedAnnotattionCommentOutPrefix);
		classWriter.writeln_o("public class {0} {", AdaptorClassInformation.ClassName);
		classWriter.writeln_n("");
		EPicoWriter membersWriter = classWriter.createDeferredEPicoWriter();
		classWriter.writeln_n("");
		classWriter.writeln_c("}");

		for(MessageInformation mi : messageInformations) {
			int argCount = mi.ArgCount;
			// 『Object arg*, Object arg*』
			String argsDefinitionString = IntStream.range(0, argCount).mapToObj(i -> "Object arg" + i).collect(Collectors.joining(", "));
			// 『arg*, arg*』
			String argsString = IntStream.range(0, argCount).mapToObj(i -> "arg" + i).collect(Collectors.joining(", "));
			membersWriter.writeln_n("/**");
			membersWriter.writeln_n(" * 『{0}』", mi.Message);
			EPicoWriter paramInformationWriter = membersWriter.createDeferredEPicoWriter();
			membersWriter.writeln_n(" */");
			membersWriter.writeln_n("public static String {0}({1}) {", mi.ID, argsDefinitionString);
			membersWriter.writeln_n("	return {0}.{1}.{2}.format({3});", MessageClassInformation.PackageName, MessageClassInformation.ClassName, mi.ID, argsString);
			membersWriter.writeln_n("}");

			for(int c = 0; c <= argCount; ++c) {
				paramInformationWriter.writeln_n(" * @param arg{0} メッセージに埋め込む{1}個目のオブジェクト", c, c+1);
			}
		}

		Path filePath = AdaptorClassInformation.getSourceFilePath();
		MessageGenerator.outputClass(filePath, classWriter);
	}

	/** クラスのソースファイルを出力します。 */
	private static void outputClass(Path filePath, EPicoWriter classWriter) {
		Path directoryPath = filePath.getParent();
		try {
			Files.createDirectories(directoryPath);
			Path tempFilePath = Files.createTempFile(directoryPath, null, null);
			try(Writer tempFileWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFilePath), StandardCharsets.UTF_8))) {
				String classString = classWriter.toString();
				new StringReader(classString).transferTo(tempFileWriter);
				tempFileWriter.flush();
				try {
					Files.move(tempFilePath, filePath, StandardCopyOption.ATOMIC_MOVE);
				} catch(AtomicMoveNotSupportedException ex) {
					throw new IllegalStateException("Atomic moveがサポートされていない環境は未対応です。", ex);
				}
			}
		} catch(IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	/**
	 * MessageGeneratorの設定を表現します。
	 * ソースディレクトリへのパス。
	 * @author すふぃあ
	 */
	public static class Configuration {
		/** 相対パスを解決するための元となるディレクトリのパスです。 */
		@Option(names={"-BaseDirectoryPath", "--base-directory-path", "-b"}, description="base directory path for relative paths.")
		public String BaseDirectoryPath;
		/** 入力となるメッセージプロパティファイルです。 */
		@Parameters(description="message property file path from current directory (or absolute path). ex. src/main/resources/message(s).properties.")
		public String[] MessagePropertyFilePaths;
		/** 著作者です。 */
		@Option(names={"-Author", "--author", "-u"}, description="author.")
		public String Author;
		/** 出力先となるソースディレクトリです。 */
		@Option(names={"-SourceDirectoryPath", "--source-directory-path", "-s"}, description="source directory path. ex. src/main/java.")
		public String SourceDirectoryPath;
		/** パッケージの名前です。 */
		@Option(names={"-PackageName", "--package-name", "-p"}, description="package name.")
		public String PackageName;
		/** メッセージクラスの名前です。 */
		@Option(names={"-MessageClassName", "--message-class-name", "-m"}, description="message class name.")
		public String MessageClassName;
		/** 例外クラスの名前です。 */
		@Option(names={"-ExceptionClassName", "--exception-class-name", "-e"}, description="exception class name.")
		public String ExceptionClassName;
		/** アダプタークラスの名前です。 */
		@Option(names={"-AdaptorClassName", "--adaptor-class-name", "-a"}, description="adaptor class name.")
		public String AdaptorClassName;
		/** 例外クラスを出力するかどうか。 */
		@Option(names={"-SuppressOutputExceptionClass", "--suppress-output-exception-class", "-E"}, description="suppress output exception class.")
		public boolean SuppressOutputExceptionClass;
		/** アダプタークラスを出力するかどうか。 */
		@Option(names={"-SuppressOutputAdaptorClass", "--suppress-output-adaptor-class", "-A"}, description="suppress output adaptor class.")
		public boolean SuppressOutputAdaptorClass;
		/** Generatedアノテーションをコメントアウトするかどうか。 */
		@Option(names={"-CommentOutGeneratedAnnotation", "--comment-out-generated-annotation", "-g"}, description="comment out generated annotation.")
		public boolean CommentoutGeneratedAnnotation;
		/** メッセージのIDの文字列表現の定数を作成するかどうか。 */
		@Option(names={"-CreateMessageIDConstants", "--create-message-id-constants", "-I"}, description="create message ID constants.")
		public boolean CreateMessageIDConstants;
		/** ヘルプを表示するかどうか。 */
		@Option(names={"-Help", "--help", "-H"}, description="display help for command line.", usageHelp=true)
		public boolean Help;
	}
	/**
	 * ユーティリティです。
	 * @author すふぃあ
	 */
	public static class Utilities {
		/**
		 * 実際に使うメッセージファイルのパスを返します。
		 * 成功したときに返すパスは全部存在します。
		 * @throws FailedToAutoDetectException 自動検出でファイルが見つからなかった場合に投げられます。
		 * @throws MissingFileException 指定されたファイルが存在しなかった場合に投げられます。
		 */
		public static Path[] getMessagePropertyFilePaths(Configuration configuration) {
			Path[] filePaths;
			if((configuration.MessagePropertyFilePaths != null) && (configuration.MessagePropertyFilePaths.length > 0)) {
				var c = Stream.of(configuration.MessagePropertyFilePaths)
					.map(s -> Path.of(s))
					.map(p -> ((p.isAbsolute() == false) && (configuration.BaseDirectoryPath != null)) ? Path.of(configuration.BaseDirectoryPath).resolve(p) : p)
					.toArray(Path[]::new);
				for(Path p : c) {
					if(Files.exists(p) == false) {
						String message = MessageFormat.format("メッセージファイル[{0}]が見つかりませんでした。", p.toAbsolutePath());
						throw new MissingFileException(message);
					}
				}
				// 指定されたファイルは全部見つかった。
				filePaths = c;
			} else {
				// 適当に探す。
				var c = Stream.of(DEFAULT_FILEPATHS)
					.map((s) -> Path.of(s))
					.map(p -> ((p.isAbsolute() == false) && (configuration.BaseDirectoryPath != null)) ? Path.of(configuration.BaseDirectoryPath).resolve(p) : p)
					.filter(Files::exists)
					.findFirst();
				if(c.isPresent() == false) {
					throw new FailedToAutoDetectException("メッセージファイルが見つかりませんでした。");
				}
				filePaths = new Path[] { c.get() };
			}
			return filePaths;
		}
		/**
		 * 実際に使うソースディレクトリのパスを返します。
		 */
		public static Path getSourceDirectoryPath(Configuration configuration, String defaultSourceDirecotryPath) {
			String sourceDirectoryPath = configuration.SourceDirectoryPath;
			Path p = Path.of((sourceDirectoryPath != null) ? sourceDirectoryPath : DEFAULT_SOuRTH_DIRECTORY_PATH);
			p = ((p.isAbsolute() == false) && (configuration.BaseDirectoryPath != null)) ? Path.of(configuration.BaseDirectoryPath).resolve(p) : p;
			return p;
		}
		/** 基本的なクラス情報を作成します。 */
		public static ClassInformation createClassInformation(String inputClassName, String inputPackageName, String defaultPackageName, String defaultClassName) {
			String classNameExpression = (inputClassName != null) ? inputClassName : defaultClassName;
			String[] parts = classNameExpression.split("\\.");
			String className = parts[parts.length - 1];
			if(className.isEmpty()) {
				throw new EmptyClassNameException("クラスの名前が空文字になっています。");
			}
			String packageName = (parts.length > 1) ? classNameExpression.substring(0, classNameExpression.length() - (1 + className.length())) : null;
			if(packageName == null) { packageName = inputPackageName; }
			if((packageName == null) || (packageName.isEmpty())) { packageName = defaultPackageName; }
			ClassInformation information = new ClassInformation(packageName, className);
			return information;
		}
	}
	/**
	 * クラスの情報を表現します。
	 * @author すふぃあ
	 */
	public static class ClassInformation {
		/** パッケージの名前です。 */
		public String PackageName;
		/** クラスの名前です。 */
		public String ClassName;
		/** コンストラクタ。 */
		public ClassInformation(String PackageName, String ClassName) {
			this.PackageName = PackageName;
			this.ClassName = ClassName;
		}
		/** コンストラクタ。 */
		public ClassInformation(ClassInformation information) {
			this(information.PackageName, information.ClassName);
		}
		/** 指定されたソースディレクトリ基準でのソースファイルのパスを返します。 */
		public Path getSourceFilePath(Path sourceDirectoryPath) {
			String packageDirectoryPath = this.PackageName.replaceAll("\\.", FileSystems.getDefault().getSeparator().replaceAll("\\\\", "\\\\\\\\"));
			String fileName = this.ClassName + ".java";
			Path path = sourceDirectoryPath.resolve(packageDirectoryPath).resolve(fileName);
			return path;
		}
	}
	/**
	 * メッセージクラスを表現します。
	 * @author すふぃあ
	 */
	public static class MessageClassInformation extends ClassInformation {
		/** 出力先となるソースディレクトリです。 */
		public Path SourceDirectoryPath;
		/** 著作者です。 */
		public String Author;
		/** Generatedアノテーションをコメントアウトするかどうか。 */
		public boolean CommentoutGeneratedAnnotation;
		/** メッセージのIDの文字列表現の定数を作成するかどうか。 */
		public boolean CreateMessageIDConstants;
		/** コンストラクタ。 */
		public MessageClassInformation(ClassInformation information) {
			super(information);
		}
		/** このクラスのソースファイルのパスを返します。 */
		public Path getSourceFilePath() {
			Path path = this.getSourceFilePath(this.SourceDirectoryPath);
			return path;
		}
	}
	/**
	 * Messageを表現します。
	 * @author すふぃあ
	 */
	public static class MessageInformation {
		/** メッセージです。 */
		public String Message;
		/** キーです。 */
		public String Key;
		/** IDです。 */
		public String ID;
		/** 引数の数です。 */
		public int ArgCount;
		/** コンストラクタ。 */
		public MessageInformation(String Key, String Message) {
			this.Key = Key;
			this.Message = Message;
			this.ID = Key.replaceAll("-", "_");
			int argCount;
			try {
				argCount = new MessageFormat(Message).getFormatsByArgumentIndex().length;
			} catch(Exception ex) {
				// エスケープされてないのとかは、通常の文字列だと捉えて、引数なしにする。
				argCount = 0;
			}
			this.ArgCount = argCount;
		}
	}
	/**
	 * 例外クラスを表現します。
	 * @author すふぃあ
	 */
	public static class ExceptionClassInformation extends MessageClassInformation {
		/** コンストラクタ。 */
		public ExceptionClassInformation(ClassInformation information) { super(information); }
	}
	/**
	 * アダプタークラスを表現します。
	 * @author すふぃあ
	 */
	public static class AdaptorClassInformation extends MessageClassInformation {
		/** コンストラクタ。 */
		public AdaptorClassInformation(ClassInformation information) { super(information); }
	}

	/**
	 * 自動検出に失敗したことを表現する例外です。
	 * @author すふぃあ
	 */
	public static class FailedToAutoDetectException extends RuntimeException {
		/** コンストラクタ。 */
		public FailedToAutoDetectException(String message) {
			super(message);
		}
	}

	/**
	 * ファイルが見つからないことを表現する例外です。
	 * @author すふぃあ
	 */
	public static class MissingFileException extends RuntimeException {
		/** コンストラクタ。 */
		public MissingFileException(String message) {
			super(message);
		}
	}

	/**
	 * クラス名が空文字であることを表現する例外です。
	 * @author すふぃあ
	 */
	public static class EmptyClassNameException extends RuntimeException {
		/** コンストラクタ。 */
		public EmptyClassNameException(String message) {
			super(message);
		}
		/** コンストラクタ。 */
		public EmptyClassNameException(String message, Throwable cause) {
			super(message, cause);
		}
	}

}
