package fuzs.netherchested.client.init;

import fuzs.netherchested.NetherChested;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;

public class ClientModRegistry {
    public static final Material NETHER_CHEST_LOCATION = new Material(Sheets.CHEST_SHEET, NetherChested.id("entity/chest/nether"));
}
