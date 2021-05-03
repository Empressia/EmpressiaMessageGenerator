package jp.empressia.message.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import jp.empressia.message.generator.MessageGenerator.Configuration;

/**
 * テストです。
 * @author すふぃあ
 */
public class GeneratorTests {

	/**
	 * Generatorが正常終了する。
	 */
	@Test
	public void Generatorが正常終了する() {
		Configuration configuration = new Configuration();
		configuration.SourceDirectoryPath = "src/test/java/";
		MessageGenerator generator = new MessageGenerator(configuration);
		generator.perform();
		Path messageClassPath = Path.of("src/test/java/jp/empressia/message/generated/Message.java");
		Path exceptionClassPath = Path.of("src/test/java/jp/empressia/message/generated/MessageException.java");
		Path adaptorClassPath = Path.of("src/test/java/jp/empressia/message/generated/Messages.java");
		assertAll(
			() -> assertThat("メッセージクラスがある。", Files.exists(messageClassPath), is(true)),
			() -> assertThat("例外クラスがある。", Files.exists(exceptionClassPath), is(true)),
			() -> assertThat("アダプタークラスがある。", Files.exists(adaptorClassPath), is(true))
		);
	}

}
