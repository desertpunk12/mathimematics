package com.cyberpixel.matimematics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyberpixel.matimematics.states.GameStateManager;
import com.cyberpixel.matimematics.states.Menu;

public class Main extends ApplicationAdapter{

    public final String TITLE="MaTimeMatics";
    public final int WIDTH=360;
    public final int HEIGHT=640;
//    public final int SCALE=3;

    private SpriteBatch sb;
    private GameStateManager gsm;

    @Override
    public void create() {
        sb = new SpriteBatch();
        gsm = new GameStateManager();
        gsm.pushState(new Menu(gsm));
        Gdx.graphics.setDisplayMode(getWidth(),getHeight(),false);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        sb.begin();
        gsm.render(sb);
        sb.end();
    }

    @Override
    public void resize(int width, int height) {
        gsm.resize(width,height);
    }

    @Override
    public void dispose() {
        gsm.dispose();
        sb.dispose();
    }

    //<editor-fold defaultstate="collapsed" desc="Configuration Getters">
    public String getTitle() {
        return TITLE;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

//    public int getScale(){
//        return SCALE;
//    }
    //</editor-fold>

}
