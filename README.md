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

## Installation

The plugin is not on the JetBrains Marketplace yet.

1. Download `git-flow-manager-<version>.zip` from the [Releases page](https://github.com/svincio/gitflow4idea-plus/releases). Do not unzip it.
2. In the IDE: **Settings → Plugins → ⚙ → Install Plugin from Disk…**, select the zip and restart the IDE.
3. If you had *Git Flow Integration Plus* (ID `Gitflow-Fix`) installed, uninstall it first, otherwise you get two Gitflow widgets.

The Gitflow widget appears in the status bar, the actions are in **Git → Gitflow** and in the widget popup.

## Building from source

```bash
./gradlew buildPlugin        # zip in build/distributions/
./gradlew verifyPlugin       # IntelliJ Plugin Verifier against 2026.2
./gradlew verifyPlugin -PverifyIde=2024.2.6   # against another IDE version
```

A JDK 25 is required to build (the 2026.2 platform classes are Java 25); Gradle downloads it automatically if missing.
The plugin itself is compiled to Java 21 bytecode, so it also runs on IDEs based on JBR 21.

## Caveats

While the plugin is operational and contains all basic functions (init/feature/release/hotfix), it may contains bugs. With your help I'll be able to find and zap them all.

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



