package com.cyberpixel.matimematics.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

public class GameStateManager {

    private Stack<State> states;

    public GameStateManager(){
        states = new Stack<State>();
    }

    public void setState(State state){
        states.peek().dispose();
        states.pop();
        states.push(state);
    }

    public void popState(){
        states.peek().dispose();
        states.pop();
    }

    public void pushState(State state){
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }

    public void resize(int width, int height){states.peek().resize(width,height);}

    public void dispose() {
        states.peek().dispose();
    }

}
