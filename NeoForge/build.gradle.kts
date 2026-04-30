plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    modCompileOnly(sharedLibs.puzzleslib.common)
    modApi(sharedLibs.puzzleslib.neoforge)
    modCompileOnly(sharedLibs.limitlesscontainers.common)
    modApi(sharedLibs.limitlesscontainers.neoforge)
    include(sharedLibs.limitlesscontainers.neoforge)
}
