package fr.inria.gforge.spoon

import fr.inria.gforge.spoon.internal.AndroidSpoonCompiler
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import spoon.OutputType
import spoon.SpoonModelBuilder
import spoon.processing.Processor
import spoon.reflect.factory.FactoryImpl
import spoon.support.DefaultCoreFactory
import spoon.support.StandardEnvironment

class SpoonAndroidTask extends DefaultTask {
    def FileCollection srcFolders
    def FileCollection srcPath
    def File outFolder
    def boolean preserveFormatting
    def boolean noClasspath
    def String[] processors = []
    def FileCollection classpath
    def int compliance

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

        def environment = new StandardEnvironment()
        environment.setComplianceLevel(compliance)
        environment.setNoClasspath(noClasspath)
        environment.setPreserveLineNumbers(preserveFormatting)
        SpoonModelBuilder compiler = new AndroidSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), environment));

        // configure spoon
        compiler.setSourceOutputDirectory(outFolder);
        compiler.setSourceClasspath(classpath.asPath.split(":"))

        srcFolders.files.each { directory -> compiler.addInputSource(directory) }
        srcPath.files.each { directory -> compiler.addGeneratedDirectory(directory) }

        // Build spoon model
        compiler.build();

        Collection<Processor> processorsClasses = processors.collect {
            it -> this.class.classLoader.loadClass(it)?.newInstance()
        } as Collection<Processor>
        compiler.process(processorsClasses);
        compiler.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
    }

    def printEnvironment(printer) {
        printer "----------------------------------------"
        printer "source folder: $srcFolders.asPath"
        printer "output folder: $outFolder"
        printer "src path: $srcPath.asPath"
        printer "preserving formatting: $preserveFormatting"
        printer "no classpath: $noClasspath"
        printer "processors: $processors"
        printer "classpath: $classpath.asPath"
        printer "----------------------------------------"
    }
}
