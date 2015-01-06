package fr.inria.gforge.spoon

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class SpoonPluginTest {

    @Test
    public void testGreetingPluginAddsHelloTask() throws Exception {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'spoon'

        assertTrue(project.tasks.spoon instanceof DefaultTask)
    }
}
