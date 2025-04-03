package fuzs.netherchested.client.renderer.blockentity;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import fuzs.puzzleslib.api.client.renderer.v1.SingleChestRenderer;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class NetherChestRenderer extends SingleChestRenderer<NetherChestBlockEntity, ChestModel> {
    static final ModelLayerFactory MODEL_LAYERS = ModelLayerFactory.from(NetherChested.MOD_ID);
    public static final ModelLayerLocation NETHER_CHEST_MODEL_LAYER_LOCATION = MODEL_LAYERS.registerModelLayer(
            "nether_chest");
    public static final ResourceLocation NETHER_CHEST_TEXTURE = NetherChested.id("nether");
    private static final Material NETHER_CHEST_LOCATION = Sheets.chestMaterial(NETHER_CHEST_TEXTURE);

    public NetherChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new ChestModel(context.bakeLayer(NETHER_CHEST_MODEL_LAYER_LOCATION)));
    }

    @Override
    protected Material getChestMaterial(NetherChestBlockEntity netherChestBlockEntity, boolean xmasTextures) {
        return xmasTextures ? Sheets.CHEST_XMAS_LOCATION : NETHER_CHEST_LOCATION;
    }
}
