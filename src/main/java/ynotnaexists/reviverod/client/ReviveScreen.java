package ynotnaexists.reviverod.client;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;
import ynotnaexists.reviverod.networking.CancelReviveC2SPayload;

import java.util.List;

public class ReviveScreen extends Screen {

    private final List<ButtonWidget> buttons = Lists.<ButtonWidget>newArrayList();
    private final boolean isHardcore;
    private final Text message = Text.translatable("revivescreen.revive_screen_message");

    public ReviveScreen(boolean isHardcore) {
        super(Text.translatable("revivescreen.revive_screen_title"));
        this.isHardcore = isHardcore;
    }

    @Override
    protected void init() {
        ButtonWidget titleScreenButton;
        this.buttons.clear();
        ClientPlayerEntity player = this.client.player;
        Text acceptText = Text.translatable("revivescreen.revive_accept");
        Text declineText = Text.translatable("revivescreen.revive_decline");
        this.buttons.add(this.addDrawableChild(ButtonWidget.builder(acceptText, button -> {
            player.requestRespawn();
            button.active = false;
        }).dimensions(this.width / 2 - 100, this.height / 4 + 60, 200, 20).build()));
        this.buttons.add(this.addDrawableChild(ButtonWidget.builder(declineText, button -> {
            ClientPlayNetworking.send(new CancelReviveC2SPayload(player.getUuid()));
            player.requestRespawn();
            button.active = false;
        }).dimensions(this.width / 2 - 100, this.height / 4 + 84, 200, 20).build()));
        titleScreenButton = addDrawableChild(
                ButtonWidget.builder(
                                Text.translatable("deathScreen.titleScreen"),
                                button -> this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::onTitleScreenButtonClicked, true)
                        )
                        .dimensions(this.width / 2 - 100, this.height / 4 + 108, 200, 20)
                        .build()
        );
        this.buttons.add(titleScreenButton);
        this.setButtonsActive(true);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(2.0F, 2.0F);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, 30, Colors.WHITE);
        context.getMatrices().popMatrix();
        context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 85, Colors.WHITE);
        if (mouseY > 85 && mouseY < 85 + 9) {
            Style style = this.getTextComponentUnderMouse(mouseX);
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        fillBackgroundGradient(context, this.width, this.height);
    }

    @Nullable
    private Style getTextComponentUnderMouse(int mouseX) {
        int i = this.client.textRenderer.getWidth(this.message);
        int j = this.width / 2 - i / 2;
        int k = this.width / 2 + i / 2;
        return mouseX >= j && mouseX <= k ? this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j) : null;
    }

    private void setButtonsActive(boolean active) {
        for (ButtonWidget buttonWidget : this.buttons) {
            buttonWidget.active = active;
        }
    }

    private void onTitleScreenButtonClicked() {
        if (this.isHardcore) {
            this.quitLevel();
        } else {
            ConfirmScreen confirmScreen = new TitleScreenConfirmScreen(confirmed -> {
                if (confirmed) {
                    this.quitLevel();
                } else {
                    ClientPlayNetworking.send(new CancelReviveC2SPayload(this.client.player.getUuid()));
                    this.client.player.requestRespawn();
                    this.client.setScreen(null);
                }
            }, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
            this.client.setScreen(confirmScreen);
            confirmScreen.disableButtons(20);
        }
    }

    static void fillBackgroundGradient(DrawContext context, int width, int height) {
        context.fillGradient(0, 0, width, height, 0x9921A3B9, 0x9121A3B9);
    }

    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
        }

        this.client.disconnectWithSavingScreen();
        this.client.setScreen(new TitleScreen());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static class TitleScreenConfirmScreen extends ConfirmScreen {
        public TitleScreenConfirmScreen(BooleanConsumer booleanConsumer, Text text, Text text2, Text text3, Text text4) {
            super(booleanConsumer, text, text2, text3, text4);
        }

        @Override
        public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            fillBackgroundGradient(context, this.width, this.height);
        }
    }
}
