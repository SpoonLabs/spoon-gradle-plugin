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

        // Adds task before the evaluation of the project to access of values
        // overloaded by the developer.
        project.afterEvaluate({
            def compileJavaTask = project.getTasksByName("compileJava", true).first();

            def spoonTask = project.task('spoon', type: SpoonTask) {
                def sourceFolders = []
                if (!project.spoon.srcFolders) {
                    sourceFolders = transformListFileToListString(project, project.sourceSets.main.java.srcDirs)
                } else {
                    sourceFolders = transformListFileToListString(project, project.spoon.srcFolders)
                }
                if (!project.spoon.outFolder) {
                    project.spoon.outFolder = project.file("${project.buildDir}/generated-sources/spoon")
                }

                srcFolders = sourceFolders
                outFolder = project.spoon.outFolder
                preserveFormatting = project.spoon.preserveFormatting
                noClasspath = project.spoon.noClasspath
                processors = project.spoon.processors
                classpath = compileJavaTask.classpath

                printEnvironment(log.&debug, project, sourceFolders, compileJavaTask)
                if (project.spoon.debug) {
                    printEnvironment(System.out.&println, project, sourceFolders, compileJavaTask)
                }
            }

            // Inserts spoon task before compiling.
            compileJavaTask.dependsOn spoonTask
        })
    }

    private static String[] transformListFileToListString(project, srcDirs) {
        def inputs = []
        srcDirs.each() {
            if (project.file(it).exists()) {
                inputs.add(it.getAbsolutePath())
            }
        };
        return inputs
    }

    private static void printEnvironment(printer, project, sourceFolders, compileJavaTask) {
        printer "----------------------------------------"
        printer "source folder: $sourceFolders"
        printer "output folder: $project.spoon.outFolder"
        printer "preserving formatting: $project.spoon.preserveFormatting"
        printer "no classpath: $project.spoon.noClasspath"
        printer "processors: $project.spoon.processors"
        printer "classpath: $compileJavaTask.classpath.asPath"
        printer "----------------------------------------"
    }
}
