package fr.inria.gforge.spoon.internal

import org.gradle.api.Project

/**
 * Created by nicolas on 05/09/2014.
 *
 * largely inspired from : com.android.build.gradle.internal.Sdk.
 */
class SdkLoader {
    private static final String FN_LOCAL_PROPERTIES = "local.properties"

    private final Project project

    private File androidSdkDir
    private File androidNdkDir
    private boolean isPlatformSdk = false

    public SdkLoader(Project project) {
        this.project = project

        findLocation()
    }

    public File getSdkDirectory() {
        checkLocation()
        return androidSdkDir
    }

    public File getNdkDirectory() {
        checkLocation()
        return androidNdkDir
    }

    private void checkLocation() {
        if (androidSdkDir == null) {
            throw new RuntimeException(
                    "SDK location not found. Define location with sdk.dir in the local.properties file or with an ANDROID_HOME environment variable.")
        }

        if (!androidSdkDir.isDirectory()) {
            throw new RuntimeException(
                    "The SDK directory '$androidSdkDir.absolutePath' does not exist.")
        }
    }

    private void findLocation() {
        def rootDir = project.rootDir
        def localProperties = new File(rootDir, FN_LOCAL_PROPERTIES)
        if (localProperties.exists()) {
            Properties properties = new Properties()
            localProperties.withInputStream { instr ->
                properties.load(instr)
            }
            def sdkDirProp = properties.getProperty('sdk.dir')

            if (sdkDirProp != null) {
                androidSdkDir = new File(sdkDirProp)
            } else {
                sdkDirProp = properties.getProperty('android.dir')
                if (sdkDirProp != null) {
                    androidSdkDir = new File(rootDir, sdkDirProp)
                    isPlatformSdk = true
                } else {
                    throw new RuntimeException(
                            "No sdk.dir property defined in local.properties file.")
                }
            }

            def ndkDirProp = properties.getProperty('ndk.dir')
            if (ndkDirProp != null) {
                androidNdkDir = new File(ndkDirProp)
            }

        } else {
            String envVar = System.getenv("ANDROID_HOME")
            if (envVar != null) {
                androidSdkDir = new File(envVar)
            } else {
                String property = System.getProperty("android.home")
                if (property != null) {
                    androidSdkDir = new File(property)
                }
            }

            envVar = System.getenv("ANDROID_NDK_HOME")
            if (envVar != null) {
                androidNdkDir = new File(envVar)
            }
        }
    }
}
