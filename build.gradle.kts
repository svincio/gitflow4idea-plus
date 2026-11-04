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
version = "1.0.0"

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
            <p><b>Git-flow for IntelliJ-based IDEs</b>: start, finish and publish features, releases, bugfixes and hotfixes from the status bar widget or the <b>Git → Gitflow</b> menu.</p>
            <h3>Features</h3>
            <ul>
              <li>Status bar widget with the git-flow actions available for the current branch</li>
              <li><b>Start</b>, <b>Finish</b> and <b>Publish</b> for features, releases, bugfixes and hotfixes; <b>Track</b> remote features, releases and bugfixes</li>
              <li>Merge conflicts during Finish Feature, Bugfix or Release open the standard IntelliJ merge dialog, and the finish resumes once they are resolved</li>
              <li>Projects with multiple Git repositories</li>
              <li>Optional integration with Task Management</li>
            </ul>
            <h3>Requirements</h3>
            <ul>
              <li>IntelliJ IDEA 2024.2 or later, or another JetBrains IDE with Git</li>
              <li>A git-flow CLI on the <code>PATH</code>: <a href="https://github.com/gittower/git-flow-next">git-flow-next</a> or <a href="https://github.com/petervanderdoes/gitflow-avh">git-flow AVH Edition</a>.
                Windows: <code>winget install GitTower.GitFlowNext</code> · macOS: <code>brew install git-flow-next</code> · Debian/Ubuntu: <code>apt install git-flow</code></li>
            </ul>
            <h3>Credits</h3>
            <p>Maintained fork of <a href="https://github.com/RubinCarter/gitflow4idea-plus">gitflow4idea-plus</a> by Rubin Carter,
            originally <a href="https://github.com/OpherV/gitflow4idea">gitflow4idea</a> by Opher Vishnia. Apache License 2.0.</p>
            <p>Source code and issues: <a href="https://github.com/svincio/gitflow4idea-plus">github.com/svincio/gitflow4idea-plus</a></p>
        """)
        pluginVersion.set("${project.version}")
        sinceBuild.set("242")
        untilBuild.set(provider { null })
        changeNotes.set("""
            <H2>Changelog for 1.0.0</H2>
            <ul>
              <li>First stable release on the JetBrains Marketplace (same features as 0.9.3)</li>
              <li>New plugin icon</li>
            </ul>
            <H2>Changelog for 0.9.3</H2>
            <ul>
              <li>Handle git-flow version check failures without reporting an IDE error, and clarify the installation guidance</li>
            </ul>
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
