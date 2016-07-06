package fr.inria.gforge.spoon

import org.gradle.api.file.FileCollection

class SpoonExtension {
    /** True to active the debug mode. */
    def boolean debug

    /** Input directories for Spoon. */
    def FileCollection srcFolders

    /** Output directory where Spoon must generate his output (spooned source code). */
    def File outFolder

    /** Tells to spoon that it must preserve formatting of original source code. */
    def boolean preserveFormatting = true;

    /** Tells to spoon that it must not assume a full classpath. */
    def boolean noClasspath

    /** List of processors. */
    def String[] processors = []

    /** True to active the compilation of original sources. */
    def boolean compileOriginalSources

    /** Java version used to spoon target project. */
    def int compliance = 7
}
