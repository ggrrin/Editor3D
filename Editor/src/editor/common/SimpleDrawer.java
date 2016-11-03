/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.Matrix4;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Vykreslovaci komponenta, schopna vykreslit jednoduche veci jako je vektor,
 * usecka bod.
 *
 * @author ggrrin_
 */
public class SimpleDrawer implements IRenderComponent {

    /**
     * Inicializuje drawer
     *
     * @param defaultColor defaultni barva car
     * @param lineWidth sirka cat
     * @param pointSize zakladni velikost bodu
     */
    public SimpleDrawer(Vector3 defaultColor, float lineWidth, float pointSize) {
        this.defaultColor = defaultColor;
        this.lineWidth = lineWidth;
        defaultPointSize = pointSize;
    }

    private Matrix4 meshMatrix = new Matrix4();

    private boolean drawing = true;
    private Vector3 defaultColor;
    private float lineWidth;
    private float defaultPointSize = 1;

    //ids of copied buffer in graphic card
    private int[] vertexBufferID = new int[1];
    private int[] colorBufferID = new int[1];

    //variables sent to shaders
    private int programShaderID;
    private int mvpMatrixShaderID;

    private ArrayList<Vector3> vertexBuffer = new ArrayList<>();
    private ArrayList<Vector3> colorBuffer = new ArrayList<>();

    /**
     * Nastavi transformaci matici pro drawer
     *
     * @param m transformacni matice
     */
    public void setMatrix(Matrix4 m) {
        meshMatrix = m;
    }

    /**
     * Vrati soucasnou transformacni matici draweru
     *
     * @return tranformacni matice
     */
    public Matrix4 getMatrix() {
        return meshMatrix;
    }

    @Override
    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, IntBuffer.wrap(vertexBufferID));
        gl.glDeleteBuffers(1, IntBuffer.wrap(colorBufferID));
        gl.glDeleteProgram(programShaderID);
    }

    /**
     * Prida vektor k vykreslovani (Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     *
     * @param position pozice vektoru
     * @param v smaotny vektor
     */
    public void addVector(Vector3 position, Vector3 v) {
        addVector(position, v, defaultColor);
    }

    /**
     * Prida vektor k vykreslovani (Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     *
     * @param position pozice vektoru
     * @param v smaotny vektor
     * @param color barva vektoru
     */
    public void addVector(Vector3 position, Vector3 v, Vector3 color) {

        vertexBuffer.add(position);
        vertexBuffer.add(position.plus(v));

        colorBuffer.add(color);
        colorBuffer.add(color);
    }

    /**
     * Prida usecku k vykreslovani(Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     *
     *
     * @param p1 prvni bod usecky
     * @param p2 druhy bod usecky
     */
    public void addSegment(Vector3 p1, Vector3 p2) {
        addSegment(p1, p2, defaultColor);
    }

    /**
     * Prida usecku k vykreslovani(Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     *
     *
     * @param p1 prvni bod usecky
     * @param p2 druhy bod usecky     * 
     * @param color barva
     */
    public void addSegment(Vector3 p1, Vector3 p2, Vector3 color) {
        addVector(p1, p2.sub(p1), color);
    }

    /**
     * Prida bod k vykreslovani(Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     *
     *
     * @param point bod
     */
    public void addPoint(Vector3 point) {
        addPoint(point, defaultColor, defaultPointSize);

    }

    /**
     * Prida bod k vykreslovani(Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     *   
     * @param point bod
     * @param color barva
     * @param pointSize velikost bodu
     */
    public void addPoint(Vector3 point, Vector3 color, float pointSize) {
        float half = 0.5f * pointSize;
        addVector(point.sub(new Vector3(half, 0, 0)), new Vector3(pointSize, 0, 0), color);
        addVector(point.sub(new Vector3(0, half, 0)), new Vector3(0, pointSize, 0), color);
        addVector(point.sub(new Vector3(0, 0, half)), new Vector3(0, 0, pointSize), color);
    }

    /**
     * Prida k vykreslovani krabici (Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     * @param b  krabice
     */
    public void addBbox(BoundingBox b) {
        addBbox(b, defaultColor);
    }

        /**
     * Prida k vykreslovani krabici (Po volani teto metody je treba zavolat
     * updateBuffers k prenosu dat do GPU)
     * @param bb  krabice
     * @param BBoxColor barva
     */
    public void addBbox(BoundingBox bb, Vector3 BBoxColor) {

        Vector3 m = bb.getMin();
        Vector3 n = bb.getMax();

        Vector3 a = new Vector3(n.X - m.X, 0, 0);
        Vector3 b = new Vector3(0, n.Y - m.Y, 0);
        Vector3 c = new Vector3(0, 0, n.Z - m.Z);

        addSegment(m, m.plus(a), BBoxColor);
        addSegment(m, m.plus(a), BBoxColor);
        addSegment(m, m.plus(b), BBoxColor);
        addSegment(m, m.plus(c), BBoxColor);
        addSegment(n, n.sub(a), BBoxColor);
        addSegment(n, n.sub(b), BBoxColor);
        addSegment(n, n.sub(c), BBoxColor);
        addSegment(m.plus(b), m.plus(b.plus(a)), BBoxColor);
        addSegment(m.plus(b), m.plus(b.plus(c)), BBoxColor);
        addSegment(n.sub(b), n.sub(b.plus(a)), BBoxColor);
        addSegment(n.sub(b), n.sub(b.plus(c)), BBoxColor);
        addSegment(m.plus(a), m.plus(a.plus(b)), BBoxColor);
        addSegment(m.plus(c), m.plus(c.plus(b)), BBoxColor);
    }

    /**
     * Vymaze vsechna dosud pridana primitiva
     */
    public void clear() {
        vertexBuffer.clear();
        colorBuffer.clear();
        lastCapacity = lastSize = 0;
    }

    
    @Override
    public void init(GL3 gl, int porgramId) {

        programShaderID = Drawable.loadShaders(gl, "line.vs", "line.fs", null);
        mvpMatrixShaderID = gl.glGetUniformLocation(programShaderID, "MVP");


        gl.glGenBuffers(1, IntBuffer.wrap(vertexBufferID));
        gl.glGenBuffers(1, IntBuffer.wrap(colorBufferID));
     
    }

    int lastCapacity = 0;
    int lastSize = 0;

    /**
     * Nahraje data do GPU
     * @param gl  gl context
     */
    public void updateBuffers(GL3 gl) {
        synchronized (vertexBuffer) {

            float[] verts = new float[vertexBuffer.size() * 3];
            int i = 0;
            for (Vector3 v : vertexBuffer) {
                verts[i++] = v.X;
                verts[i++] = v.Y;
                verts[i++] = v.Z;
            }

            float[] cols = new float[colorBuffer.size() * 3];
            i = 0;
            for (Vector3 v : colorBuffer) {
                cols[i++] = v.X;
                cols[i++] = v.Y;
                cols[i++] = v.Z;
            }

            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufferID[0]);
            gl.glBufferData(GL4.GL_ARRAY_BUFFER, verts.length * 4, null, GL4.GL_DYNAMIC_DRAW);
            gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, verts.length * 4, FloatBuffer.wrap(verts));

            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, colorBufferID[0]);
            gl.glBufferData(GL4.GL_ARRAY_BUFFER, cols.length * 4, null, GL4.GL_DYNAMIC_DRAW);
            gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, cols.length * 4, FloatBuffer.wrap(cols));
        }
    }

    /**
     * Vykresli primitva
     * @param gl gl context
     * @param mvp  trasformacni matice
     */
    @Override
    public void draw(GL3 gl, Matrix4 mvp) {
        if (!drawing) {
            return;
        }

        Matrix4 m = new Matrix4();
        m.multMatrix(mvp);
        m.multMatrix(meshMatrix);

        FloatBuffer prevWidth = FloatBuffer.allocate(1);
        gl.glGetFloatv(GL4.GL_LINE_WIDTH, prevWidth);
        gl.glLineWidth(lineWidth);
        ////////////////////////////////////////////////////////////////////////

        gl.glUseProgram(programShaderID);

        gl.glUniformMatrix4fv(mvpMatrixShaderID, 1, false, FloatBuffer.wrap(m.getMatrix()));

        {
            gl.glEnableVertexAttribArray(0);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vertexBufferID[0]);
            gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);

            gl.glEnableVertexAttribArray(1);
            gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, colorBufferID[0]);
            gl.glVertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 0, 0);

            gl.glDrawArrays(GL4.GL_LINES, 0, vertexBuffer.size());
            gl.glDisableVertexAttribArray(0);
            gl.glDisableVertexAttribArray(1);
        }

        ////////////////////////////////////////////////////////////////////////
        gl.glLineWidth(prevWidth.get(0));

    }

}
