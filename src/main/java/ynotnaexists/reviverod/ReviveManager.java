package ynotnaexists.reviverod;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import ynotnaexists.reviverod.blocks.ReviveRodBlock;
import ynotnaexists.reviverod.blocks.ReviveBlocks;
import ynotnaexists.reviverod.networking.InitializeReviveS2CPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviveManager implements ServerPlayerEvents.AfterRespawn, ServerPlayConnectionEvents.Disconnect {
    private ServerPlayerEntity player;
    private BlockPos lifeRodBlockPos;
    private World world;
    protected static List<ReviveManager> reviveManagers = new ArrayList<>();

    public ReviveManager(ServerPlayerEntity player, BlockPos lifeRodBlockPos) {
        this.player = player;
        this.lifeRodBlockPos = lifeRodBlockPos;
        this.world = player.getEntityWorld();
        reviveManagers.add(this);
        ServerPlayerEvents.AFTER_RESPAWN.register(this);
        ServerPlayConnectionEvents.DISCONNECT.register(this);

        this.setLifeRodPowered(true);
        world.playSound(
            null,
            lifeRodBlockPos,
            ReviveSoundEffects.REVIVE_ROD_ACTIVATES.value(),
            SoundCategory.BLOCKS,
            1.0f,
            1.0f
        );

        ServerPlayNetworking.send(player, new InitializeReviveS2CPayload());
    }

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean b) {
        if (!oldPlayer.equals(player)) return;
        if (!world.getBlockState(lifeRodBlockPos).isOf(ReviveBlocks.REVIVE_ROD)) {
            newPlayer.sendMessage(Text.translatable("revive.fail.broken_rod"));
            return;
        }
        newPlayer.teleport(lifeRodBlockPos.getX(), lifeRodBlockPos.getY(), lifeRodBlockPos.getZ(), false);
        newPlayer.setHealth(1.0f);
        newPlayer.getHungerManager().setFoodLevel(1);
        this.setLifeRodPowered(false);

        this.dispose();
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if (handler.getPlayer().equals(player)) this.setLifeRodPowered(false);
    }

    public static BlockPos findLifeRodBlockFromPlayer(ServerPlayerEntity player, World world) {
        Box searchBox = new Box(player.getBlockPos()).expand(10);

        return BlockPos.stream(searchBox)
                .filter(p -> world.getBlockState(p).isOf(ReviveBlocks.REVIVE_ROD))
                .findFirst()
                .get();
    }
    public static List<ServerPlayerEntity> findDeadPlayersFromLifeRodBlockPos(BlockPos lifeRodBlockPos, World world) {
        Box searchBox = new Box(lifeRodBlockPos).expand(10);

        return PlayerLookup.all(world.getServer())
                .stream()
                .filter(p -> searchBox.contains(p.getBlockPos()) && p.isDead() && p instanceof PlayerEntity)
                .toList();
    }

    public static ReviveManager getInstanceByPlayer(UUID uuid) {
        return reviveManagers.stream()
                .filter(r -> r.player != null)
                .filter(r -> r.player.getUuid().equals(uuid) )
                .findFirst()
                .orElse(null);
    }

    public void setLifeRodPowered(boolean powered) {
        BlockState blockState = world.getBlockState(lifeRodBlockPos);
        ((ReviveRodBlock) blockState.getBlock()).setIsPowered(blockState, world, lifeRodBlockPos, powered);
    }

    public void dispose() {
        this.player = null;
        this.lifeRodBlockPos = null;
        this.world = null;
        reviveManagers.remove(this);
    }
}