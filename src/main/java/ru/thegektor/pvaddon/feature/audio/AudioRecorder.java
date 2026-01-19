package ru.thegektor.pvaddon.feature.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger("pv-addon-audio");
    private TargetDataLine line;
    private final AudioFormat format;
    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private ByteArrayOutputStream outputStream;
    private Thread recordingThread;

    public AudioRecorder() {
        this.format = new AudioFormat(48000, 16, 1, true, false);
    }

    public void startRecording() {
        if (isRecording.get()) return;

        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                LOGGER.error("Line not supported: {}", format);
                return;
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            isRecording.set(true);
            outputStream = new ByteArrayOutputStream();

            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                try {
                    while (isRecording.get() && line.isOpen()) {
                        int read = line.read(buffer, 0, buffer.length);
                        if (read > 0) {
                            outputStream.write(buffer, 0, read);
                        }
                    }
                } catch (Exception e) {
                    // Ignore exception on stop
                }
            }, "PV-Audio-Recorder");
            recordingThread.start();
            LOGGER.info("Recording started. Line open: {}", line.isOpen());

        } catch (LineUnavailableException e) {
            LOGGER.error("Microphone unavailable: ", e);
        }
    }

    public java.util.concurrent.CompletableFuture<byte[]> stopRecording() {
        java.util.concurrent.CompletableFuture<byte[]> future = new java.util.concurrent.CompletableFuture<>();
        
        if (!isRecording.compareAndSet(true, false)) {
            future.complete(new byte[0]);
            return future;
        }

        new Thread(() -> {
            try {
                if (line != null && line.isOpen()) {
                    line.stop();
                    line.close(); // This should interrupt the read
                }
                
                if (recordingThread != null) {
                    recordingThread.join(1000);
                }
                
                byte[] result = outputStream.toByteArray();
                LOGGER.info("Recording stopped. Bytes captured: {}", result.length);
                future.complete(result);
            } catch (Exception e) {
                LOGGER.error("Error stopping recording: ", e);
                future.completeExceptionally(e);
            }
        }, "PV-Recorder-Stopper").start();

        return future;
    }

    public boolean isRecording() {
        return isRecording.get();
    }
}
