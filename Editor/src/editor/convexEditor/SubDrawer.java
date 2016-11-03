/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.Highligter;
import editor.common.Renderer;

/**
 * Zvyraznovac bodu jiz hotoveho objektu
 * @author ggrrin_
 */
public class SubDrawer extends Highligter{

    Renderer rend;
    
    /**
     * inicilizuje s danym renererem
     * @param d odpovidajici renderer
     */
    public SubDrawer(Renderer d) {
        super(d.getCurentModel());
        rend = d;        
    }
    
    @Override
    public void draw(GL3 gl, Matrix4 mvp)
    {
        model = rend.getCurentModel();
        super.draw(gl, mvp);
    }
    
}
