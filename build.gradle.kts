group = "net.minestom"
plugins {
    id("maven-publish")
}

allprojects {
    version = "1.0"
}

val mcVersion = project.properties["mcVersion"].toString()
val outputDirectory = (findProperty("output") ?: rootDir.resolve("MinestomData").absolutePath) as String

tasks {
    register("generateData") {
        logger.warn("Mojang requires all source-code and mappings used to be governed by the Minecraft EULA.")
        logger.warn("Please read the Minecraft EULA located at https://account.mojang.com/documents/minecraft_eula.")
        logger.warn("In order to agree to the EULA you must create a file called eula.txt with the text 'eula=true'.")
        val eulaTxt = File("${rootProject.projectDir}/eula.txt")
        logger.warn("The file must be located at '${eulaTxt.absolutePath}'.")
        if ((eulaTxt.exists() && eulaTxt.readText(Charsets.UTF_8)
                .equals("eula=true", true)) || project.properties["eula"].toString().toBoolean()
        ) {
            logger.warn("")
            logger.warn("The EULA has been accepted and signed.")
            logger.warn("")
        } else {
            throw GradleException("Data generation has been halted as the EULA has not been signed.")
        }
        logger.warn("It is unclear if the data from the data generator also adhere to the Minecraft EULA.")
        logger.warn("Please consult your own legal team!")
        logger.warn("All data is given independently without warranty, guarantee or liability of any kind.")
        logger.warn("The data may or may not be the intellectual property of Mojang Studios.")
        logger.warn("")
        // Here is an example:
        // We want to run the data generator for the version 1.16.3
        // This will mean we want to run the code from the 1.16.5 generators with the 1.16.3 JAR on runtime
        // First of all we will deobf the 1.16.3 JAR and then, to COMPILE the 1.16.5 generators, deobf the 1.16.5 JAR
        // Then the 1.16.5 generators are included in the runtime of DataGenerator and using reflection they are accessed.
        // Why use reflection???
        // --> We want to allow the DataGenerator module to hit any version without having to necessarily compile every version
        // E.g. If we referenced the 1.17 generator in DataGenerator we NEED it for compile
        // However if we use reflection we can just "ignore its unavailability in the classpath"
        // This also allows the 1.17 generator to reference the 1.16.5 generator without much hassle.
        // As long as the 1.16.5 JAR is also decompiled for compiling the 1.16.5 generators.

        // TL;DR: We decompile one (or more) version for compile, and only ever one for runtime.
        // Run the deobfuscator
        dependsOn(project(":Deobfuscator").tasks.getByName<JavaExec>("run") {
            args = arrayListOf(mcVersion)
        }).finalizedBy(
            // Run the DataGenerator
            project(":DataGenerator").tasks.getByName<JavaExec>("run") {
                args = arrayListOf(outputDirectory)
            }
        )

    }
    register<Jar>("dataJar") {
        dependsOn("generateData")

        archiveBaseName.set("minestom-data")
        archiveVersion.set(mcVersion)
        destinationDirectory.set(layout.buildDirectory.dir("dist"))
        from(outputDirectory)
    }
}

publishing {
    publications {
        create<MavenPublication>("MinestomData") {
            groupId = "net.minestom"
            artifactId = "minestom-data"
            version = mcVersion

            artifact(tasks.getByName("dataJar"))
        }
    }
}