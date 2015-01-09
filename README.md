# Spoon gradle plugin

A gradle plugin to run spoon on a target project.

## Usage

To use a plugin in a build script, you need to add the plugin classes to the build script's classpath.
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

So you can execute `gradle build` in your project to launch the `spoon` task on your source code.

## Inputs

You can configure some parameters in the plugin in the `spoon` block in your `build.gradle`:

```
spoon {
    // Here is your custom parameters.
}
```

### Source and output folder

You can specify at spoon its input and output directories with, respectively, `srcFolder` and `outFolder` parameters.

These parameters are typed by `File`, so you must specify these information like the example below:

```
spoon {
    srcFolder = file('src/main/java')
    outFolder = file('build/spoon')
}
```

### Formatting

You can preserving the formatting of your source code with the boolean parameter `preserveFormatting`.

### Processors

Spoon can use processors to process some codes during its analysis of a source code. The plugin supports processors and can be specified in the configuration.

In the next usage, we would like to launch the processor name `fr.inria.gforge.spoon.processors.CountStatementProcessor` (you must specify the full qualified name) and the dependency necessary to locate the processor in the `buildscript` block.

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

## Download

The plugin isn't yet available on Maven Central. For now, you must to clone this repository and install it on your maven local repository.