package fr.inria.gforge.spoon

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class SpoonPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        def hasJavaPlugin = project.plugins.hasPlugin JavaPlugin
        if (!hasJavaPlugin) {
            throw new IllegalStateException('The java plugin is required')
        }
        def log = project.logger

        project.extensions.create "spoon", SpoonExtension

        project.afterEvaluate({
            def compileJavaTask = project.getTasksByName("compileJava", true).first();

            def spoonTask = project.task('spoon', type: SpoonTask) {
                if (!project.spoon.srcFolder) {
                    project.spoon.srcFolder = project.file(project.sourceSets.main.java.srcDirs.first())
                }
                if (!project.spoon.outFolder) {
                    project.spoon.outFolder = project.file("${project.buildDir}/generated-sources/spoon")
                }

                srcFolder = project.spoon.srcFolder
                outFolder = project.spoon.outFolder
                preserveFormatting = project.spoon.preserveFormatting
                noClasspath = project.spoon.noClasspath
                processors = project.spoon.processors
                classpath = compileJavaTask.classpath

                printEnvironment(log.&debug, project, compileJavaTask)
                if (project.spoon.debug) {
                    printEnvironment(System.out.&println, project, compileJavaTask)
                }
            }

            // insert spoon task before compiling.
            compileJavaTask.dependsOn spoonTask
        })
    }

    private static void printEnvironment(printer, project, compileJavaTask) {
        printer "----------------------------------------"
        printer "source folder: $project.spoon.srcFolder"
        printer "output folder: $project.spoon.outFolder"
        printer "preserving formatting: $project.spoon.preserveFormatting"
        printer "no classpath: $project.spoon.noClasspath"
        printer "processors: $project.spoon.processors"
        printer "classpath: $compileJavaTask.classpath.asPath"
        printer "----------------------------------------"
    }
}
