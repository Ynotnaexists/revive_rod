package ynotnaexists.reviverod.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import ynotnaexists.reviverod.ReviveRod;

import java.util.UUID;

public record CancelReviveC2SPayload(UUID playerUuid) implements CustomPayload {

    public static final Identifier CANCEL_REVIVE_PAYLOAD_ID = Identifier.of(ReviveRod.MOD_ID, "cancel_revive");
    public static final CustomPayload.Id<CancelReviveC2SPayload> ID = new CustomPayload.Id<>(CANCEL_REVIVE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, CancelReviveC2SPayload> CODEC =
            PacketCodec.tuple(Uuids.PACKET_CODEC, CancelReviveC2SPayload::playerUuid, CancelReviveC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
