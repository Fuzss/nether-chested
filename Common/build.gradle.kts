plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
    modCompileOnlyApi(sharedLibs.limitlesscontainers.common)
}

spotless {
    format("TinyTakeoverV2") {
        target("src/main/java/**/*.java")

        replaceRegex(
            "Update common imports",
            "\\bimport\\s+fuzs\\.limitlesscontainers\\.api\\.",
            "import fuzs.limitlesscontainers.common.api."
        )
    }
}
