plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group = "commons-io", name = "commons-io", version = "2.6")
    compile(group = "commons-collections", name = "commons-collections", version = "3.2.2")

    compile(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = "2.9.8")

    compile(group = "org.kohsuke", name = "github-api", version = "1.95")

    compile(group = "com.squareup.okhttp3", name = "okhttp-urlconnection", version = "3.13.1")
}
