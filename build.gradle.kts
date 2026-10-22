import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

group = "io.github.svincio"
version = "0.9.2"

java {
    toolchain {
        // JDK 25 is needed to read the 2026.2 platform classes (class file version 69)
        languageVersion = JavaLanguageVersion.of(25)
    }
}

// Emit Java 21 bytecode so the plugin also loads on IDEs running on JBR 21
tasks.withType<JavaCompile> {
    options.release = 21
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    intellijPlatform {
        intellijIdea("2026.2.2")
        bundledPlugin("Git4Idea")
        bundledModule("intellij.platform.vcs.dvcs")
        bundledModule("intellij.platform.vcs.dvcs.impl")
        // Task Management UI is no longer bundled since 2026.x: optional dependency (see gitflow-tasks.xml)
        plugin("com.intellij.tasks", "262.8665.173")
        bundledModule("intellij.platform.tasks")
        bundledModule("intellij.platform.tasks.impl")
        pluginVerifier()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            // -PverifyIde=<version> (and -PverifyIdeType=IU for releases before 2025.3) to check other IDE versions
            create(
                IntelliJPlatformType.fromCode(providers.gradleProperty("verifyIdeType").getOrElse("IU")),
                providers.gradleProperty("verifyIde").getOrElse("2026.2.2"),
            )
        }
    }
}

tasks {
    patchPluginXml {
        pluginId.set("io.github.svincio.gitflow")
        pluginDescription.set("""
            <H2>Git Flow Integration for IntelliJ</H2>
            An IntelliJ plugin providing a UI layer for git-flow, which in itself is a collection of Git extensions to provide high-level repository operations for Vincent <a href="https://nvie.com/posts/a-successful-git-branching-model/">Driessen's branching model</a>.
            <p>Requires a git-flow CLI: AVH Edition, git-flow-next or gitflow-cjs.</p>
            <p>Maintained fork for current IntelliJ versions (2024.2 to 2026.2 and later) by Simone Vincenzi: <a href="https://github.com/svincio/gitflow4idea-plus">https://github.com/svincio/gitflow4idea-plus</a>.
            Based on <a href="https://github.com/RubinCarter/gitflow4idea-plus">gitflow4idea-plus</a> by Rubin Carter,
            originally <a href="https://github.com/OpherV/gitflow4idea">gitflow4idea</a> by Opher Vishnia. Apache License 2.0.</p>
        """)
        pluginVersion.set("${project.version}")
        sinceBuild.set("242")
        untilBuild.set(provider { null })
        changeNotes.set("""
            <H2>Changelog for 0.9.2</H2>
            <ul>
              <li>git-flow-next: merge conflicts during Finish Feature/Bugfix/Release are detected, the Git merge dialog opens and the finish resumes</li>
            </ul>

            <H2>Changelog for 0.9.1</H2>
            <ul>
              <li>New plugin ID and vendor: maintained fork "Git Flow Manager" by Simone Vincenzi</li>
              <li>Support for IntelliJ 2024.2 to 2026.2 (builds 242 to 262): migrate to IntelliJ Platform Gradle Plugin 2.x</li>
              <li>Task Management plugin is now an optional dependency (no longer bundled in 2026.x) #51</li>
              <li>Fix "Freeze in EDT": status bar widget refresh runs off the EDT #47</li>
              <li>Fix NullPointerException on gitflowBranchUtil #42</li>
              <li>Fix merge-conflict flow of Finish Feature/Bugfix/Release: the finish was never resumed on 2026.x; conflicts are now resolved with the standard Git merge dialog, without the "Was the merge completed?" prompt</li>
              <li>Remove all internal, override-only, deprecated and scheduled-for-removal API usages flagged by the Plugin Verifier</li>
            </ul>

            <H2>Changelog for 0.8.1-beta.2</H2>
            <ul>
              <li>Support git-flow-next implementation #45</li>
              <li>Support gitflow-cjs implementation #34</li>
              <li>Fix "Could not determine git flow version" for Windows users (Git 2.51.1+) #44</li>
              <li>Add git-flow-next config compatibility layer (from PR #46)</li>
              <li>Smart branch detection for init dialog (prioritize main over master)</li>
              <li>Improve init prompt matching for different git-flow implementations</li>
              <li>Improve unsupported version error message with installation guidance</li>
            </ul>

            <H2>Changelog for 0.7.13</H2>
            <ul>
              <li>Fix "(class com.intellij.openapi.project.impl.ProjectImpl) has already been disposed" #29 </li>
            </ul>
            
            <H2>Changelog for 0.7.11</H2>
            <ul>
              <li>Fix "Access is allowed from event dispatch thread only" #17 #21 #19 </li>
            </ul>
            
            <H2>Changelog for 0.7.10</H2>
            <ul>
              <li>Fix "Error during startup" #4</li>
            </ul>
        
            <H2>Changelog for 0.7.9</H2>
            <ul>
              <li>Support for 2022.1 build</li>
            </ul>
        
            <H2>Changelog for 0.7.8</H2>
            <ul>
              <li>Support for 2021.3 build</li>
            </ul>
        
            <H2>Changelog for 0.7.7</H2>
            <ul>
              <li>Fix issue with "Unsupported gitflow version" message presented at startup #328 #329</li>
              <li>Support for 2021.2 build</li>
            </ul>
        
            <H2>Changelog for 0.7.6</H2>
            <ul>
              <li>Fix "Error using shortcuts" #322</li>
              <li>fix Finishing BugFix throws stacktrace #320</li>
              <li>Support for 2021.1 build</li>
            </ul>
        
            <H2>Changelog for 0.7.5</H2>
            <ul>
              <li>PluginException: Icon cannot be found in 'AllIcons.Vcs.CheckOut' #314 (@tumb1er)</li>
              <li>Support for 2020.3 build</li>
            </ul>
        
            <H2>Changelog for 0.7.4</H2>
            <ul>
              <li>Fix deprecations #298 (@fabmars)</li>
              <li>Support for 2020.2 build</li>
            </ul>
        
            <H2>Changelog for 0.7.3</H2>
            <ul>
              <li>Implemented sorting and filtering of track branch dialog #290 (@mmopitz)</li>
              <li>Fix Version 0.7.2 causes that Active Tool Windows only is showed in one project if you have several open #301 (@tumb1er)</li>
              <li>Fix Unsupported Git Flow version Fix #302 (@opherv)</li>
              <li>Fix init settings shown in UI are misleading (do not match default) #283 (@opherv)</li>
            </ul>
        
            <H2>Changelog for 0.7.2</H2>
            <ul>
              <li>Support for Idea build 200 #276 (@fabmars, @tumb1er )</li>
              <li>Fix Icon cannot be found in 'AllIcons.Vcs.' #286 (@fabmars)</li>
              <li>Fix finish release error (Mac OS) #273 (@opherv)</li>
              <li>Breaking 'Search Everywhere' dialog window for projects without git #265 (@opherv)</li>
            </ul>
        
            <H2>Changelog for 0.7.1</H2>
            <ul>
              <li>Support for Idea build 193 #259 (@opherv)</li>
              <li>Check that the user has AVH version of git flow installed, show dialog otherwise #253 (@opherv)</li>
              <li>Add safety which should help fix #249 - Init repo failed #259 (@opherv)</li>
              <li>Fix Memory leak of ProjectImpl and GitRepositoryImpl after projet is closed #255 (@opherv)</li>
              <li>Add icons to actions #232 (@opherv)</li>
            </ul>
        
            <H2>Changelog for 0.7.0</H2>
            <ul>
              <li>Fix NPE when clicking Gitflow menu #245 (@opherv)</li>
              <li>Fix "Init gitflow" doesn't update widget #247 (@opherv)</li>
              <li>Fix Wrong message when finishing a feature #144 (@opherv)</li>
              <li>Feature: Re-init gitflow (access from VCS>Git>Gitflow>Advanced menu) #50 (@bmwsedee/@opherv)</li>
              <li>"Feature": Don't show branch select combo on new Hotfix (@opherv)</li>
            </ul>
        
            <H2>Changelog for 0.6.9</H2>
            <ul>
              <li>Support for Idea build 192 #241 (@opherv)</li>
              <li>Feature: No Fast-forward option is not working #225 (@opherv)</li>
              <li>Hotfix: option -k to keep branch after performing finish #199 (@opherv)</li>
              <li>Exceptions #243 #235 #323 #223 (@bmwsedee)</li>
            </ul>
        
            <H2>Changelog for 0.6.8</H2>
            <ul>
              <li>Support for Idea build 191 #221 (@ottnorml)</li>
              <li>Fix performance issues in plugin #195 (@bmwsedee)</li>
            </ul>
        
            <p>Note - if you see 'no gitflow' in the status bar you will need to re-init using <code>git flow init -f</code></p>
        """)
    }
}
