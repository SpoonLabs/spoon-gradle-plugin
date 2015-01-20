package fr.inria.gforge.spoon

import fr.inria.gforge.spoon.internal.SdkLoader
import org.gradle.api.Project

final class Utils {
    /**
     * Transforms list of File to list of String.
     */
    def static String[] transformListFileToListString(project, srcDirs) {
        def inputs = []
        srcDirs.each() {
            if (project.file(it).exists()) {
                inputs.add(it.getAbsolutePath())
            }
        };
        return inputs
    }

    /**
     * Locates android SDK to compile current project.
     */
    def static getAndroidSdk(Project project) {
        // get current android sdk version
        def sdk = new SdkLoader(project)
        def jar = new File(sdk.sdkDirectory, "platforms/${project.android.compileSdkVersion}/android.jar")
        if (!jar.exists())
            throw new IllegalArgumentException("unable to locate android jar for version ${project.android.compileSdkVersion} at location ${jar.getAbsolutePath()}")
        return project.files(jar);
    }
}
