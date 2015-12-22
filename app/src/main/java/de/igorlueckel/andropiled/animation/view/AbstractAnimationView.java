package de.igorlueckel.andropiled.animation.view;

import android.graphics.drawable.Drawable;

import de.igorlueckel.andropiled.animation.AbstractAnimation;

/**
 * Created by Igor on 19.08.2015.
 */
public class AbstractAnimationView {
    AbstractAnimation animation;
    String name;
    String description;
    Drawable drawable;

    public AbstractAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(AbstractAnimation animation) {
        this.animation = animation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
