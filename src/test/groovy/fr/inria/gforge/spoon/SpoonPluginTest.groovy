package fr.inria.gforge.spoon

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

class SpoonPluginTest {
    private Project project

    @Before
    public void setUp() throws Exception {
        project = buildEvaluatableProject()
    }

    @Test
    public void testSpoonPluginAddsSpoonTask() throws Exception {
        project.evaluate()

        assertTrue(this.project.tasks.spoon instanceof SpoonTask)
    }

    @Test
    public void testExtensionWithDefaultValues() throws Exception {
        project.evaluate()

        assertEquals(null, project.spoon.srcFolders)
        String expected = "${project.buildDir.absolutePath}/generated-sources/spoon"
        assertEquals(expected, project.spoon.outFolder.absolutePath)
        assertFalse(project.spoon.preserveFormatting)
        assertFalse(project.spoon.noClasspath)
        assertEquals(0, project.spoon.processors.size())
    }

    @Test
    public void testChangesDebugModeExtensionValue() throws Exception {
        project.spoon {
            debug = true
        }
        project.evaluate()

        assertEquals(true, project.spoon.debug)
    }

    @Test
    public void testChangesSrcFoldersExtensionValue() throws Exception {
        project.spoon {
            srcFolders = project.files('.')
        }
        project.evaluate()

        assertEquals(1, project.spoon.srcFolders.getFiles().size())
        final String expected = "${project.projectDir.absolutePath}"
        assertEquals(expected, project.spoon.srcFolders.first().absolutePath)
    }

    @Test
    public void testChangesOutFolderExtensionValue() throws Exception {
        project.spoon {
            outFolder = project.file('build/spoon')
        }
        project.evaluate()

        final String expected = "${project.buildDir.absolutePath}/spoon"
        assertEquals(expected, project.spoon.outFolder.absolutePath)
    }

    @Test
    public void testChangesPreserveFormattingExtensionValue() throws Exception {
        project.spoon {
            preserveFormatting = true
        }
        project.evaluate()

        assertTrue(project.spoon.preserveFormatting)
    }

    @Test
    public void testChangesNoClasspathExtensionValue() throws Exception {
        project.spoon {
            noClasspath = true
        }
        project.evaluate()

        assertTrue(project.spoon.noClasspath)
    }

    @Test
    public void testChangesProcessorsExtensionValue() throws Exception {
        project.spoon {
            processors = ['fr.inria.gforge.spoon.Processor']
        }
        project.evaluate()

        assertEquals(1, project.spoon.processors.size())
        assertEquals('fr.inria.gforge.spoon.Processor', project.spoon.processors.first())
    }

    @Test
    public void testChangesComplianceValue() throws Exception {
        project.spoon {
            compliance = 8
        }
        project.evaluate()

        assertEquals(8, project.spoon.compliance)
    }

    @Test(expected = PluginApplicationException.class)
    public void testLaunchPluginWithoutJavaPlugin() throws Exception {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'spoon'
        project.evaluate()
    }

    @Test
    public void testExecutionOfSpoonTask() throws Exception {
        project.spoon {
            srcFolders = project.files('.')
        }
        project.evaluate()
        executeSpoon(project)

        assertTrue(project.file("${project.buildDir}/generated-sources/spoon").exists())
    }

    @Test
    public void testClasspathWithAProjectWithCompileDependencies() throws Exception {
        project.repositories {
            mavenCentral()
        }
        project.dependencies {
            compile group: 'junit', name: 'junit', version: '4.11'
        }
        project.evaluate()

        assertNotNull(project.tasks.spoon.classpath.files.find {
            it.absolutePath.contains("junit${File.separator}junit${File.separator}4.11")
        })
    }

    @Test
    public void testNoClasspathWhenProjectDontHaveDependencies() throws Exception {
        project.evaluate()

        assertNull(project.tasks.spoon.classpath.files.find {
            it.absolutePath.contains("junit${File.separator}junit${File.separator}4.11")
        })
    }

    private static executeSpoon(Project project) {
        executeTask(project, 'spoon')
    }

    private static executeTask(Project project, String task) {
        def spoon = project.getTasksByName(task, true).first()
        spoon.actions.each { Action action ->
            action.execute(spoon)
        }
    }

    private static Project buildEvaluatableProject() {
        final Project project = getProject("")
        project.apply plugin: 'java'
        project.apply plugin: 'spoon'
        return project
    }

    private static Project buildEvaluatableProjectWithProjectPath(String projectPath) {
        final Project project = getProject(projectPath)
        project.apply plugin: 'java'
        project.apply plugin: 'spoon'
        return project
    }

    private static Project getProject(String projectPath) {
        def builder = ProjectBuilder.builder()
        if (!projectPath.empty) {
            builder.withProjectDir(new File(projectPath))
        }
        builder.build()
    }
}
