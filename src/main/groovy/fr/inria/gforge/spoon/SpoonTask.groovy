package fr.inria.gforge.spoon

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import spoon.Launcher

class SpoonTask extends DefaultTask {
    def String[] srcFolders = []
    def File outFolder
    def boolean preserveFormatting
    def boolean noClasspath
    def String[] processors = []
    def FileCollection classpath

    @TaskAction
    void run() {
        // No source code to spoon.
        if (srcFolders.size() == 0) {
            return;
        }
        List<String> params = new LinkedList<>()

        addParam(params, '-i', srcFolders.join(':'))
        addParam(params, '-o', outFolder.getAbsolutePath())
        addParam(params, '--compliance', '7')
        if (preserveFormatting) {
            addKey(params, '-f')
        }
        if (noClasspath) {
            addKey(params, '-x')
        }
        if (processors.size() != 0) {
            addParam(params, '-p', processors.join(':'))
        }
        if (!classpath.asPath.empty) {
            addParam(params, '--source-classpath', classpath.asPath)
        }

        def launcher = new Launcher()
        launcher.setArgs(params.toArray(new String[params.size()]))
        launcher.run()
    }

    private static void addParam(params, key, value) {
        addKey(params, key)
        params.add(value)
    }

    private static void addKey(params, key) {
        params.add(key)
    }
}
