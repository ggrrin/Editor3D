/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import com.jogamp.opengl.math.Matrix4;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;

/**
 * Trida poskytujici implementaci meneni nastroju, rotace objektu a pohybu kamery
 * @author ggrrin_
 */
public abstract class ToolsGLJPanel extends InteractiveGLJPanel {

    protected Rotator ball = new Rotator(this);
    protected ITool currentTool = null;
    protected Matrix4 modelMat;
    
    /**
     * true  mozna je treba ulozeni data byla zmenena
     */
    public boolean save = false;
    


    /**
     * otevreni modelu
     * @param path soubor modelu
     * @throws IOException  chyba pri cteni souboru
     */
    public abstract void open(File path) throws IOException;

    /**
     * restart do puvodniho stavu
     */
    public abstract void reset();

    
    /**
     * ulozeni modelu
     * @param path soubor modelu
     * @throws IOException chyba pri cteni souboru
     */
    public abstract void save(File path) throws IOException;

    /**
     * Vrati matici celeho modelu
     * @return dana matice
     */
    public Matrix4 getModelmatrix() {
        return modelMat;
    }

    /**
     * Vybere danny nastroj 
     * @param t nastroj
     */
    public void selectTool(ITool t) {
        if (currentTool != null) {
            currentTool.reset();
        }
        currentTool = t;
        t.init();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseClicked(e);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (currentTool != null) {
            currentTool.mouseWheelMoved(e);
        }
    }

    /**
     * Rotace objektu
     * @param e 
     */
    private void dragRotate(MouseEvent e) {

        if (e.getModifiers() == 4/*rightMouseButton*/) {
            ball.getCurentRotation(e.getPoint(), modelMat);
            ball.getCameraMove(e.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseDragged(e);
        }

        dragRotate(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseMoved(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseReleased(e);
        }

        ball.stopRotate();
        ball.stopMove();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (currentTool != null) {
            currentTool.mouseExited(e);
        }
    }
}
