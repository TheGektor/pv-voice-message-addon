package ru.thegektor.pvaddon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.thegektor.pvaddon.PVAddonClient;
import ru.thegektor.pvaddon.feature.audio.AudioRecorder;
import ru.thegektor.pvaddon.feature.networking.NetworkManager;
import ru.thegektor.pvaddon.feature.ui.RecordButton;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addRecordButton(CallbackInfo ci) {
        int buttonWidth = 20;
        int buttonHeight = 20;
        // Position to the right of the chat input field (approximate, needs tuning)
        int x = this.width - buttonWidth - 2; 
        int y = this.height - 14 - buttonHeight; // Above command suggestion area

        RecordButton recordButton = new RecordButton(x, y, buttonWidth, buttonHeight, button -> {
            if (button instanceof RecordButton rb) {
                boolean isRecording = rb.isRecording();
                AudioRecorder recorder = PVAddonClient.getAudioRecorder();
                
                if (!isRecording) {
                    // Start Recording
                    recorder.startRecording();
                    rb.setRecording(true);
                } else {
                    // Stop Recording & Send
                    rb.setRecording(false); // Update UI immediately
                    recorder.stopRecording().thenAccept(data -> {
                        NetworkManager.sendVoiceMessage(data);
                    });
                }
            }
        });

        this.addDrawableChild(recordButton);
    }
}
