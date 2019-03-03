import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21" apply false
    id("org.jetbrains.intellij") version "0.4.3" apply false
    id("com.github.ben-manes.versions") version "0.20.0" apply false
}

group = "com.almightyalpaca.jetbrains.plugins.discord"
version = "1.0.0-SNAPSHOT"

subprojects {
    group = rootProject.group.toString() + project.name
    version = rootProject.version

    repositories {
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    apply(plugin = "com.github.ben-manes.versions")

    tasks {
        "dependencyUpdates"(DependencyUpdatesTask::class) {
            resolutionStrategy {
                componentSelection {
                    all {
                        sequenceOf("alpha", "beta", "rc", "cr", "m", "preview")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-_]*", RegexOption.IGNORE_CASE) }
                            .any { regex -> regex.matches(candidate.version) }
                            .let { if (it) reject("Release candidate") }
                    }
                }
            }
        }
    }

    val secrets = rootProject.file("secrets.gradle.kts")
    if (secrets.exists()) {
        apply(from = secrets)

        // project.extra["DISCORD_TOKEN"] as String?
    }
}
