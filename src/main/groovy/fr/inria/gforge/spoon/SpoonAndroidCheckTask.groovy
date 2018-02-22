package fr.inria.gforge.spoon

import spoon.OutputType
import spoon.compiler.Environment

class SpoonAndroidCheckTask extends SpoonAndroidTask {
    @Override
    void configureEnvironment(Environment environment) {
        super.configureEnvironment(environment)
        environment.setOutputType(OutputType.NO_OUTPUT)
    }
}
