/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.Drawable;
import editor.common.IModel;
import editor.common.InteractiveGLJPanel;
import editor.common.SimpleDrawer;
import editor.common.TempModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Trida reprezentujici model konvexniho objektu. A zaroven zodpovedna za vykreslovani.
 * @author ggrrin_
 */
public class ConvexModel extends Drawable {

    public String name = "unnamed";

    private ArrayList<Vector3> vertsx = new ArrayList<>();
    private ArrayList<Integer> ind = new ArrayList<>();

    SimpleDrawer sd;

    private Matrix4 modelMatrix;

    /**
     * Inicializuje model pomoci jineho modelu m a matice m1
     * @param m model reprezentujici tento model
     * @param m1 matice modelu
     */
    public ConvexModel(IModel m , Matrix4 m1)
    {
        this(m,m1, "noname");
    }
    
    /**
     * Inicializuje model pomoci jineho modelu m a matice m1, a nastavi identifikator
     * @param model vzorovy model
     * @param m matice
     * @param name identifikator
     */
    public ConvexModel(IModel model, Matrix4 m, String name) {
        this.name = name;
        vertsx = new ArrayList<>(model.getVertexBuffer());
        
        List<Integer> iii = model.getIndexBuffer();
        
        ind = iii == null ? null : new ArrayList<>(iii);

        if (ind == null) {
            ind = new ArrayList<>();

            sd = new SimpleDrawer(getColor(), 3, 1f);
            for (Vector3 w : vertsx) {
                sd.addPoint(w);
            }
        }

        this.modelMatrix = m;
    }

    /**
     * Nastavi matici modelu na m
     * @param m nova matice modelu
     */
    public void setModelMatrix(Matrix4 m) {
        modelMatrix = m;
    }

    private Vector3 centeroidCache = null;

    /**
     * Vypocita teziste daneho modelu ve svetovych souradnicich
     * @return teziste
     */
    public Vector3 getCenteroid() {
        if (centeroidCache == null) {
            Vector3 centeroid = new Vector3(0, 0, 0);

            for (Vector3 v : vertsx) {
                centeroid = centeroid.plus(v);
            }

            centeroidCache = centeroid.ms(1f / vertsx.size());
        }

        return InteractiveGLJPanel.mulMatrix(centeroidCache, modelMatrix);
    }

    /**
     * Vrati teziste modelu transformovane dannou matici 
     * @param v transformaci matice
     * @return transformovany bod teziste
     */
    public Vector3 getCenteroid(Matrix4 v) {
        return InteractiveGLJPanel.mulMatrix(getCenteroid(), v);
    }

    @Override
    public void init(GL3 gl, int programId) {
        super.init(gl, programId);

        if (sd != null) {
            sd.init(gl, programId);
            sd.updateBuffers(gl);
        }
    }

    @Override
    public void update(GL3 gl) {
        if (sd != null) {
            sd.update(gl);
        } else {
            super.update(gl);
        }
    }

    @Override
    public void draw(GL3 gl, Matrix4 mvp) {
        if (sd != null) {
            Matrix4 m = new Matrix4();
            m.multMatrix(mvp);
            synchronized (modelMatrix) {
                m.multMatrix(modelMatrix);
            }
            sd.draw(gl, m);
        } else {
            super.draw(gl, mvp);
        }
    }

    @Override
    public void dispose(GL3 gl) {
        if (sd != null) {
            sd.dispose(gl);
        }

        super.dispose(gl);
    }

    @Override
    public List<Integer> getIndexBuffer() {
        return ind;
    }

    @Override
    public List<Vector3> getVertexBuffer() {
        return vertsx;
    }

    @Override
    public Vector3 getColor() {
        return new Vector3(1, 0, 0);
    }

    @Override
    public Matrix4 getModelMatrix() {
        return modelMatrix;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Nacte ze souboru dany konvexni model
     * @param file soubor s ulozenym modelem
     * @return model reprezentovany souborem 
     * @throws IOException chyba pri cteni souboru
     */
    public static ConvexModel load(File file) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return load(r);
        }
    }

    /**
     * Nacte ze readru danny konvexni model
     * @param r reader
     * @return vysledny model nacteny ze readeru
     * @throws IOException  chyba pri cteni souboru
     */
    public static ConvexModel load(BufferedReader r) throws IOException {
        //read name
        String name = r.readLine();

        //reand matrix
        Matrix4 mat = new Matrix4();
        float[] mf = mat.getMatrix();

        String[] floats = r.readLine().split(" ");
        for (int i = 0; i < 16; i++) {
            mf[i] = Float.parseFloat(floats[i]);
        }

        //vertex count
        int vbCount = Integer.parseInt(r.readLine());
        //vertices
        Vector3[] vb = new Vector3[vbCount];
        for (int i = 0; i < vbCount; i++) {
            String[] line = r.readLine().split(" ");
            vb[i] = new Vector3(
                    Float.parseFloat(line[0]),
                    Float.parseFloat(line[1]),
                    Float.parseFloat(line[2]));
        }
        
        //index count
        int ibCount = Integer.parseInt(r.readLine());
        //vertices
        Integer[] ib = new Integer[ibCount];
        for (int i = 0; i < ibCount; i++) {            
            ib[i] = Integer.parseInt(r.readLine());                 
        }
        
        return new ConvexModel(new TempModel(Arrays.asList(ib), Arrays.asList(vb)), mat, name);
    }

    /**
     * Ulozeni tohoto modelu do souboru file
     * @param file soubor pro ulozni modelu
     * @throws IOException  chyba pri zapisu do souboru
     */
    public void export(File file) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            name = file.getName();
            export(w);
        }
    }

    /**
     * Ulozi soubor pomoci daneho writeru
     * @param w writer
     * @throws IOException chyba pri zapisu 
     */
    public void export(BufferedWriter w) throws IOException {
        //name
        w.write(name + "\n");

        //matrix
        for (float f : modelMatrix.getMatrix()) {
            w.write(f + " ");
        }
        w.write("\n");

        //vertices count
        w.write(vertsx.size() + "\n");
        //wertices
        for (Vector3 Vertice : vertsx) {
            w.write(Vertice.X + " ");
            w.write(Vertice.Y + " ");
            w.write(Vertice.Z + "\n");
        }

        //indices count
        w.write(ind.size() + "\n");

        //indices
        for (int i : ind) {
            w.write(i + "\n");
        }

    }
}
