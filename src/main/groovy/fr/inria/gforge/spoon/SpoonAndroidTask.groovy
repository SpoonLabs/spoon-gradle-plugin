package fr.inria.gforge.spoon

import fr.inria.gforge.spoon.internal.AndroidSpoonCompiler
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import spoon.OutputType
import spoon.compiler.SpoonCompiler
import spoon.reflect.factory.FactoryImpl
import spoon.support.DefaultCoreFactory
import spoon.support.StandardEnvironment
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler

class SpoonAndroidTask<T extends JDTBasedSpoonCompiler> extends DefaultTask {
    def String[] srcFolders = []
    def ConfigurableFileCollection srcPath
    def File outFolder
    def boolean preserveFormatting
    def boolean noClasspath
    def String[] processors = []
    def FileCollection classpath

    @TaskAction
    void run() {
        def log = project.logger
        printEnvironment(log.&debug)
        if (project.spoon.debug) {
            printEnvironment(System.out.&println)
        }

        // No source code to spoon.
        if (srcFolders.size() == 0) {
            return;
        }

        SpoonCompiler compiler = new AndroidSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment()));

        // configure spoon
        compiler.setOutputDirectory(outFolder);
        compiler.factory.environment.setDebug(project.spoon.debug);
        compiler.setSourceClasspath(classpath.asPath.split(":"))
        srcFolders.each { directory -> compiler.addInputSource(new File(directory)) }
        srcPath.files.each { directory -> compiler.addGeneratedDirectory(directory) }

        // Build spoon model
        compiler.build();

        compiler.process(Arrays.asList(processors));
        compiler.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
    }

    def printEnvironment(printer) {
        printer "----------------------------------------"
        printer "source folder: $srcFolders"
        printer "output folder: $outFolder"
        printer "src path: $srcPath.asPath"
        printer "preserving formatting: $preserveFormatting"
        printer "no classpath: $noClasspath"
        printer "processors: $processors"
        printer "classpath: $classpath.asPath"
        printer "----------------------------------------"
    }
}
