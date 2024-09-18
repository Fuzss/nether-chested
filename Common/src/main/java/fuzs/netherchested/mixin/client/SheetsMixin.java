package fuzs.netherchested.mixin.client;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
abstract class SheetsMixin {
    @Unique
    private static final Material NETHERCHESTED$NETHER_CHEST_LOCATION = new Material(Sheets.CHEST_SHEET, NetherChested.id("entity/chest/nether"));

    @Inject(method = "chooseMaterial", at = @At("HEAD"), cancellable = true)
    private static void chooseMaterial(BlockEntity blockEntity, ChestType chestType, boolean holiday, CallbackInfoReturnable<Material> callback) {
        if (blockEntity instanceof NetherChestBlockEntity) callback.setReturnValue(NETHERCHESTED$NETHER_CHEST_LOCATION);
    }
}
