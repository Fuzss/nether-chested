package fuzs.netherchested.init;

import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.netherchested.world.level.block.entity.NetherChestForgeBlockEntity;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ForgeModRegistry {
    public static final RegistryReference<BlockEntityType<NetherChestBlockEntity>> NETHER_CHEST_BLOCK_ENTITY_TYPE = ModRegistry.REGISTRY.registerBlockEntityTypeBuilder("nether_chest", () -> ModBlockEntityTypeBuilder.of(NetherChestForgeBlockEntity::new, ModRegistry.NETHER_CHEST_BLOCK.get()));

    public static void touch() {

    }
}
