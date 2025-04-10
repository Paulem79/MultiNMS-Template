pluginManagement {
    val shadowPluginVersion: String by settings
    val remapperPluginVersion: String by settings

    plugins {
        id("com.gradleup.shadow") version shadowPluginVersion
        id("io.github.patrick.remapper") version remapperPluginVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
rootProject.name = "MultiNMS-Template"

include(":NMS:Wrapper")

// You can name them in any way you want, but this is the "convention" used in the project
include(":NMS:1.19_R3")

include(":NMS:1.20_R1")
include(":NMS:1.20_R2")
include(":NMS:1.20_R3")
include(":NMS:1.20_R4")

include(":NMS:1.21_R1")
include(":NMS:1.21_R2")
include(":NMS:1.21_R3")
include(":NMS:1.21_R4")