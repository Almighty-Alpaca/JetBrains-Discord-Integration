import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41" apply false
    id("com.github.ben-manes.versions") version "0.21.0"
    id("org.jetbrains.intellij") version "0.4.9" apply false
    id("com.github.johnrengelman.shadow") version "5.1.0" apply false
    id("com.palantir.git-version") version "0.11.0"
}

group = "com.almightyalpaca.jetbrains.plugins.discord"
val version = "1.0.0"

@Suppress("UNCHECKED_CAST")
val versionDetails = (project.extra["versionDetails"] as Closure<VersionDetails>)()
project.version = when (versionDetails.lastTag.endsWith(version)) {
    false -> "${"1.0.0"}-eap-${versionDetails.commitDistance}"
    true -> version
}

subprojects {
    group = rootProject.group.toString() + "." + project.name.toLowerCase()
    version = rootProject.version

    repositories {
        mavenCentral()
        jcenter()
    }

    val secrets = rootProject.file("secrets.gradle.kts")
    if (secrets.exists()) {
        apply(from = secrets)
    }

    secret("DISCORD_TOKEN")
    secret("BINTRAY_KEY")
    secret("JETBRAINS_TOKEN")

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

fun secret(name: String) {
    if (project.extra.has(name))
        return

    val env: String = System.getenv(name) ?: return

    project.extra[name] = env
}

defaultTasks = mutableListOf("plugin:buildPlugin")

tasks {
    dependencyUpdates {
        resolutionStrategy {
            componentSelection {
                all {
                    sequenceOf("alpha", "beta", "rc", "cr", "m", "preview", "eap")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-_]*", RegexOption.IGNORE_CASE) }
                        .any { regex -> regex.matches(candidate.version) }
                        .let { if (it) reject("Release candidate") }
                }
            }
        }
    }

    withType<Wrapper> {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "5.5"
    }

    val clean by registering(Delete::class) {
        group = "build"

        delete.add(project.buildDir)
    }
}
