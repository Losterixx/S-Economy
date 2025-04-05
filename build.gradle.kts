plugins {
    kotlin("jvm") version "2.1.20"

    id("com.gradleup.shadow") version "8.3.5"
}

group = "dev.losterixx"
version = "1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }

    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("dev.dejvokep:boosted-yaml:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}


tasks {
    jar {
        enabled = false
    }
    shadowJar {
        archiveClassifier.set("")

        from("src/main/kotlin/dev/losterixx/sEconomy/utils/bStats/Metrics.java") {
            include("dev/losterixx/sEconomy/utils/bStats/**")
        }

        relocate("dev.dejvokep.boostedyaml", "dev.losterixx.sEconomy.libs")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/kotlin", "src/main/java")
        }
    }
}
