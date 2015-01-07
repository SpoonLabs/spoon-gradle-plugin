package fr.inria.gforge.spoon

class SpoonExtension {
    /** Input directory for Spoon. */
    def File srcFolder

    /** Output directory where Spoon must generate his output (spooned source code). */
    def File outFolder

    /** Tells to spoon that it must preserve formatting of original source code. */
    def boolean preserveFormatting

    /** Tells to spoon that it must not assume a full classpath. */
    def boolean noClasspath

    /** List of processors. */
    def String[] processors = []
}
