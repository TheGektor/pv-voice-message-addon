package ru.thegektor.pvaddon.feature.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class AudioRecorder {
    private static final Logger LOGGER = Logger.getLogger("AudioRecorder");
    private TargetDataLine line;
    private final AudioFormat format; // 48kHz, 16bit, Mono usually for PV
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private ByteArrayOutputStream outputStream;
    private Thread recordingThread;

    public AudioRecorder() {
        // PV uses Opus 48kHz usually. We capture raw PCM 48kHz 16bit Mono.
        this.format = new AudioFormat(48000, 16, 1, true, false);
    }

    public void startRecording() {
        if (isRecording.get()) return;

        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                LOGGER.severe("Line not supported: " + format);
                return;
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            isRecording.set(true);
            outputStream = new ByteArrayOutputStream();

            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (isRecording.get()) {
                    int read = line.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                }
            });
            recordingThread.start();
            LOGGER.info("Recording started.");

        } catch (LineUnavailableException e) {
            LOGGER.severe("Microphone unavailable: " + e.getMessage());
        }
    }

    public java.util.concurrent.CompletableFuture<byte[]> stopRecording() {
        java.util.concurrent.CompletableFuture<byte[]> future = new java.util.concurrent.CompletableFuture<>();
        
        if (!isRecording.get()) {
            future.complete(new byte[0]);
            return future;
        }

        isRecording.set(false);
        // The recording thread will see the flag, stop reading, close line, and complete the future.
        // We need to pass the future to the thread or have the thread complete it.
        // Since we can't easily pass it to the running thread without a field, let's use a callback or just strictly handle it here but non-blocking?
        // Actually, line.stop() and line.close() might block. Ideally we do them in the thread.
        
        // Let's rely on the thread loop to finish.
        // But we need to capture the thread we started? We have 'recordingThread'.
        
        // Better approach: Submit a task to stop it? 
        // Or just let the loop break.
        
        new Thread(() -> {
            try {
                if (recordingThread != null) {
                    recordingThread.join(2000); // Wait for recorder to finish natural loop
                }
                // Now closing is done in the thread or here? 
                // Let's modify the recording loop to handle cleanup.
                
                // For simplicity in this hotfix: assuming line.stop() is fast enough or we accept tiny hiccup. 
                // BUT better:
                if (line != null && line.isOpen()) {
                     line.stop();
                     line.close();
                }
                
                LOGGER.info("Recording stopped. Size: " + outputStream.size());
                future.complete(outputStream.toByteArray());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }).start();

        return future;
    }

    public boolean isRecording() {
        return isRecording.get();
    }
}
