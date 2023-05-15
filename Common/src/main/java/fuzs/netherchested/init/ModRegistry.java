package fuzs.netherchested.init;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModLoader;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(NetherChested.MOD_ID);
    public static final RegistryReference<Block> NETHER_CHEST_BLOCK = REGISTRY.registerBlockWithItem("nether_chest", () -> new NetherChestBlock(BlockBehaviour.Properties.copy(Blocks.ENDER_CHEST)), CreativeModeTab.TAB_DECORATIONS);
    public static final RegistryReference<BlockEntityType<NetherChestBlockEntity>> NETHER_CHEST_BLOCK_ENTITY_TYPE = REGISTRY.whenNotOn(ModLoader.FORGE).registerBlockEntityTypeBuilder("nether_chest", () -> ModBlockEntityTypeBuilder.of(NetherChestBlockEntity::new, NETHER_CHEST_BLOCK.get()));
    public static final RegistryReference<MenuType<NetherChestMenu>> NETHER_CHEST_MENU_TYPE = REGISTRY.registerMenuTypeSupplier("nether_chest", () -> NetherChestMenu::new);

    public static void touch() {

    }
}
