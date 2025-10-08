package fuzs.netherchested.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = {"Multiplier for the max stack size for all items placed in a nether chest.", "The stack size of already damaged items cannot be increased."})
    @Config.IntRange(min = 1, max = 32)
    public int stackSizeMultiplier = 8;
    @Config(description = "Strength of the explosion when a nether chest is trying to be opened in the nether dimension.")
    @Config.DoubleRange(min = 0, max = 10)
    public int netherExplosionStrength = 0;
}
