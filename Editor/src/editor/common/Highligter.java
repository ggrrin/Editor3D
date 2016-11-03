/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;

/**
 * Trida reprezentujici Zvyraznovac vrcholu jiz daneho vertex bufferu jineho modelu
 * @author ggrrin_
 */
public class Highligter implements IRenderComponent {

    private final int[] indexBufferID = new int[1];

    private int[] indexBufferData = new int[0];
    private float[] vertexBufferData;
    private boolean updateRequired = true;
    private float aspectRatio;
    private float width;

    private float pointSize;

    private int programShaderID;
    private int mvpMatrixShaderID;
    private int paramID;
    private int colorID;

    private Vector3 color;


    protected Drawable model;

    /**
     * Vybori novy zvyraznovac a asociuje ho s danym modelem d
     * @param d model nad kterym bude probyhat zvyraznovani
     */
    public Highligter(Drawable d) {
        setModel(d);
        this.pointSize = 15;
        this.color = new Vector3(1, 0.75f, 0);
    }
    
    private Object lock = new Object();
    
    /**
     * Nastaveni jineho modelu nad kterym bude probyhat zvyraznovani
     * @param d novy model
     */
    public void setModel(Drawable d)
    {
        synchronized(lock)
        {
            model = d;            
        }        
    }
  
    @Override
    public void init(GL3 gl, int programShaderId) {
        programShaderID = Drawable.loadShaders(gl, "simple.vs", "simple.fs", "simple.gs");
        mvpMatrixShaderID = gl.glGetUniformLocation(programShaderID, "MVP");
        paramID = gl.glGetUniformLocation(programShaderID, "param");
        colorID = gl.glGetUniformLocation(programShaderID, "color");

        gl.glGenBuffers(1, IntBuffer.wrap(indexBufferID));
    }

    /**
     * Nastaveni noveho indexbufferu nad vertexbufferem stavajciho modelu
     * @param buffer novy buffer
     */
    public void resetIndexBuffer(Collection<Integer> buffer) {
        synchronized (indexBufferID) {
            updateRequired = true;
            
            if(buffer == null)
                indexBufferData = new int[0];
            
            indexBufferData = new int[buffer.size()];
            int i = 0;
            for (int index : buffer) {
                indexBufferData[i++] = index;
            }
        }
    }

    @Override
    public void update(GL3 gl) {
        if (updateRequired) {
            synchronized (indexBufferID) {
                updateRequired = false;
                gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, indexBufferID[0]);
                gl.glBufferData(GL3.GL_ARRAY_BUFFER, indexBufferData.length * 4, IntBuffer.wrap(indexBufferData), GL3.GL_DYNAMIC_DRAW);

            }
        }
    }

    @Override
    public void reshape(GL3 gl, int x, int y, int width, int height) {
        aspectRatio = width / (float) height;
        this.width = width;
    }

    @Override
    public void draw(GL3 gl, Matrix4 mvp) {

        if(model == null)
            return;
        
        gl.glUseProgram(programShaderID);

 
        Matrix4 mat = new Matrix4();
        mat.multMatrix(mvp);
        mat.multMatrix(model.getModelMatrix());

        gl.glUniformMatrix4fv(mvpMatrixShaderID, 1, false, FloatBuffer.wrap(mat.getMatrix()));
        gl.glUniform3f(paramID, pointSize, aspectRatio, width);
        gl.glUniform3f(colorID, color.X, color.Y, color.Z);

        {
            gl.glEnableVertexAttribArray(0);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, model.getVbId());
            gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 0, 0);

            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, indexBufferID[0]);
            gl.glDrawElements(GL3.GL_POINTS, indexBufferData.length, GL3.GL_UNSIGNED_INT, 0);

            gl.glDisableVertexAttribArray(0);
        }
    }

    @Override
    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, IntBuffer.wrap(indexBufferID));
    }

}
