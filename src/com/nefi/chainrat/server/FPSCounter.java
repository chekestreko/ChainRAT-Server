package com.nefi.chainrat.server;

public class FPSCounter {

    private int averageFrameCount;
    private long lastTime;
    private long frameCount;
    private double passedTime;
    private double fpsSum;
    private double aggFps, avgFps;

    public FPSCounter(int avgFrameCount) {
        this.averageFrameCount = avgFrameCount;

        this.frameCount = 0;
        this.passedTime = 0;
        this.fpsSum = 0.0;

        this.lastTime = System.nanoTime();
    }

    public double getAverageFps() {
        return this.avgFps;
    }


    public double nextFrame() {
        return this.nextFrame(System.nanoTime() - this.lastTime);
    }

    private double nextFrame(long deltaNanos) {
        this.frameCount++;
        this.passedTime += deltaNanos;

        this.lastTime = System.nanoTime();
        final double fps = 1.e9 / (double) deltaNanos;
        this.fpsSum += fps;

        if (frameCount >= this.averageFrameCount) {
            this.aggFps = (double) this.frameCount / ((double) this.passedTime / 1.e9);
            this.avgFps = this.fpsSum / (double) this.frameCount;

            this.frameCount = 0;
            this.passedTime = 0;
            this.fpsSum = 0.0;
        }

        return fps;
    }

}
