module jp.empressia.message.generator {
	requires jp.empressia.message;
	requires jp.empressia.picocog;
	requires info.picocli;
	opens jp.empressia.message.generator to info.picocli;
	exports jp.empressia.message.generator;
}
