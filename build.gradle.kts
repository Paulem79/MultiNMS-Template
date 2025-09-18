// TODO: Add newer NMS versions

plugins {
    java

    id("com.gradleup.shadow")
    id("io.github.patrick.remapper")
}

val projectJava = JavaLanguageVersion.of(21)
val targetJava = 17

// Check if a project has the remap task and is not the root project
fun Project.usesReobfuscatedJar(proj: Project) : Boolean {
    return proj.tasks.findByName("remap") != null && proj.name != rootProject.name
}

allprojects {
    // Returns for the "not really a submodule (just NMS)" project
    if(project.name == "NMS") {
        return@allprojects
    }

    // IDK if apply is really needed here
    plugins.apply("java")
    if (usesReobfuscatedJar(this@allprojects)) {
        plugins.apply("com.gradleup.shadow")
        plugins.apply("io.github.patrick.remapper")
    }

    group = "com.example.plugin"
    version = "1.0"

    repositories {
        // Use 'mavenLocal()' to resolve dependencies from the local maven repository (Spigot with mojang mappings)
        mavenLocal()
        mavenCentral()

        maven {
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

            content {
                includeGroup("org.bukkit")
                includeGroup("org.spigotmc")
            }
        }

        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/central") }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // Needs to be afterEvaluate for dependency resolution
    afterEvaluate {
        dependencies {
            // Put common dependencies here
        }

        if(usesReobfuscatedJar(this@allprojects)) {
            // Get the mcVersion specified in gradle.properties
            val mcVersion = properties["mcVersion"].toString()
            val javaVersion = if(properties["javaVersion"] != null) {
                Integer.valueOf(properties["javaVersion"].toString())
            } else {
                targetJava
            }

            java {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(javaVersion))
                }
            }

            tasks.withType<JavaCompile> {
                options.release = targetJava
                targetJava.toString().let {
                    sourceCompatibility = it
                    targetCompatibility = it
                }
            }

            // Configure the remap task
            tasks.remap {
                dependsOn(tasks.shadowJar)
                inputTask.set(tasks.shadowJar)

                version.set(mcVersion)
                archiveName.set("${project.name}-${project.version}.jar")

                doLast {
                    // Delete the shadow jar after remapping
                    delete(tasks.shadowJar.get().archiveFile.get().asFile.absolutePath)
                }
            }

            tasks.assemble {
                dependsOn(tasks.remap)
            }

            dependencies {
                // Spigot with mojang mappings
                // you can use reobfuscated spigot jar, but you'll need to change the code to stop using the remap task
                compileOnly("org.spigotmc:spigot:${mcVersion}-R0.1-SNAPSHOT:remapped-mojang")
                // NMS Wrapper (API)
                compileOnly(project(":NMS:Wrapper"))
            }
        } else {
            dependencies {
                // If it's not using remap, it should be using the spigot-api (or spigot)
                compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
            }
        }
    }

}

// Get all subprojects except the famous "NMS"
val nmsProjects = subprojects.filter {
    return@filter (it.parent?.name ?: "") == "NMS"
}

dependencies {
    // NMS Implementations (for shading)
    for (nmsProject in nmsProjects) {
        implementation(project(path = ":NMS:${nmsProject.name}"))
    }
}

// Make sure to finish building submodules before building root project
tasks.compileJava {
    subprojects.forEach {
        if(it.name != "NMS") {
            dependsOn(it.tasks.build)
        }
    }
}

// Shade everything into a single jar
tasks.assemble {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain {
        languageVersion.set(projectJava)
    }
}

tasks.withType<JavaCompile> {
    options.release = targetJava
    targetJava.toString().let {
        sourceCompatibility = it
        targetCompatibility = it
    }
}

// Edit this if you update the gradle wrapper
tasks.wrapper {
    gradleVersion = "9.1.0"
    distributionType = Wrapper.DistributionType.ALL
}