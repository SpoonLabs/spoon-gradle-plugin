package fr.inria.gforge.spoon

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpoonPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('spoon') << {
            println "Hello from Spoon Plugin"
        }
    }
}
