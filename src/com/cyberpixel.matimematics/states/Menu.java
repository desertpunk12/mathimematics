package com.cyberpixel.matimematics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Menu extends State{


    private Texture texPlay;
    private Rectangle rectPlay;

    private Texture texLogo;
    private Texture texBg;
    private Texture texQuit;

    private Rectangle rectQuit;

    private OrthographicCamera cam;
    private Viewport view;


    private int screenWidth = 360;
    private int screenHeight = 640;


    public Menu(GameStateManager gsm) {
        super(gsm);

        texBg = new Texture("bg2.png");
        texLogo = new Texture("logo.png");
        texPlay = new Texture("play.png");

        rectPlay = new Rectangle(130 -screenWidth/2,150 -screenHeight/2,texPlay.getWidth(),texPlay.getHeight());


        cam = new OrthographicCamera();
        view =  new StretchViewport(screenWidth,screenHeight,cam);
        view.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        texQuit = new Texture("quit.png");
        rectQuit = new Rectangle(80,-280,texQuit.getWidth(),texQuit.getHeight());
    }


    @Override
    public void update(float dt) {
        cam.update();
        if(Gdx.input.justTouched()){
            whenClicked();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);

        sb.draw(texBg,-screenWidth/2,-screenHeight/2);
        sb.draw(texLogo,33 -screenWidth/2,350 -screenHeight/2);
        sb.draw(texPlay,130 -screenWidth/2,150 -screenHeight/2);

        sb.draw(texQuit,80,-280);
    }

    @Override

    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        texBg.dispose();
        texQuit.dispose();
        texPlay.dispose();
        texLogo.dispose();
    }

    private void whenClicked(){
        Vector3 input = cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
        if(rectPlay.contains(input.x,input.y)) {
            gsm.pushState(new Game(gsm));
        }else if(rectQuit.contains(input.x,input.y)){
            Gdx.app.exit();
        }
    }


}
