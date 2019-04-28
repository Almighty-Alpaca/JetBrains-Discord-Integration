import java.net.URI

plugins {
    kotlin("jvm")
}

group = "com.almightyalpaca.jetbrains.plugins.discord.icons"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven { url = URI("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    compile(project(":shared"))

    compile(group = "org.apache.commons", name = "commons-text", version = "1.6")

    compile(group = "io.ktor", name = "ktor-client-core", version = "1.1.4")
    compile(group = "io.ktor", name = "ktor-client-okhttp", version = "1.1.4")
    compile(group = "io.ktor", name = "ktor-client-auth", version = "1.1.4")
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


    val `find-unused-icons` by registering(JavaExec::class) task@{
        group = "icons"

        sourceSets.main.configure { this@task.classpath = runtimeClasspath }
        main = "com.almightyalpaca.jetbrains.plugins.discord.icons.find.UnusedIconFinderKt"
    }

    val find by registering task@{
        group = "icons"

        dependsOn(`find-unused-icons`)
    }

    test {
        dependsOn(validate)
    }

    if (project.extra.has("DISCORD_TOKEN") && project.extra.has("BINTRAY_KEY")) {
        val `upload-icons` by registering(JavaExec::class) task@{
            group = "icons"

//            dependsOn(`validate-icons`)

            sourceSets.main.configure { this@task.classpath = runtimeClasspath }
            main = "com.almightyalpaca.jetbrains.plugins.discord.icons.uploader.DiscordUploaderKt"

            environment("DISCORD_TOKEN", project.extra["DISCORD_TOKEN"] as String)
        }

        val `upload-languages` by registering(JavaExec::class) task@{
            group = "icons"

//            dependsOn(`validate-languages`)

            sourceSets.main.configure { this@task.classpath = runtimeClasspath }
            main = "com.almightyalpaca.jetbrains.plugins.discord.icons.uploader.BintrayUploaderKt"

            environment("BINTRAY_KEY", project.extra["BINTRAY_KEY"] as String)
        }

        val upload by registering task@{
            group = "icons"

//            dependsOn(validate)

            dependsOn(`upload-languages`)
            dependsOn(`upload-icons`)
        }
    }
}
