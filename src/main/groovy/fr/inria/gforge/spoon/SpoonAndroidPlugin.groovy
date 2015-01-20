package fr.inria.gforge.spoon

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class SpoonAndroidPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        def hasAppPlugin = project.plugins.hasPlugin AppPlugin
        def hasLibraryPlugin = project.plugins.hasPlugin LibraryPlugin

        // Ensure the Android plugin has been added in app or library form, but not both.
        if (!hasAppPlugin && !hasLibraryPlugin) {
            throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
        } else if (hasAppPlugin && hasLibraryPlugin) {
            throw new IllegalStateException("Having both 'android' and 'android-library' plugin is not supported.")
        }

        project.extensions.create "spoon", SpoonExtension

        // Adds task before the evaluation of the project to access of values
        // overloaded by the developer.
        project.afterEvaluate({
            def variants = hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
            variants.all { variant ->
                def buildTypeName = variant.buildType.name.capitalize()
                def projectFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
                if (projectFlavorNames.isEmpty()) {
                    projectFlavorNames = [""]
                }
                def projectFlavorName = projectFlavorNames.join()
                def variationName = "$projectFlavorName$buildTypeName"

                def compileJavaTask = variant.javaCompile;

                def spoonTask = project.task("spoon${variationName}", type: SpoonAndroidTask, dependsOn: "generate${variationName}Sources") {
                    if (!project.spoon.outFolder) {
                        project.spoon.outFolder = project.file("${project.buildDir}/generated-sources/spoon")
                    }

                    srcFolders = Utils.transformListFileToListString(project, project.android.sourceSets.main.java.srcDirs)
                    outFolder = project.spoon.outFolder
                    preserveFormatting = project.spoon.preserveFormatting
                    noClasspath = project.spoon.noClasspath
                    processors = project.spoon.processors
                    classpath = compileJavaTask.classpath + Utils.getAndroidSdk(project)
                    srcPath = project.files(
                            "${project.buildDir}/generated/source/r/${buildTypeName}/",
                            "${project.buildDir}/generated/source/buildConfig/${buildTypeName}/"
                    )
                }

                // Inserts spoon task before compiling.
                compileJavaTask.source = project.files("${project.buildDir}/generated/source", project.spoon.outFolder)
                compileJavaTask.dependsOn spoonTask
            }
        })
    }
}
