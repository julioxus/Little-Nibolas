package com.codamasters.LNHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.codamasters.gameobjects.Horse;
import com.codamasters.screens.PantallaActual;


public class InputHandler implements InputProcessor {
	
    private float scaleFactorX;
    private float scaleFactorY;
    private Horse myNibolas;
    private PantallaActual pantalla;

    public InputHandler(PantallaActual miPantalla, float scaleFactorX,
            float scaleFactorY) {

        this.scaleFactorX = scaleFactorX;
        this.scaleFactorY = scaleFactorY;
        myNibolas = miPantalla.getHorse();
        pantalla = miPantalla;

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	
    	if(!pantalla.isMuerto()){
	    	myNibolas.move(screenX, screenY);
	        
	        myNibolas.onClick();
    	}
    	else
    		pantalla.restart();
    	
	        return true;
        
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenX = scaleX(screenX);
        screenY = scaleY(screenY);


        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private int scaleX(int screenX) {
        return (int) (screenX / scaleFactorX);
    }

    private int scaleY(int screenY) {
        return (int) (screenY / scaleFactorY);
    }

}
