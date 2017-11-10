package fr.inria.gforge.spoon

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree

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
        project.afterEvaluate(
                {
                    def variants = hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
                    variants.all { variant ->
                        String variantName = variant.name
                        def variantDirName = variant.dirName

                        def compileJavaTask = variant.javaCompiler;
                        if (!project.spoon.outFolder) {
                            project.spoon.outFolder = project.file("${project.buildDir}/generated-sources/spoon")
                        }
                        def spoonOutFolder = project.file("${project.spoon.outFolder.path}/$variantDirName")


                        FileCollection variantSrcFolders = ((FileTree) compileJavaTask.source).filter { f -> !f.path.contains(project.buildDir.path) }
                        FileCollection variantSrcPath = ((FileTree) compileJavaTask.source).minus(variantSrcFolders)


                        def spoonTask = project.task("spoon${variantName.capitalize()}", type: SpoonAndroidTask, dependsOn: "generate${variantName.capitalize()}Sources") {
                            srcFolders = variantSrcFolders
                            outFolder = spoonOutFolder
                            preserveFormatting = project.spoon.preserveFormatting
                            noClasspath = project.spoon.noClasspath
                            processors = project.spoon.processors
                            processorsInstance = project.spoon.processorsInstance
                            classpath = compileJavaTask.classpath + Utils.getAndroidSdk(project)
                            srcPath = variantSrcPath
                            compliance = project.spoon.compliance
                        }

                        // Changes source folder if the user don't would like use the original source.
                        if (!project.spoon.compileOriginalSources) {
                            def files = variantSrcPath.files
                            files << spoonOutFolder;
                            // convert file path to root folder path
                            compileJavaTask.source =
                                    {
                                        def p = /(${project.buildDir}\/generated\/source\/.*\/${variantDirName}).*/
                                        def paths = []
                                        paths << spoonOutFolder.path;
                                        variantSrcPath.files.each {
                                            File file ->
                                                def m = file.path =~ p;
                                                if (m) {
                                                    def path = m[0][1]
                                                    if (!paths.contains(path))
                                                        paths << path;
                                                }
                                        }
                                        return project.files(paths);
                                    }
                        }

                        // Inserts spoon task before compiling.
                        compileJavaTask.dependsOn spoonTask
                    }
                }
        )
    }

}
