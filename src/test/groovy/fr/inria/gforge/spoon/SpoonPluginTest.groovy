package fr.inria.gforge.spoon

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class SpoonPluginTest {

    @Test
    public void testSpoonPluginAddsSpoonTask() throws Exception {
        final Project project = buildEvaluatableProject()
        project.evaluate()

        assertTrue(project.tasks.spoon instanceof SpoonTask)
    }

    @Test
    public void testExtensionWithDefaultValues() throws Exception {
        final Project project = buildEvaluatableProject()
        project.evaluate()

        String expected = project.sourceSets.main.java.srcDirs.first()
        assertEquals(expected, project.spoon.srcFolder.absolutePath)
        expected = "${project.buildDir.absolutePath}/generated-sources/spoon"
        assertEquals(expected, project.spoon.outFolder.absolutePath)
        assertFalse(project.spoon.preserveFormatting)
        assertFalse(project.spoon.noClasspath)
        assertEquals(0, project.spoon.processors.size())
    }

    @Test
    public void testChangesSrcFolderExtensionValue() throws Exception {
        final Project project = buildEvaluatableProject()
        project.spoon {
            srcFolder = project.file('src/main/java')
        }
        project.evaluate()

        final String expected = "${project.projectDir.absolutePath}/src/main/java"
        assertEquals(expected, project.spoon.srcFolder.absolutePath)
    }

    @Test
    public void testChangesOutFolderExtensionValue() throws Exception {
        final Project project = buildEvaluatableProject()
        project.spoon {
            outFolder = project.file('build/spoon')
        }
        project.evaluate()

        final String expected = "${project.buildDir.absolutePath}/spoon"
        assertEquals(expected, project.spoon.outFolder.absolutePath)
    }

    @Test
    public void testChangesPreserveFormattingExtensionValue() throws Exception {
        final Project project = buildEvaluatableProject()
        project.spoon {
            preserveFormatting = true
        }
        project.evaluate()

        assertTrue(project.spoon.preserveFormatting)
    }

    @Test
    public void testChangesNoClasspathExtensionValue() throws Exception {
        final Project project = buildEvaluatableProject()
        project.spoon {
            noClasspath = true
        }
        project.evaluate()

        assertTrue(project.spoon.noClasspath)
    }

    @Test
    public void testChangesProcessorsExtensionValue() throws Exception {
        final Project project = buildEvaluatableProject()
        project.spoon {
            processors = ['fr.inria.gforge.spoon.Processor']
        }
        project.evaluate()

        assertEquals(1, project.spoon.processors.size())
        assertEquals('fr.inria.gforge.spoon.Processor', project.spoon.processors.first())
    }

    @Test(expected = PluginApplicationException.class)
    public void testLaunchPluginWithoutJavaPlugin() throws Exception {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'spoon'
        project.evaluate()
    }

    @Test
    public void testExecutionOfSpoonTask() throws Exception {
        final Project project = buildEvaluatableProject()
        project.spoon {
            srcFolder = project.projectDir
        }
        project.evaluate()
        executeTask(project, 'spoon')

        assertTrue(project.file("${project.buildDir}/generated-sources/spoon").exists())
    }

    private static Project buildEvaluatableProject() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'java'
        project.apply plugin: 'spoon'
        return project
    }

    private static void executeTask(Project project, String task) {
        def spoon = project.getTasksByName(task, true).first()
        spoon.actions.each { Action action ->
            action.execute(spoon)
        }
    }
}