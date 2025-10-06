package ynotnaexists.reviverod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ynotnaexists.reviverod.blocks.ReviveBlocks;
import ynotnaexists.reviverod.networking.CancelReviveC2SPayload;
import ynotnaexists.reviverod.networking.InitializeReviveS2CPayload;

public class ReviveRod implements ModInitializer {
	public static final String MOD_ID = "reviverod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ReviveBlocks.registerClass();
		ReviveSoundEffects.registerClass();
		PayloadTypeRegistry.playS2C().register(InitializeReviveS2CPayload.ID, InitializeReviveS2CPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(CancelReviveC2SPayload.ID, CancelReviveC2SPayload.CODEC);

		ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
			if (!(livingEntity instanceof ServerPlayerEntity deadPlayer)) return;

			ReviveManager existing = ReviveManager.getInstanceByPlayer(deadPlayer.getUuid());
			if (existing != null) {
				LOGGER.warn("ReviveManager already exists for player {}. Disposing of existing instance.", deadPlayer.getName().getString());
				existing.dispose();
			}

			new ReviveManager(deadPlayer, ReviveManager.findLifeRodBlockFromPlayer(deadPlayer, deadPlayer.getEntityWorld()));
        });

		ServerPlayNetworking.registerGlobalReceiver(CancelReviveC2SPayload.ID, (payload, context) -> {
			ReviveManager manager = ReviveManager.getInstanceByPlayer(payload.playerUuid());
			if (manager != null) {
				manager.setLifeRodPowered(false);
				manager.dispose();
			} else LOGGER.warn("Revive cancel attempted but no ReviveManager found");
		});
	}
}
