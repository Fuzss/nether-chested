package fuzs.netherchested.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = {"Multiplier for the max stack size for all items placed in a nether chest.", "The stack size of already damaged items cannot be increased."})
    @Config.IntRange(min = 1, max = 32)
    public int stackSizeMultiplier = 8;
    @Config(description = "Explode a nether chest when it is trying to be opened in the nether dimension.")
    public boolean explodeInNether = false;
    @Config(description = "Strength of the explosion when a nether chest is trying to be opened in the nether dimension.")
    @Config.DoubleRange(min = 1, max = 10)
    public int netherExplosionStrength = 5;
    @Config(description = "Prevent a nether chest from opening when a block is placed directly above.")
    public boolean noBlockAbove = true;
    @Config(description = {"Only increase the stacks size for items that are already stackable, so items like tools will remain unstackable in a nether chest.", "Note that the stack size of already damaged items cannot be increased."})
    public boolean increaseStackableOnly = false;
}
