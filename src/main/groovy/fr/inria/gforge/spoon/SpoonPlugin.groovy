package fr.inria.gforge.spoon

import org.gradle.api.GradleException
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

            project.task('spoon', type: SpoonTask) {
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

                log.debug("----------------------------------------")
                log.debug("source folder: $project.spoon.srcFolder")
                log.debug("output folder: $project.spoon.outFolder")
                log.debug("preserving formatting: $project.spoon.preserveFormatting")
                log.debug("no classpath: $project.spoon.noClasspath")
                log.debug("processors: $project.spoon.processors")
                log.debug("classpath: $compileJavaTask.classpath.asPath")
                log.debug("----------------------------------------")
            }
        })
    }
}
