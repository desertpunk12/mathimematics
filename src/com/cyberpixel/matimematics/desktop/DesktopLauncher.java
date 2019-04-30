package com.cyberpixel.matimematics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cyberpixel.matimematics.Main;

public class DesktopLauncher
{
    public static void main(String[] args){
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Main coreMain = new Main();
        config.width=coreMain.getWidth();
        config.height=coreMain.getHeight();
        config.title=coreMain.getTitle();
        new LwjglApplication(new Main(),config);
    }

}
