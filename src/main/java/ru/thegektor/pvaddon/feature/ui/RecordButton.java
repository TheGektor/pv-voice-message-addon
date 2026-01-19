package ru.thegektor.pvaddon.feature.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RecordButton extends ButtonWidget {

    private boolean isRecording = false;
    private long startTime = 0;

    public RecordButton(int x, int y, int width, int height, PressAction onPress) {
        super(x, y, width, height, net.minecraft.text.Text.literal("Record"), onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = isRecording ? 0xFFFF0000 : 0xFFFFFFFF;
        context.fill(getX(), getY(), getX() + width, getY() + height, color);
        
        String text = "MIC";
        if (isRecording) {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            text = String.format("%02d:%02d", seconds / 60, seconds % 60);
        }

        int textColor = 0xFF000000;
        context.drawCenteredTextWithShadow(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer, 
            Text.literal(text), 
            getX() + width / 2, 
            getY() + (height - 8) / 2, 
            textColor
        );
    }

    public void setRecording(boolean recording) {
        this.isRecording = recording;
        if (recording) {
            this.startTime = System.currentTimeMillis();
        }
        // Message is now dynamic in render
    }

    public boolean isRecording() {
        return isRecording;
    }
}
