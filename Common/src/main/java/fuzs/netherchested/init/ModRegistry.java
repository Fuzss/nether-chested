package fuzs.netherchested.init;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collections;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(NetherChested.MOD_ID);
    public static final Holder.Reference<Block> NETHER_CHEST_BLOCK = REGISTRIES.registerBlock("nether_chest",
            NetherChestBlock::new,
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.ENDER_CHEST));
    public static final Holder.Reference<Item> NETHER_CHEST_ITEM = REGISTRIES.registerBlockItem(NETHER_CHEST_BLOCK);
    public static final Holder.Reference<BlockEntityType<NetherChestBlockEntity>> NETHER_CHEST_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
            "nether_chest",
            NetherChestBlockEntity::new,
            () -> Collections.singleton(NETHER_CHEST_BLOCK.value()));
    public static final Holder.Reference<MenuType<NetherChestMenu>> NETHER_CHEST_MENU_TYPE = REGISTRIES.registerMenuType(
            "nether_chest",
            () -> NetherChestMenu::new);

    public static void bootstrap() {
        // NO-OP
    }
}
