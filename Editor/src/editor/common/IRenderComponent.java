/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;

/**
 * Rozhrani vykreslitelne komponenty, takovat komponenta lze preda rendereru
 * a ten ji pak sam vykresluje
 * @author ggrrin_
 */
public interface IRenderComponent {

    /**
     * inicializace openGl
     * @param gl gl context
     * @param programShaderId shader 
     */
    void init(GL3 gl, int programShaderId);

    /**
     * Update volana pred draw ve smyce
     * @param gl  gl context
     */
    default void update(GL3 gl) {}

    /**
     * Volanoi pri kazdem vykreslovani komponenty
     * @param gl gl context
     * @param mvp trasformacni matice
     */
    void draw(GL3 gl, Matrix4 mvp);
    
    /**
     * Zmena velkosti vykreslovani panelu nebo poprv pri spusteni smycky
     * @param gl gl context
     * @param x pouzice componenty
     * @param y pouzice componenty
     * @param width sirka kompontnty
     * @param height vyska komponenty
     */
    default void reshape(GL3 gl, int x, int y, int width, int height){}

    /**
     * disposnuti zdrujo z graficke karty
     * @param gl  gl context
     */
    void dispose(GL3 gl);

}
