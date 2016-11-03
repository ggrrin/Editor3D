/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.Matrix4;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Trida reprezentujici rendere. Provadi zakladni inicializaci open gl
 * nastaveni projekctni  view matice
 * @author ggrrin_
 */
public abstract class Renderer implements GLEventListener {

    private Vector3 cameraPoint;
    private Vector3 targetPoint;

    /**
     * Vrati pod na ktery se diva kamera
     * @return vector pozice
     */
    public Vector3 getTargetPoint() {
        synchronized (targetPoint) {
            return targetPoint.Clone();
        }
    }

    /**
     * Vrati bod na kterem je parave kamera
     * @return vektor na pozici
     */
    public Vector3 getCameraPoint() {
         synchronized (targetPoint) {
            return cameraPoint.Clone();
        }
    }

    /**
     * Nastavi bod na ktery se ma presunout kamera
     * @param v  bod
     */
    public void setCameraPoint(Vector3 v) {
        cameraPoint.Y = Math.max(-3, Math.min(13, v.Y));
    }

    private Matrix4 lookAt;
    private Matrix4 view = new Matrix4();

    /**
     * blizsi rovina projekce
     */
    public float nearPlane = 0.01f;
    
    /**
     * vzdalena rovina projekce
     */
    public float farPlane = 100f;

    private int[] VertexArrayID = new int[1];

    private int programID;

    private Matrix4 mvp = new Matrix4();

    /**
     * Vrati matici projekce*view
     * @return dana matice
     */
    public Matrix4 getMvp() {
        return mvp;
    }

    public Renderer() {
        cameraPoint = new Vector3(10, 13, 15);
        targetPoint = new Vector3(0, 0, 0);

        lookAt = lookAt(cameraPoint, targetPoint, new Vector3(0, 1, 0));
    }

    /**
     * Vrati model ktery se prave vykresluje
     * @return model
     */
    public abstract Drawable getCurentModel();

    /**
     * Vyvolano pri inicializaci opengl
     * @param gl gl context
     * @param programShaderId globali shader 
     */
    public abstract void init(GL3 gl, int programShaderId);

    /**
     * Vyvolano ve smycce pred vykreslenim
     * @param gl gl context
     */
    public abstract void update(GL3 gl);

    
    /**
     * Vyvolani pri zmene vykreslovaciho platna
     * @param gl gl context
     * @param x pozice x
     * @param y pozice y
     * @param width sirka componenty
     * @param height vyska componenty
     */
    public void reshape(GL3 gl, int x, int y, int width, int height) {

    }

    /**
     * Vyvolano pri vykreslovani
     * @param gl gl context
     * @param mvp  matice projekce
     */
    public abstract void draw(GL3 gl, Matrix4 mvp);

    /**
     * Vyvolano pri disposovani opengl
     * @param gl gl context
     */
    public abstract void dispose(GL3 gl);

    private boolean initialized = false;
    private boolean disposed = false;

    private ArrayList<IRenderComponent> components = new ArrayList<>();

    /**
     * Registruje komponentu k vautomatickemu vykreslovani
     * @param c  komponenta
     */
    public void registerComponent(IRenderComponent c) {
        synchronized (components) {
            if (components.contains(c)) {
                throw new IllegalStateException();
            }

            components.add(c);

            if (initialized) {
                invokeLater((g) -> c.init(g, programID));
            }
        }
    }

    /**
     * vymaze komponentu z automatickeho vykresloavani
     * @param c  komponenta
     */
    public void unregisterComponent(IRenderComponent c) {
        synchronized (components) {
            if (!components.remove(c)) {
                throw new IllegalStateException();
            } else {
                if (!disposed) {
                    invokeLater((g) -> dispose(g));
                }
            }
        }
    }

    ArrayDeque<Consumer<GL3>> taskQueue = new ArrayDeque<>();

    /**
     * Vyvola danou rutinu na vykreslovacim vlakne
     * @param c cinost
     */
    public void invokeLater(Consumer<GL3> c) {
        synchronized (taskQueue) {
            taskQueue.push(c);
        }
    }

    /**
     * Inicializuje opengl
     * @param drawable  gldrawable
     */
    @Override
    public final void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        gl.glClearColor(0, 0.5f, 1, 0);
        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glDepthFunc(GL3.GL_LESS);
        //gl.glEnable(GL3.GL_CULL_FACE);
        //gl.glPolygonMode( GL3.GL_FRONT_AND_BACK, GL3.GL_LINE ); //doesnt work

        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenVertexArrays(1, IntBuffer.wrap(VertexArrayID));
        gl.glBindVertexArray(VertexArrayID[0]);
        programID = Drawable.loadShaders(gl, "wire.vs", "wire.fs", "wire.gs");

        init(gl, programID);

        synchronized (components) {
            for (IRenderComponent c : components) {
                c.init(gl, programID);
            }

            initialized = true;
        }
    }

    /**
     * Vykreslovani
     * @param drawable gldrawable
     */
    @Override
    public final void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        synchronized (taskQueue) {
            while (!taskQueue.isEmpty()) {
                taskQueue.pop().accept(gl);
            }
        }

        synchronized (components) {
            for (IRenderComponent c : components) {
                c.update(gl);

            }
        }

        update(gl);
        mvp.loadIdentity();
        mvp.multMatrix(view);
        lookAt = lookAt(cameraPoint, targetPoint, new Vector3(0, 1, 0));
        mvp.multMatrix(lookAt);

        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        synchronized (components) {
            for (IRenderComponent c : components) {

                c.draw(gl, mvp);
            }
        }

        draw(gl, mvp);
        drawable.swapBuffers();
    }

    /**
     * Zmena platna
     * @param drawable gldrawable
     * @param x pozice x
     * @param y pozice y
     * @param width sirka componenty
     * @param height vyska componenty
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        view.loadIdentity();
        view.makePerspective((float) Math.PI / 4.0f, width / (float) height, nearPlane, farPlane);

        reshape(drawable.getGL().getGL3(), x, y, width, height);

        synchronized (components) {
            for (IRenderComponent c : components) {

                c.reshape(drawable.getGL().getGL3(), x, y, width, height);
            }
        }
    }

    /**
     * Disposnuti opengl
     * @param drawable gldrawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        dispose(gl);

        gl.glDeleteVertexArrays(1, IntBuffer.wrap(VertexArrayID));
        gl.glDeleteProgram(programID);
        
        synchronized (components) {
            for (IRenderComponent c : components) {
                c.dispose(gl);
            }
            disposed = true;
        }

    }

    /**
     * Vyutvori lookAt matici
     * @param eye pozice kamery
     * @param center misto na ktere se kamera podiva
     * @param up up vektor kamery
     * @return look at matice
     */
    public static Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
        Matrix4 m = new Matrix4();

        Vector3 f, s, u;

        f = Vector3.normalize(center.sub(eye));
        s = Vector3.normalize(Vector3.Cross(f, up));
        u = Vector3.Cross(s, f);

        float[] res = m.getMatrix();

        res[0] = s.X;
        res[1] = s.Y;
        res[2] = s.Z;
        res[4] = u.X;
        res[5] = u.Y;
        res[6] = u.Z;
        res[8] = -f.X;
        res[9] = -f.Y;
        res[10] = -f.Z;

        res[3] = -Vector3.dot(s, eye);
        res[7] = -Vector3.dot(u, eye);
        res[11] = Vector3.dot(f, eye);

        m.transpose();
        return m;
    }
}
