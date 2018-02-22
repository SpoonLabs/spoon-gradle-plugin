[![Build Status](https://travis-ci.org/SpoonLabs/spoon-gradle-plugin.svg?branch=master)](https://travis-ci.org/SpoonLabs/spoon-gradle-plugin)

# Spoon gradle plugin

A gradle plugin to run source code transformations using spoon on a project built with Gradle.

## Basic usage

To use spoon-gradle-plugin, you need to add the plugin classes to the build script's classpath. To do this, you use a `buildscript` block. The following example shows how you might do this when the JAR containing the plugin has been published to a local repository:

```groovy
buildscript {
    repositories {
	   mavenLocal()
       mavenCentral()
    }
    dependencies {
        classpath group: 'fr.inria.gforge.spoon',
		          name: 'spoon-gradle-plugin',
		          version:'1.3'
    }
}

apply plugin: 'java'
apply plugin: 'spoon'
```

Consequently, when `gradle build` is run on your project, the source code is first rewritten by `spoon` before compilation.

> **Note:** This project contains two plugins: `spoon` and `spoon-android`. The first one is used to compile java source code and the plugin `java` is required. The second is used to compile android source code and the plugin `com.android.application` or `com.android.library` are required.

## How to add processors?

Spoon can use processors to analyze and transform source code.

To add processors, one must:

1. add a dependency  in the `buildscript` block.
2. configure spoon.processors (you must specify the full qualified name).

In the example below, we add processor `fr.inria.gforge.spoon.processors.CountStatementProcessor` and the dependency necessary to locate the processor.

```groovy
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath group: 'fr.inria.gforge.spoon',
			      name: 'spoon-gradle-plugin',
			      version:'1.3'
        classpath group: 'fr.inria.gforge.spoon',
			      name: 'spoon-processors',
			      version:'1.0-SNAPSHOT'
    }
}

apply plugin: 'java'
apply plugin: 'spoon'

spoon {
    processors = ['fr.inria.gforge.spoon.processors.CountStatementProcessor']
}

```

## How to change source folder?

spoon-gradle-plugin analyzes and transforms the standard sourceSets as follows:

```groovy
sourceSets {
    main {
        java {
            srcDir 'src/main/project'
        }
    }
}
```

or for android projects:

```groovy
android {
	sourceSets {
		main {
			java {
				srcDir 'src/main/project'
			}
		}
	}
}
```

## How to compile original sources?

By default, spoon generate your source code and compile these sources but you can specify at the plugin that you want to compile your original sources with compileOriginalSources sets to true.

```groovy
spoon {
	compileOriginalSources true
}
```

## Download

To use the plugin, you first clone this repository and install it on your maven local repository.

## License

Copyright Inria, all rights reserved.
