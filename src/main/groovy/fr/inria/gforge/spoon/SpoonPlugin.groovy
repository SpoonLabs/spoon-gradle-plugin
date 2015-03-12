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

        project.extensions.create "spoon", SpoonExtension

        // Adds task before the evaluation of the project to access of values
        // overloaded by the developer.
        project.afterEvaluate({
            def compileJavaTask = project.getTasksByName("compileJava", true).first();

            def spoonTask = project.task('spoon', type: SpoonTask) {
                def sourceFolders = []
                if (!project.spoon.srcFolders) {
                    sourceFolders = Utils.transformListFileToListString(project, project.sourceSets.main.java.srcDirs)
                } else {
                    sourceFolders = Utils.transformListFileToListString(project, project.spoon.srcFolders)
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
                compliance = project.spoon.compliance
            }

            // Changes source folder if the user don't would like use the original source.
            if (!project.spoon.compileOriginalSources) {
                compileJavaTask.source = project.spoon.outFolder
            }
            // Inserts spoon task before compiling.
            compileJavaTask.dependsOn spoonTask
        })
    }
}
