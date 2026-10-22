# Git Flow Manager for IntelliJ

> **Maintained fork** by Simone Vincenzi of [gitflow4idea-plus](https://github.com/RubinCarter/gitflow4idea-plus) (Rubin Carter),
> itself a fork of [gitflow4idea](https://github.com/OpherV/gitflow4idea) (Opher Vishnia).
> It brings the plugin to current IntelliJ versions (2024.2 to 2026.2 and later). Plugin ID: `io.github.svincio.gitflow`.
> Changes from upstream are listed in the plugin change notes and in the git history.


An IntelliJ plugin providing a UI layer for git-flow, which in itself is a collection of Git extensions to provide high-level repository operations for Vincent [Driessen's branching model](http://nvie.com/git-model).

![screenshot](https://github.com/svincio/gitflow4idea-plus/blob/develop/docs/img/gitflow.png)

## Companion agent skill (experimental)

Using an AI coding agent? The upstream author also maintains **[gitflow-cli-plus](https://github.com/RubinCarter/gitflow-cli-plus)** — an agent skill companion to this plugin, derived from a functional analysis of its semantics. It ships a `SKILL.md` that teaches agents the same git-flow operations (init / feature / release / hotfix / bugfix), backed by a small Go CLI binary, follows the AVH edition semantics and config layout, and recognizes repos initialized by git-flow-next through a read-only compatibility layer.

**Note: this skill project is currently experimental** — expect rough edges, and please report anything you find in its [issue tracker](https://github.com/RubinCarter/gitflow-cli-plus/issues).

## Getting started

For the best introduction to get started with `git flow`, please read Jeff Kreeftmeijer's blog post:

[http://jeffkreeftmeijer.com/2010/why-arent-you-using-git-flow/](http://jeffkreeftmeijer.com/2010/why-arent-you-using-git-flow/)

Or have a look at this [cheat sheet](http://danielkummer.github.io/git-flow-cheatsheet/) by Daniel Kummer:

Huge shoutout [to Kirill Likhodedov](https://github.com/klikh), who wrote much of the original git4idea plugin, without which this plugin could not exist

## Requirements

- IntelliJ IDEA (or another JetBrains IDE with the Git plugin) **2024.2 or later**.
- A git-flow CLI on the `PATH`: [AVH edition](https://github.com/petervanderdoes/gitflow), [git-flow-next](https://github.com/gittower/git-flow-next) or [gitflow-cjs](https://github.com/CJ-Systems/gitflow-cjs).
  The original [nvie git-flow](https://github.com/nvie/gitflow) is not supported: it hasn't been maintained in years. See [GITFLOW_VERSION.md](GITFLOW_VERSION.md).
  On macOS the Homebrew formula `git-flow-avh` was disabled on 2025-03-03 (existing installs keep working); for new setups `brew install git-flow-next`.
  On Debian/Ubuntu `apt install git-flow` still installs AVH. On Windows, download git-flow-next from its [releases](https://github.com/gittower/git-flow-next/releases).

## Installation

The plugin is not on the JetBrains Marketplace yet.

1. Download `git-flow-manager-<version>.zip` from the [Releases page](https://github.com/svincio/gitflow4idea-plus/releases). Do not unzip it.
2. In the IDE: **Settings → Plugins → ⚙ → Install Plugin from Disk…**, select the zip and restart the IDE.
3. If you had *Git Flow Integration Plus* (ID `Gitflow-Fix`) installed, uninstall it first, otherwise you get two Gitflow widgets.

The Gitflow widget appears in the status bar, the actions are in **Git → Gitflow** and in the widget popup.

### Troubleshooting

- **The Gitflow widget does not appear in the status bar**: restart the IDE once more after installing, then right-click the status bar and check that *GitFlow status bar* is enabled.
- **"Unsupported Git Flow version"**: check `git flow version` in a terminal; see [Requirements](#requirements).
- Anything else: **Help → Show Log in Finder/Explorer** and attach `idea.log` (or the stack trace from the red error icon) to an [issue](https://github.com/svincio/gitflow4idea-plus/issues).

## Changes from upstream

Compared with [gitflow4idea-plus](https://github.com/RubinCarter/gitflow4idea-plus) 0.8.1-beta.2:

- Works on IntelliJ 2024.2 to 2026.2 and later (the upstream plugin is marked incompatible after 2024.1).
- The Task Management plugin is optional: it is no longer bundled with the IDE since 2026.x.
- No more IDE freeze while the status bar widget refreshes ([#47](https://github.com/RubinCarter/gitflow4idea-plus/issues/47)), no more `NullPointerException` on `gitflowBranchUtil` ([#42](https://github.com/RubinCarter/gitflow4idea-plus/issues/42)).
- Finish Feature/Bugfix/Release with merge conflicts: the standard Git merge dialog opens and the finish resumes by itself (it used to stop half-way on recent IDEs).
- git-flow-next merge conflicts are detected like the AVH ones.
- No internal or deprecated IntelliJ APIs; a memory leak in the popup is fixed.
- New plugin ID `io.github.svincio.gitflow` and name *Git Flow Manager*, so it can be installed and updated independently.

The full list is in the change notes (Settings → Plugins → Git Flow Manager) and in the git history.

## Building from source

```bash
./gradlew buildPlugin        # zip in build/distributions/
./gradlew verifyPlugin       # IntelliJ Plugin Verifier against 2026.2
./gradlew verifyPlugin -PverifyIde=2024.2.6   # against another IDE version
```

A JDK 25 is required to build (the 2026.2 platform classes are Java 25); Gradle downloads it automatically if missing.
The plugin itself is compiled to Java 21 bytecode, so it also runs on IDEs based on JBR 21.

## Known limitations

- **Finish Hotfix with merge conflicts** does not open the merge dialog (unchanged from upstream): resolve the conflicts with **Git → Resolve Conflicts**, commit, and complete the finish from a terminal with `git flow hotfix finish <name>`.
- **git-flow-next** support has been checked on the command line with git-flow-next 2.1.0, not yet inside the IDE. AVH is the edition tested end to end.
- **IDE versions before 2026.2** are checked with the IntelliJ Plugin Verifier only; hands-on testing so far is on IntelliJ IDEA 2026.2.
- The plugin may still contain bugs: please report them (see below).

## Helping out

This project is under active development.
If you encounter any bug or an issue, I encourage you to add the them to the [Issues list](https://github.com/svincio/gitflow4idea-plus/issues) on Github.
Feedback and suggestions are also very welcome.

## License

This plugin is under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
Copyright 2013-2020, Opher Vishnia.
Modifications copyright 2026, Simone Vincenzi.

## Who and why

This plugin was created by [Opher Vishnia](http://www.opherv.com), after I couldn't find any similar implementation.
I saw this [suggestion page](http://youtrack.jetbrains.com/issue/IDEA-65491) on the JetBrains site has more than 220 likes and 80 comments, and decided to take up the gauntlet :)

This plugin is forked from original https://github.com/OpherV/gitflow4idea .



