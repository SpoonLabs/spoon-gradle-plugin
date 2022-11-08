package fr.inria.gforge.spoon

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import spoon.Launcher

class SpoonTask extends DefaultTask {
    def String[] srcFolders = []
    @Internal
    def File outFolder
    @Internal
    def boolean preserveFormatting
    @Internal
    def boolean noClasspath
    def String[] processors = []
    @Classpath
    def FileCollection classpath
    @Internal
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
        List<String> params = new LinkedList<>()

        addParam(params, '-i', srcFolders.join(':'))
        addParam(params, '-o', outFolder.getAbsolutePath())
        addParam(params, '--compliance', '' + compliance)
        if (preserveFormatting) {
            addKey(params, '-f')
        }
        if (noClasspath) {
            addKey(params, '-x')
        }
        if (processors.size() != 0) {
            addParam(params, '-p', processors.join(File.pathSeparator))
        }
        if (!classpath.asPath.empty) {
            addParam(params, '--source-classpath', classpath.asPath)
        }

        def launcher = new Launcher()
        launcher.setArgs(params.toArray(new String[params.size()]))
        launcher.run()
    }

    private printEnvironment(printer) {
        printer "----------------------------------------"
        printer "source folder: $srcFolders"
        printer "output folder: $outFolder"
        printer "preserving formatting: $preserveFormatting"
        printer "no classpath: $noClasspath"
        printer "processors: $processors"
        printer "classpath: $classpath.asPath"
        printer "----------------------------------------"
    }

    private static void addParam(params, key, value) {
        addKey(params, key)
        params.add(value)
    }

    private static void addKey(params, key) {
        params.add(key)
    }
}
