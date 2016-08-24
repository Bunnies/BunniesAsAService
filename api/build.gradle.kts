import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val baasVersion by project
val dropwizardVersion by project

buildscript {
    repositories {
        gradleScriptKotlin()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("com.github.jengelman.gradle.plugins:shadow:1.2.3")
    }
}

apply {
    plugin("kotlin")
    plugin("com.github.johnrengelman.shadow")
}

repositories {
    gradleScriptKotlin()
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile("io.dropwizard:dropwizard-core:$dropwizardVersion")
    compile("com.google.code.gson:gson:2.3.1")
    compile("com.bendb.dropwizard:dropwizard-redis:$dropwizardVersion-0")

    testCompile("junit:junit:4.11")
}

val buildNumberAddition = if(project.hasProperty("BUILD_NUMBER")) { ".${project.property("BUILD_NUMBER")}" } else { "" }

version = "$baasVersion$buildNumberAddition"
group = "io.bunnies.baas"

shadowJar {
    mergeServiceFiles()
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}

jar {
    manifest.attributes += "Main-Class" to "io.bunnies.baas.BaasApplication"
}

fun Project.jar(setup: Jar.() -> Unit) = (project.tasks.getByName("jar") as Jar).setup()
fun Project.shadowJar(setup: ShadowJar.() -> Unit) = (project.tasks.getByName("shadowJar") as ShadowJar).setup()
