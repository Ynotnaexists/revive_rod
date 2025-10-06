package ynotnaexists.reviverod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import ynotnaexists.reviverod.networking.InitializeReviveS2CPayload;

public class ReviveRodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(InitializeReviveS2CPayload.ID, (payload, context) -> {
            ClientPlayerEntity player = context.client().player;
            if (player.getEntityWorld() == null) return;

            context.client().setScreen(new ReviveScreen(context.client().world.getLevelProperties().isHardcore()));
        });
    }
}
