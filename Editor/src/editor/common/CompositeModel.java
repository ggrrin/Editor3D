/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstraktni trida pro tvorbu slozenych modelu
 * @author ggrrin_
 * @param <T> typ submodelu
 */
public abstract class CompositeModel<T extends Drawable> extends Drawable {

    private boolean drawBase = false;

    protected Matrix4 modelMatrix = new Matrix4();
    protected ArrayList<T> submodels = new ArrayList<>();

    private ArrayDeque<T> initQueue = new ArrayDeque<>();
    private ArrayDeque<T> disposeQueue = new ArrayDeque<>();

    /**
     * Inicializuje potrebne komponenty pro slozeny modely
     * @param m transformaci matice celeho modelu
     * @param drawBase pouzit i standartni vykreslovani zdedene s Drawable ci ne
     */
    public CompositeModel(Matrix4 m, boolean drawBase) {
        modelMatrix = m;
        this.drawBase = drawBase;
    }

    /**
     * Prida submodel do toho modelu
     * @param m submodel
     */
    public void addElement(T m) {

        synchronized (submodels) {
            submodels.add(m);
            if (initialized) {
                initLater(m);
            }
        }
    }

    /**
     * Odeber submodel z toho modelu
     * @param m submodel
     */
    public void removeElement(Object m) {
        synchronized (submodels) {
            submodels.remove(m);
            if (!disposed) {
                disposeLater((T) m);
            }
        }
    }

    @Override
    public void setWireColor(Vector3 v) {
        for (T t : submodels) {
            t.setWireColor(v);
        }
    }

    private boolean initialized = false;
    private boolean disposed = false;

    int programId;

    @Override
    public void init(GL3 gl, int programId) {
        if (drawBase) {
            super.init(gl, programId);
        }

        this.programId = programId;
        synchronized (submodels) {
            for (T c : submodels) {
                c.init(gl, programId);
            }
            initialized = true;
        }
    }

    @Override
    public void update(GL3 gl) {
        if (drawBase) {
            super.update(gl);
        }

        synchronized (submodels) {
            while (!initQueue.isEmpty()) {
                initQueue.pop().init(gl, programId);
            }
            while (!disposeQueue.isEmpty()) {
                disposeQueue.pop().dispose(gl);
            }
        }
    }

    @Override
    public void draw(GL3 gl, Matrix4 mvp) {

        Matrix4 m = new Matrix4();
        m.multMatrix(mvp);
        m.multMatrix(modelMatrix);

        synchronized (submodels) {
            for (T c : submodels) {
                c.draw(gl, m);
            }
        }

        if (drawBase) {
            super.draw(gl, mvp);
        }
    }

    @Override
    public void dispose(GL3 gl) {
        if (drawBase) {
            super.dispose(gl);
        }

        synchronized (submodels) {
            for (T c : submodels) {
                c.dispose(gl);
            }

            disposed = true;
        }
    }

    @Override
    public Matrix4 getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(Matrix4 m) {
        modelMatrix = m;
    }

    /**
     * Prida pozadavek pro inicializaci do fronty vykonavajiciho vlakna
     * @param m  objekt k inicializaci
     */
    private void initLater(T m) {
        initQueue.addLast(m);
    }

    /**
     * Prida pozadavek pro disposnuti objektu do fronty vykonavajicho vlakna
     * @param m  objekt k disposnuti
     */
    private void disposeLater(T m) {
        disposeQueue.addLast(m);
    }

    /**
     * Vrati seznamu submodelu
     * @return seznam sub modelu
     */
    public List<T> getSubModels() {
        return submodels;
    }
}
