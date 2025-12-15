plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    modCompileOnly(libs.puzzleslib.common)
    modApi(libs.puzzleslib.neoforge)
    modCompileOnly(libs.limitlesscontainers.common)
    modApi(libs.limitlesscontainers.neoforge)
    include(libs.limitlesscontainers.neoforge)
}
