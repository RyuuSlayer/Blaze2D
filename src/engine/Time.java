package engine;

import java.util.concurrent.TimeUnit;

public class Time {
    private static final long second = 1000000000L;
    public static float timeScale = 1;
    private static float time;
    private static float deltaTime;
    private static float unscaledDelta;
    private static int frameRate;

    private static long startTime;
    private static long lastFrameTime;
    private static double unprocessedTime;
    private static int currentFrameRate;

    static void Init() {
        lastFrameTime = System.nanoTime();
        startTime = lastFrameTime;
    }

    public static float GetTime() {
        return time;
    }

    public static float DeltaTime() {
        return deltaTime;
    }

    public static float UnscaledDelta() {
        return unscaledDelta;
    }

    public static int FrameRate() {
        return frameRate;
    }


    static void Process() {
        long rawTime = System.nanoTime();
        time = (float) (TimeUnit.MILLISECONDS.convert(rawTime - startTime, TimeUnit.NANOSECONDS));

        long framePassedTime = rawTime - lastFrameTime;
        lastFrameTime = rawTime;

        double rawDelta = framePassedTime / (double) second;
        if (rawDelta > 0.01f) {
            deltaTime = (float) (0.01 * timeScale);
            unscaledDelta = 0.01f;
        } else {
            deltaTime = (float) (rawDelta * timeScale);
            unscaledDelta = (float) rawDelta;
        }
        unprocessedTime += rawDelta;
        currentFrameRate++;

        if (unprocessedTime >= 1.0f) {
            frameRate = currentFrameRate;
            currentFrameRate = 0;
            unprocessedTime = 0;
        }
    }
}
