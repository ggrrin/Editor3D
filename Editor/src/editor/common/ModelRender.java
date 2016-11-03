/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;

/**
 * Renderer vykreslujici jeden model/drawable
 * @author ggrrin_
 */
public class ModelRender extends Renderer {

    private Drawable model;
    private SimpleDrawer sd;

    
    @Override
    public Drawable getCurentModel() {
        return model;
    }

    /**
     * nastavy dosavadni vykreslovaci model na jiny m
     * @param m novy modle k vykreslovani
     */
    public void setCurrentModel(Drawable m) {
        if (model != null) {
            unregisterComponent(model);
        }
        model = m;
        registerComponent(model);
    }

    @Override
    public void init(GL3 gl, int programID) {

        sd = new SimpleDrawer(new Vector3(1.0f, 0.85f, 0.0f), 1f, 0.2f);
        sd.init(gl, programID);
    }

    @Override
    public void update(GL3 gl) {        
    }

    @Override
    public void draw(GL3 gl, Matrix4 mvp) {       
        sd.draw(gl, mvp);
    }

    @Override
    public void dispose(GL3 gl) {      
        sd.dispose(gl);
    }

}
