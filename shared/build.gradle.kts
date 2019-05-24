plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-jdk8", version = "1.2.1")

    compile(group = "commons-io", name = "commons-io", version = "2.6")

    compile(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.9.9")
    compile(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = "2.9.9")
}
