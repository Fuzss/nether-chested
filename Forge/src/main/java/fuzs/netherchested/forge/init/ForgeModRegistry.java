package fuzs.netherchested.forge.init;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.forge.world.level.block.entity.NetherChestForgeBlockEntity;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ForgeModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(NetherChested.MOD_ID);
    public static final Holder.Reference<BlockEntityType<NetherChestBlockEntity>> NETHER_CHEST_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityType("nether_chest", () -> BlockEntityType.Builder.of(
            NetherChestForgeBlockEntity::new, ModRegistry.NETHER_CHEST_BLOCK.get()));

    public static void touch() {

    }
}
