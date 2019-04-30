package com.cyberpixel.matimematics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends State {

    private Texture texSigns;

    private Texture texBg;
    private Texture texTopRect;
    private Texture texBotRect;
    private Texture texBgBar;


    private Texture texSettings;
    private Texture texLife;
    private Texture texTimeBar;

    private Texture texFalling;

    private BitmapFont fnt;

    private TextureRegion texrAdd;

    private OrthographicCamera cam;
    private Viewport view;

    private int screenWidth = 360;
    private int screenHeight = 640;

    private float maxTime = 10;
    private float curTime = 5;

    private float speed;

    private int num1,num2,num3,sign;

    private int proby = 125;
    private int probxspace = 45;
    private final int fntsize = 16;
    private int fntSpaceWidth;

    private float goodSpan = 0.75f;
    private boolean drawGoodAndMiss = false;
    private float goodCurTime = 0;
    private boolean good = false;

    private boolean gameOver = false;

    private int life = 3;

    private Rectangle[] rectsBars = new Rectangle[4];
    private Rectangle[] rectsOverlap = new Rectangle[4];

    private Rectangle rectRetry;
    private Rectangle rectQuit;

    private Array<Falling> fallings = new Array();

    RandomXS128 rand;

    private int score = 0;

    private int highscore = 0;

    private Texture texQuitRetry;

    private GlyphLayout glyphHighScore;


    private Preferences save;
    private Rectangle rectQuitGame;

    public Game(GameStateManager gsm) {
        super(gsm);
        rand = new RandomXS128(System.currentTimeMillis());

        initTextures();
        initCamView();
        initRects();

        fnt = new BitmapFont(Gdx.files.internal("ocraext.fnt"));
        fntSpaceWidth = (int) fnt.getSpaceWidth();

        sr = new ShapeRenderer();
        genRandProb();

        speed = 250;

        rectRetry = new Rectangle(-8,-55,78,35);
        rectQuit = new Rectangle(-40-50,-55,78,35);

        loadHighScore();

        rectQuitGame = new Rectangle(180-texSettings.getWidth()-25,320-texSettings.getHeight()-25,
                texSettings.getWidth()+5,texSettings.getHeight()+5);
    }

    float time=0;
    @Override
    public void update(float dt) {
        if(gameOver){
            if(Gdx.input.justTouched())
                gameOverClick();
            return;
        }
        curTime-=dt;
        time+=dt;
        if(Gdx.input.justTouched()){
            whenClicked();

        }

        for(Falling f: fallings){
            f.moveDown(dt*speed);
        }

        if(time>=0.75f){
            int s = rand.nextInt(4);
            fallings.add(new Falling(rand.nextInt(4),s));
            time-=0.75f;
        }


        for(int i=0;i<fallings.size;i++){
            if(fallings.get(i).y<=-360){
                fallings.removeIndex(i);
            }
        }

        if(curTime<=0) {
            if(life<=0){
                gameOver();
            }
            genRandProb();
            life--;
            curTime=maxTime;
            good = false;
            drawGoodAndMiss = true;
        }

        if(drawGoodAndMiss){
            goodCurTime+=dt;
        }


    }




    private ShapeRenderer sr;
    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        cam.update();

        renderBgs(sb);
        renderLife(sb);

        for(Falling f: fallings){
            sb.draw(f.tex,f.rect.x,f.rect.y);
            fnt.draw(sb,f.txtSign,f.fntx,f.fnty);
        }
        sb.draw(texTopRect,-texTopRect.getWidth()/2,64);
        renderProblem(sb);


        sb.draw(texSettings,180-texSettings.getWidth()-20,320-texSettings.getHeight()-20);
        sb.draw(texTimeBar,-180,63,360* (curTime/maxTime),texTimeBar.getHeight());
        sr.setProjectionMatrix(cam.combined);

        fnt.draw(sb,""+score,-150,250);

        fnt.getData().setScale(0.5f);
        fnt.draw(sb,"High Score:"+highscore,-170,310);
        fnt.draw(sb,"fps:"+Gdx.graphics.getFramesPerSecond()+"",-175,-305);
        fnt.getData().setScale(1);


        if(drawGoodAndMiss){
            if(goodCurTime<goodSpan){
                drawGoodAndMiss(sb);
            }else{
                goodCurTime=0;
                drawGoodAndMiss =false;
            }
        }

        if(gameOver){
            sb.draw(texQuitRetry,-texQuitRetry.getWidth()/2,-100);
        }

//
//        sb.draw(texrAdd,-8,-55,16,16);
//        sb.draw(texrAdd,15+55,-20,16,16);
//        sb.draw(texrAdd,-40-50,-55,16,16);
//        sb.draw(texrAdd,-23,-20,16,16);

    }

    @Override
    public void resize(int width, int height) {
        view.update(width,height);
    }

    @Override
    public void dispose() {
        texBg.dispose();
        texBgBar.dispose();
        texBotRect.dispose();
        texFalling.dispose();
        texLife.dispose();
        texQuitRetry.dispose();
        texSettings.dispose();
        texTimeBar.dispose();
        texTopRect.dispose();
        texSigns.dispose();
    }


    private void initTextures(){
        texBg = new Texture("bg2.png");
        texTopRect = new Texture("rectTop.png");
        texBotRect = new Texture("rectBot.png");

        texSigns = new Texture("signs2.png");
        texrAdd = new TextureRegion(texSigns,64*3,0,64,64);

        texBgBar = new Texture("bgbar.png");

        texLife = new Texture("life.png");
        texSettings = new Texture("quitgame.png");
        texTimeBar = new Texture("timebar.png");
        texFalling = new Texture("falling.png");
        texQuitRetry = new Texture("quitretry.png");

    }

    private void initCamView(){
        cam = new OrthographicCamera();
        view =  new StretchViewport(screenWidth,screenHeight,cam);
        view.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    }

    private void whenClicked(){
        Vector3 input = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        for(int i=0;i<4;i++) {
            for(int j=0;j<fallings.size;j++) {
                if (rectsBars[i].contains(input.x, input.y)) {
                    checkAns(i);
                    genRandProb();
                    break;
                }
            }
        }

        if(rectQuitGame.contains(input.x,input.y)){
            System.out.println("ZERRROOOOo");
            gameOver=true;
        }


    }


    class Falling{
        public float y;
        public float x;
        public float fntx;
        public float fnty;
        public int row;
        public Texture tex;
        public Rectangle rect;
        public String txtSign;
        public int sign;

        public Falling(int row,int sign){
            tex = texFalling;
            this.row=row;
            this.sign = sign;
            y = 64;

            txtSign =
                    sign==0?"/":
                    sign==1?"x":
                    sign==2?"-":
                            "+";


            if(row==0){
                x=-30+(-tex.getWidth())*2;
            }else if(row==1){
                x=-10-tex.getWidth();
            }else if(row==2){
                x=10;
            }else if(row==3){
                x=30+tex.getWidth();
            }

            fntx = x+(tex.getWidth()/2)-10;
            fnty = y+texFalling.getHeight()/2+10;

            rect = new Rectangle(x,y,texFalling.getWidth(),texFalling.getHeight());
        }

        public void moveDown(float dt){
            y-=dt;
            fnty=y+texFalling.getHeight()/2+10;
            rect.setX(x);
            rect.setY(y);
        }

    }

    private void genRandProb(){
        sign = rand.nextInt(4);
        do {
            num1 = 10 + rand.nextInt(500);
            num2 = 10 + rand.nextInt(500);
        }while(num1%num2!=0 && sign==0);

        if(sign==1) {
            num1 = 10 + rand.nextInt(21);
            num2 = 10 + rand.nextInt(21);
        }
        num3 = (
                sign==0?(num1/num2):
                sign==1?(num1*num2):
                sign==2?(num1-num2):
                        (num1+num2)
                );
        curTime=maxTime;
    }

    private void renderProblem(SpriteBatch sb){
        fnt.draw(sb,""+num1,probxspace-180,proby);
        fnt.draw(sb,"_",probxspace+(fntsize *3)+ fntSpaceWidth *1-screenWidth/2,proby);
        fnt.draw(sb,""+num2,probxspace+(fntsize *4)+ fntSpaceWidth *2-screenWidth/2,proby);
        fnt.draw(sb,"=",probxspace+(fntsize *7)+ fntSpaceWidth *3-screenWidth/2,proby);
        fnt.draw(sb,""+num3,probxspace+(fntsize *8)+ fntSpaceWidth *4-screenWidth/2,proby);

    }

    private void renderLife(SpriteBatch sb){
        for(int i=1;i<=life;i++)
            sb.draw(texLife,120-5*i-texLife.getWidth()*(i-1),147);
    }

    private void renderBgs(SpriteBatch sb){
        sb.draw(texBg,-texBg.getWidth()/2,-texBg.getHeight()/2);
        sb.draw(texBotRect,-texBotRect.getWidth()/2,-303);

        sb.draw(texBgBar,-10-texBgBar.getWidth(),64-texBgBar.getHeight());
        sb.draw(texBgBar,-30+(-texBgBar.getWidth())*2,64-texBgBar.getHeight());
        sb.draw(texBgBar,10,64-texBgBar.getHeight());
        sb.draw(texBgBar,30+texBgBar.getWidth(),64-texBgBar.getHeight());
    }

    private void initRects(){
        rectsBars[0] = new Rectangle(-30-texBgBar.getWidth()*2,64-texBgBar.getHeight(),texBgBar.getWidth(),texBgBar.getHeight());
        rectsBars[1] = new Rectangle(-10-texBgBar.getWidth(),64-texBgBar.getHeight(),texBgBar.getWidth(),texBgBar.getHeight());
        rectsBars[2] = new Rectangle(-20+texBgBar.getWidth()/2,64-texBgBar.getHeight(),texBgBar.getWidth(),texBgBar.getHeight());
        rectsBars[3] = new Rectangle(-30+texBgBar.getWidth()*2,64-texBgBar.getHeight(),texBgBar.getWidth(),texBgBar.getHeight());

        rectsOverlap[0] = new Rectangle(-30-texBgBar.getWidth()*2,64-texBgBar.getHeight(),texBgBar.getWidth(),texBotRect.getHeight());
        rectsOverlap[1] = new Rectangle(-10-texBgBar.getWidth(),64-texBgBar.getHeight(),texBgBar.getWidth(),texBotRect.getHeight());
        rectsOverlap[2] = new Rectangle(-20+texBgBar.getWidth()/2,64-texBgBar.getHeight(),texBgBar.getWidth(),texBotRect.getHeight());
        rectsOverlap[3] = new Rectangle(-30+texBgBar.getWidth()*2,64-texBgBar.getHeight(),texBgBar.getWidth(),texBotRect.getHeight());
    }

    private void gameOver(){
        System.out.println("Game Over");
        gameOver=true;
    }

    private void checkAns(int row){
        good = false;
        System.out.println(row);
        boolean found = false;
        for(int i=0;i<fallings.size;i++) {
            System.out.println(fallings.get(i).txtSign+"   "+sign+":"+fallings.get(i).sign+"    ||    "+rectsOverlap[row].overlaps(fallings.get(i).rect));
            if(sign==fallings.get(i).sign && rectsOverlap[row].overlaps(fallings.get(i).rect)) {
                score++;
                if(score>highscore)
                    highscore=score;
                fallings.removeIndex(i);
                found=true;
                good = true;
                break;
            }
        }
        System.out.println("Found: "+found);
        if(!found){
            if(life<=0)
                gameOver();
            life--;
            fallings.clear();
        }
        drawGoodAndMiss=true;
    }

    public void drawGoodAndMiss(SpriteBatch sb){
        float scale = 2+(goodCurTime<=(goodSpan/4)?goodCurTime:(goodSpan/4));
        fnt.getData().setScale(scale);
        fnt.draw(sb,good?"Good":"Miss",-(fntsize/2)*4*scale,-50);
        fnt.getData().setScale(1);
    }

    private void gameOverClick(){
        saveHighScore();
        Vector3 input = cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
        if(rectRetry.contains(input.x,input.y)){
            System.out.println("Click game oversss!");
            gsm.setState(new Game(gsm));
        }else if(rectQuit.contains(input.x,input.y)){
            gsm.popState();
        }
    }
    private void loadHighScore() {
//        System.out.println(Gdx.files.local("highscore").readString());
//        if(Gdx.files.local("highscore").exists()){
//            highscore = Integer.parseInt(Gdx.files.local("highscore").readString());
//        }else
//            highscore=0;
//
//        System.out.println(highscore);
        save = Gdx.app.getPreferences("highscore");
        if (save.contains("highscore")) {
            highscore = save.getInteger("highscore");
        } else
            highscore = 0;
    }

    private void saveHighScore() {
//        Gdx.files.local("highscore").writeString(highscore+"",false);
        save.putInteger("highscore", highscore);
        save.flush();
    }
}


