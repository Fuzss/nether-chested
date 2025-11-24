package fuzs.netherchested.init;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(NetherChested.MOD_ID);
    /**
     * @see net.minecraft.core.component.DataComponents#CONTAINER
     */
    public static final Holder.Reference<DataComponentType<ItemContainerContents>> UNLIMITED_CONTAINER_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "unlimited_container",
            (DataComponentType.Builder<ItemContainerContents> builder) -> {
                return builder.persistent(NetherChestBlockEntity.ITEM_CONTAINER_CONTENTS_CODEC)
                        .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                        .cacheEncoding();
            });
    public static final Holder.Reference<Block> NETHER_CHEST_BLOCK = REGISTRIES.registerBlock("nether_chest",
            () -> new NetherChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENDER_CHEST)));
    public static final Holder.Reference<Item> NETHER_CHEST_ITEM = REGISTRIES.registerBlockItem(NETHER_CHEST_BLOCK);
    public static final Holder.Reference<BlockEntityType<NetherChestBlockEntity>> NETHER_CHEST_BLOCK_ENTITY_TYPE = REGISTRIES.whenNotOn(
                    ModLoader.FORGE)
            .registerBlockEntityType("nether_chest",
                    () -> BlockEntityType.Builder.of(NetherChestBlockEntity::new, NETHER_CHEST_BLOCK.value()));
    public static final Holder.Reference<MenuType<NetherChestMenu>> NETHER_CHEST_MENU_TYPE = REGISTRIES.registerMenuType(
            "nether_chest",
            () -> NetherChestMenu::new);

    public static void touch() {
        // NO-OP
    }
}
