/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 * Trida zajistujici nizkourovnovou komunikaci s opengl a vykreslovani objektu
 * pomoce vertexbufferu a indexbufferu
 * @author ggrrin_
 */
public abstract class Drawable implements IRenderComponent {

    private int programID;


    private int[] vertexbufferID = new int[1];
    private int[] indexbufferID = new int[1];


    private int mvpMatrixShaderID;

    public int getVbId() {
        return vertexbufferID[0];
    }

    public int getIbId() {
        return indexbufferID[1];
    }

    /**
     * Vrati indexBuffer
     * @return indexBuffer
     */
    public abstract List<Integer> getIndexBuffer();

    /**
     * Vrati vertex Buffer
     * @return  vertexbuffer
     */
    public abstract List<Vector3> getVertexBuffer();

    /*
    Vrati barvu objektu
    */
    public abstract Vector3 getColor();

    /**
     * Vrati transformacni matici objektu
     * @return dana matice
     */
    public abstract Matrix4 getModelMatrix();


    int wColor;
    int meshColor;

    boolean useGlobalColor = true;
    
    private Vector3 wireColor = new Vector3(0,0,0);
    
    /**
     * Nastavi barvu car pri vykreslovani
     * @param v barva urcena vektorem hodnoty 0-1
     */
    public void setWireColor(Vector3 v)
    {
        wireColor = v;
    }

    /**
     * Inicializace bufferu a dalsich potrebnych veci pro vykresleni pomoci opengl
     * @param gl kontext gl
     * @param programId id pouzivaneho shaderu
     */
    @Override
    public void init(GL3 gl, int programId) {
        this.programID = programId;
        mvpMatrixShaderID = gl.glGetUniformLocation(programID, "MVP");
        wColor = gl.glGetUniformLocation(programId, "wireColor");
        meshColor = gl.glGetUniformLocation(programId, "meshColor");

        //VB
        gl.glGenBuffers(1, IntBuffer.wrap(vertexbufferID));
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexbufferID[0]);

        //IB
        gl.glGenBuffers(1, IntBuffer.wrap(indexbufferID));
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, indexbufferID[0]);

        restartBuffers(gl);

    }

    /**
     * Znovu nahraje buffery do graficke karty
     * @param gl kontext gl
     */
    public void restartBuffers(GL3 gl) {
        int[] indices = new int[getIndexBuffer().size()];
        float[] vertices = new float[getVertexBuffer().size() * 3];

        int i = 0;
        for (Vector3 v : getVertexBuffer()) {
            vertices[i++] = v.X;
            vertices[i++] = v.Y;
            vertices[i++] = v.Z;
        }

        i = 0;
        for (int k : getIndexBuffer()) {
            indices[i++] = k;
        }

        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexbufferID[0]);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * 4, FloatBuffer.wrap(vertices), GL3.GL_STATIC_DRAW);

        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, indexbufferID[0]);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, IntBuffer.wrap(indices), GL3.GL_STATIC_DRAW);

    }

    /**
     * vykresli objekt
     * @param gl kontext
     * @param mvp transformacni matice
     */
    @Override
    public void draw(GL3 gl, Matrix4 mvp) {

        gl.glUseProgram(programID);

        Matrix4 mat = new Matrix4();
        mat.multMatrix(mvp);
        mat.multMatrix(getModelMatrix());

        gl.glUniformMatrix4fv(mvpMatrixShaderID, 1, false, FloatBuffer.wrap(mat.getMatrix()));
        gl.glUniform3f(wColor, wireColor.X, wireColor.Y, wireColor.Z);

        if (useGlobalColor) {
            gl.glUniform3f(meshColor, getColor().X, getColor().Y, getColor().Z);
        } else {
            gl.glUniform3f(meshColor, -1, -1, -1);
        }

        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        {
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexbufferID[0]);
            gl.glVertexAttribPointer(
                    0, // attribute 0. 
                    3, // size
                    GL3.GL_FLOAT, // type
                    false, // normalized
                    0, // stride
                    0 // array buffer offset
            );

            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexbufferID[0]);//TODO chyba
            gl.glVertexAttribPointer(
                    1, // attribute 0. N
                    3, // size
                    GL3.GL_FLOAT, // type
                    false, // normalized
                    0, // stride
                    0 // array buffer offset
            );


            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, indexbufferID[0]);

            gl.glDrawElements(GL3.GL_TRIANGLES, getIndexBuffer().size(), GL3.GL_UNSIGNED_INT, 0);

        }
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(0);
    }

    @Override
    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, IntBuffer.wrap(vertexbufferID));
        gl.glDeleteBuffers(1, IntBuffer.wrap(indexbufferID));
      
    }

    /**
     * Nacte shadery z textoveho souboru
     * @param gl gl kontext
     * @param vertexShaderPath cesta k vertexshaderu
     * @param fragmentShaderPath cesta k fragment shaderu
     * @param geometryShaderPath cesta k geometry shaderu
     * @return id shaderu
     */
    public static int loadShaders(GL3 gl, String vertexShaderPath, String fragmentShaderPath, String geometryShaderPath) {

        // Create the shaders
        int VertexShaderID = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        int FragmentShaderID = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        int GeometryShaderID = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);

        // Read the Vertex Shader code from the file
        String[] vertexShader = new String[]{readFile(vertexShaderPath)};

        // Read the Fragment Shader code from the file    
        String[] fragmentShader = new String[]{readFile(fragmentShaderPath)};

        gl.glShaderSource(VertexShaderID, 1, vertexShader, null, 0);
        gl.glCompileShader(VertexShaderID);

        gl.glShaderSource(FragmentShaderID, 1, fragmentShader, null, 0);
        gl.glCompileShader(FragmentShaderID);

        // Link      
        int ProgramID = gl.glCreateProgram();
        gl.glAttachShader(ProgramID, VertexShaderID);
        gl.glAttachShader(ProgramID, FragmentShaderID);

        //Read the Geometry shader cofe from the file
        String[] geometryShader = null;
        if (geometryShaderPath != null) {
            geometryShader = new String[]{readFile(geometryShaderPath)};

            gl.glShaderSource(GeometryShaderID, 1, geometryShader, null, 0);
            gl.glCompileShader(GeometryShaderID);

            gl.glAttachShader(ProgramID, GeometryShaderID);
        }

        gl.glLinkProgram(ProgramID);

        gl.glDeleteShader(VertexShaderID);
        gl.glDeleteShader(FragmentShaderID);

        if (geometryShader != null) {
            gl.glDeleteShader(GeometryShaderID);
        }

        return ProgramID;
    }

    /**
     * Vrati string nacteneho souboru
     * @param path
     * @return 
     */
    private static String readFile(String path) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line = null;
            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            r.close();
        } catch (IOException e) {
            System.out.println("Couldnt not find shader.");
        }

        return sb.toString();
    }

}
