import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.21.0"
    kotlin("jvm") version "1.3.30" apply false
    id("org.jetbrains.intellij") version "0.4.7" apply false
    id("com.github.johnrengelman.shadow") version "5.0.0" apply false
}

group = "com.almightyalpaca.jetbrains.plugins.discord"
version = "1.0.0-SNAPSHOT"

subprojects {
    group = rootProject.group.toString() + "." + project.name.toLowerCase()
    version = rootProject.version

    repositories {
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

//    apply(plugin = "com.github.ben-manes.versions")

    val secrets = rootProject.file("secrets.gradle.kts")
    if (secrets.exists()) {
        apply(from = secrets)
    }
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "5.3.1"
}

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
