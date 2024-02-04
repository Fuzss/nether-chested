package fuzs.netherchested.init;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(NetherChested.MOD_ID);
    public static final RegistryReference<Block> NETHER_CHEST_BLOCK = REGISTRY.registerBlock("nether_chest", () -> new NetherChestBlock(BlockBehaviour.Properties.copy(Blocks.ENDER_CHEST)));
    public static final RegistryReference<Item> NETHER_CHEST_ITEM = REGISTRY.registerBlockItem(NETHER_CHEST_BLOCK);
    public static final RegistryReference<BlockEntityType<NetherChestBlockEntity>> NETHER_CHEST_BLOCK_ENTITY_TYPE = REGISTRY.whenNotOn(ModLoader.FORGE).registerBlockEntityType("nether_chest", () -> BlockEntityType.Builder.of(NetherChestBlockEntity::new, NETHER_CHEST_BLOCK.get()));
    public static final RegistryReference<MenuType<NetherChestMenu>> NETHER_CHEST_MENU_TYPE = REGISTRY.registerMenuType("nether_chest", () -> NetherChestMenu::new);

    public static void touch() {

    }
}
