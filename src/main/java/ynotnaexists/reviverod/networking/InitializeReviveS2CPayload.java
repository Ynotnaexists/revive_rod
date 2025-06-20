package ynotnaexists.reviverod.networking;

import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import ynotnaexists.reviverod.ReviveRod;

public record InitializeReviveS2CPayload() implements CustomPayload {

    public static final Identifier INITIALIZE_REVIVE_PAYLOAD_ID = Identifier.of(ReviveRod.MOD_ID, "initialize_revive");
    public static final CustomPayload.Id<InitializeReviveS2CPayload> ID = new CustomPayload.Id<>(INITIALIZE_REVIVE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, InitializeReviveS2CPayload> CODEC = PacketCodec.unit(new InitializeReviveS2CPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
