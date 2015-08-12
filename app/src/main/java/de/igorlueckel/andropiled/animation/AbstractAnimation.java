package de.igorlueckel.andropiled.animation;

import android.support.annotation.NonNull;

import de.igorlueckel.andropiled.services.NetworkService;

/**
 * Created by Igor on 04.08.2015.
 */
public abstract class AbstractAnimation extends Thread {
    boolean isInfinite;
    long tickDuration;
    NetworkService networkService;
    boolean stopped = false;
    int[] lastColor;
    NetworkService.AnimationEventHandler animationEventHandler;

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setIsInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }

    public long getTickDuration() {
        return tickDuration;
    }

    public void setTickDuration(long tickDuration) {
        this.tickDuration = tickDuration;
    }

    /**
     * Inject the NetworkService before it is used in the NetworkService
     * @param networkService
     */
    public void setNetworkService(@NonNull NetworkService networkService) {
        this.networkService = networkService;
    }

    NetworkService getNetworkService() {
        return networkService;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int[] getLastColor() {
        return lastColor;
    }

    public void setLastColor(int[] lastColor) {
        this.lastColor = lastColor;
    }

    public NetworkService.AnimationEventHandler getAnimationEventHandler() {
        return animationEventHandler;
    }

    public void setAnimationEventHandler(NetworkService.AnimationEventHandler animationEventHandler) {
        this.animationEventHandler = animationEventHandler;
    }
}
