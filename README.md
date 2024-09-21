# Kord Extensions i18n Tools

This repository contains a set of tools and APIs used by Kord Extensions and its Gradle plugins to implement and work
with the translations system.

You can find the current version on [the releases page](https://github.com/Kord-Extensions/i18n/releases).

# Translation Class Generator

As of Kord Extensions 2.3.0, users must provide all translation keys as `Key` objects.
To make this easier and more intuitive, the translation class generator reads the default translations for a translation
bundle and generates a class containing the corresponding keys.

As some Kord Extensions users either don't use Gradle or don't wish to use our Gradle plugin, the generator is available
here as a standalone API and CLI tool.

## API

- Releases repo: `https://releases-repo.kordex.dev`
- Snapshots repo: `https://snapshots-repo.kordex.dev`
- Maven Coordinate: `dev.kordex.i18n:i18n-generator:VERSION`

A single API class is exposed as `TranslationsClass`.

To generate a translations class, create an instance of `TranslationsClass`, pass the constructor the following data:

- `allProps`, a `Properties` instance containing the translation keys and default values.
- `bundle`, the corresponding bundle name.
- `className`, the name used by the generated class.
- `classPackage`, the package containing the generated class.

If you need to access the KotlinPoet `FileSpec` object, access it via the `spec` property.
Otherwise, use the `writeTo(File)` function to write the generated class to a given directory, including the package
structure.

## CLI Tool

The CLI tool is meant to be integrated with non-Gradle workflows, allowing you to generate translations classes without
using our Gradle plugins.

This tool requires Java 21 or later. Download the latest `-all` JAR from
[the releases page](https://github.com/Kord-Extensions/i18n/releases), and run it as follows:

```bash
java -jar i18n-generator-<version>-all.jar <options>
```

This tool will parse a properties file and output a generated class containing the given bundle,
and the file's translation keys along with a comment containing the default translation.
The tool will generate this class within a directory tree matching the given package.

### Required Parameters

- `-b, --bundle` - Bundle name, represented as a dot-separated string matching your resource files.
  For example, `template.string` would correspond with `translations/template/string.properties`.
- `-i, --input-file` - Location of a properties file representing your bundle's default translations.
- `-p, --class-package` - Dot-separated package name to place your generated files within.

### Optional Parameters

- `-c, --class-name = Translations` - Name for the generated root class.
- `-e, --encoding = UTF-8` - Encoding used to read the input properties file.
- `-o, --output-dir = ./output` - Output directory for the generated files.

### Other Parameters

- `-h, --help` - Output a help message and exit.
- `-V ,--version` - Output the current version number and exit.
