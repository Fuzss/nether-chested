import fuzs.multiloader.mixin.MixinConfigJsonTask

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
}

tasks.named<MixinConfigJsonTask>("generateMixinConfig") {
    json {
        mixin("TransportItemTargetMixin", "TransportItemsBetweenContainersMixin")
    }
}
