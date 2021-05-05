# Empressia Message Generator

## 目次

* [概要](#概要)
* [使い方](#使い方)
* [ライセンス](#ライセンス)
* [使用しているライブラリ](#使用しているライブラリ)

## 概要

Empressia製のメッセージ管理ライブラリ＆ツールであるEmpressia Messageの一部です。  
Empressia Messageのメッセージクラスなどを生成するためのライブラリ兼ツールを提供します。  

## 使い方

基本は、Gradle用プラグインを使用したクラス生成を想定しています。  
ここでは、主に、それ以外の使い方によるクラス生成を紹介します。  

Generatorで生成されたクラスの使い方は、Empressia Messageを参照してください。  

### メッセージプロパティの配置場所

元となるメッセージプロパティは、プロパティを想定しています。  

何も設定しない場合、以下の順にファイルを探します。  
1. src/main/resources/message.properties
1. src/main/resources/messages.properties

これは、MessagePropertyFilePathsを指定することで変更できます。  
複数指定できるのは、複数のLocaleのファイルを読み込むためです。  
先に指定したファイルが優先的に読み込まれます。  

クラス生成後は、これらのファイルには、リソースとしてアクセスされます。  
何も設定しない場合は、見つかったファイルに応じて、自動で設定されます。  
自動設定がうまく動かない場合は、MessagePropertyResourceLocationを指定してください。  

### Javaからの使用

ライブラリを追加します。  
Gradleであれば、例えば、以下のようにします。  

```groovy
dependencies {
	implementation(group:"jp.empressia", name:"jp.empressia.message.generator", version:"1.0.0");
}
```

MessageGeneratorクラスのperformメソッドを呼び出します。  

MessageGeneratorConfigurationで動作の設定を行います。  

主な設定は、以下の通りです。

|設定名|説明|
|-|-|
|BaseDirectoryPaths|相対パスを解決するための元となるディレクトリのパスです。|
|MessagePropertyFilePaths|入力となるメッセージプロパティファイルです。|
|MessagePropertyResourceLocation|入力となるメッセージプロパティファイルです。|
|Author|著作者です。|
|SourceDirectoryPath|出力先となるソースディレクトリです。|
|PackageName|パッケージの名前です。|
|MessageClassName|メッセージクラスの名前です。|
|ExceptionClassName|例外クラスの名前です。|
|AdaptorClassName|アダプタークラスの名前です。|
|SuppressOutputExceptionClass|例外クラスを出力するかどうか。|
|SuppressOutputAdaptorClass|アダプタークラスを出力するかどうか。|
|CommentoutGeneratedAnnotation|Generatedアノテーションをコメントアウトするかどうか。|
|CreateMessageIDConstants|メッセージのIDの文字列表現の定数を作成するかどうか。|

最新の設定情報は、MessageGeneratorConfigurationクラスを参照してください。  

### コマンドラインからの使用

以下のライブラリを--module-pathで読み込めるように設定して。  
jp.enpressia.message.generatorモジュールを実行します。  

* jp.enpressia.message.generator
* jp.enpressia.message
* jp.empressia.picocog
* picocli
* picocog

例えば、以下のようなコマンドになります。

```
java --module-path "path/to/libraries/" --module jp.empressia.message.generator
```

コマンドに渡す引数があれば、さらに後ろに追加します。  

たとえば、ヘルプをみる場合は、

```
java --module-path "path/to/libraries/" --module jp.empressia.message.generator --help
```
や
```
java --module-path "path/to/libraries/" --module jp.empressia.message.generator -Help
```

などとします。

指定できるオプションとパラメーターは、  
MessageGeneratorConfigurationの設定名の先頭に『-』をつけたものを、  
だいたい、そのまま使えます。  

詳細はコマンドのヘルプを参照してください。  

### Gradleからの使用

Gradle用のプラグインの説明を参照してください。  

## ライセンス

いつも通りのライセンスです。  
zlibライセンス、MITライセンスでも利用できます。  

ただし、チーム（複数人）で使用する場合は、MITライセンスとしてください。  

## 使用しているライブラリ

* Empressia Message Library
	* https://github.com/Empressia/EmpressiaMessageLibrary
* EPicocog
	* https://github.com/Empressia/EPicocog
* picocli
    * https://github.com/remkop/picocli

## 注意

プロジェクトはVSCodeのJava拡張機能ではテストを実行できないようです（2021/05/01）。  
Gradleからは実行できます。  

Javadocの中身がないのは、JDK11の不具合のためです。  
* https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8208269
