[![Build Status](https://travis-ci.org/SpoonLabs/spoon-gradle-plugin.svg?branch=master)](https://travis-ci.org/SpoonLabs/spoon-gradle-plugin)

# Spoon gradle plugin

A gradle plugin to run source code transformations using spoon on a project built with Gradle.

## Basic usage

To use spoon-gradle-plugin, you need to add the plugin classes to the build script's classpath.
To do this, you use a `buildscript` block. The following example shows how you might do this when
the JAR containing the plugin has been published to a local repository:

```
buildscript {
    repositories {
        maven {
            mavenLocal()
        }
    }
    dependencies {
        classpath group: 'fr.inria.gforge.spoon', name: 'spoon-gradle-plugin', version:'1.0-SNAPSHOT'
    }
}

apply plugin: 'java'
apply plugin: 'spoon'
```

> **Note:** Spoon is used to compile java source code. The plugin java is required for the plugin.

The plugin has a task with the same name, `spoon` executed just before the compilation of your project.

Consequently, when `gradle build` is run on your project, the source code is first rewritten by `spoon` before compilation.

### How to add processors?

Spoon can use processors to analyze and transform source code.

To add processors, one must:

1. add a dependency  in the `buildscript` block. (you must specify the full qualified name)
2. configure spoon.processors

In the example below, we add processor `fr.inria.gforge.spoon.processors.CountStatementProcessor` and the dependency necessary to locate the processor.

```
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath group: 'fr.inria.gforge.spoon', name: 'spoon-gradle-plugin', version:'1.0-SNAPSHOT'
        classpath group: 'fr.inria.gforge.spoon', name: 'spoon-processors', version:'1.0-SNAPSHOT'
    }
}

apply plugin: 'java'
apply plugin: 'spoon'

spoon {
    processors = ['fr.inria.gforge.spoon.processors.CountStatementProcessor']
}

```

### Source folder

spoon-gradle-plugin analyzes and transforms the standard sourceSets as follows:

```
sourceSets {
    main {
        java {
            srcDir 'src/main/project'
        }
    }
}
```

## Download

To use the plugin, you first clone this repository and install it on your maven local repository.

## License

Copyright Inria, all rights reserved.