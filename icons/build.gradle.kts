import java.net.URI

plugins {
    kotlin("jvm")
}

group = "com.almightyalpaca.jetbrains.plugins.discord.icons"
version = "1.0.0-SNAPSHOT"

repositories {
    jcenter()
    maven { url = URI("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    compile(project(":shared"))

    compile(group = "org.apache.commons", name = "commons-text", version = "1.6")

    compile(group = "io.ktor", name = "ktor-client-okhttp", version = "1.1.3")
}

tasks {
    val `graphs-dot` by registering(JavaExec::class) task@{
        group = "icons"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.graphs.GraphsKt"
    }

    val graphs by registering task@{
        group = "icons"

        dependsOn(`graphs-dot`)

        doLast {
            val files = project.file("build/graphs").listFiles()
                .filter { f -> f.isFile }
                .map { f -> f.nameWithoutExtension }
            for (file in files) {
                exec {
                    workingDir = file("build/graphs")
                    commandLine = listOf("dot", "-Tpng", "$file.dot", "-o", "$file.png")
                }
            }
        }
    }

    val `validate-languages` by registering(JavaExec::class) task@{
        group = "icons"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.validator.LanguageValidatorKt"
    }

    val `validate-icons` by registering(JavaExec::class) task@{
        group = "icons"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.validator.IconValidatorKt"
    }

    val validate by registering task@{
        group = "icons"

        dependsOn(`validate-languages`)
        dependsOn(`validate-icons`)
    }

    test {
        dependsOn(validate)
    }

    if (project.extra.has("DISCORD_TOKEN")) {
        val `upload` by registering(JavaExec::class) task@{
            group = "icons"

            dependsOn(validate)

            sourceSets.main.configure { this@task.classpath = runtimeClasspath }
            main = "com.almightyalpaca.jetbrains.plugins.discord.icons.uploader.UploaderKt"

            environment("DISCORD_TOKEN", project.extra["DISCORD_TOKEN"] as String)
        }
    }
}
