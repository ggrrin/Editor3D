/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bodyEditor;

import Polyhedron.Vector3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import editor.common.CompositeModel;
import editor.common.InteractiveGLJPanel;
import editor.common.Rotator;
import editor.convexEditor.ConvexModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Trida reprezentujici model objektu. Zaroven pouzita pro vykreslovani
 * @author ggrrin_
 */
public class BodyModel extends CompositeModel<ConvexModel> {

    /**
     * Textovy identifikator objektu
     */
    public String name = "noname";

    
    /**
     * inicializuje model s dannou matici m a pod dannym jmenem name
     * @param m matice cleho objektu
     * @param name textovy identifikator
     */
    public BodyModel(Matrix4 m, String name) {
        super(m, false);
        this.name = name;
    }

    int programId;

    @Override
    public void init(GL3 gl, int programId) {
        super.init(gl, programId);

    }

    @Override
    public void update(GL3 gl) {
        super.update(gl);
    }

    @Override
    public void draw(GL3 gl, Matrix4 mvp) {
        super.draw(gl, mvp);
    }

    @Override
    public void restartBuffers(GL3 gl) {
    }

    @Override
    public void dispose(GL3 gl) {
        super.dispose(gl);
    }

    @Override
    public List<Integer> getIndexBuffer() {
        ArrayList<Integer> ind = new ArrayList<Integer>();
        synchronized (submodels) {
            for (ConvexModel c : submodels) {
                for (int l : c.getIndexBuffer()) {
                    ind.add(l);
                }
            }
        }
        return ind;
    }

    @Override
    public List<Vector3> getVertexBuffer() {

        ArrayList<Vector3> ind = new ArrayList<>();
        synchronized (submodels) {
            for (ConvexModel c : submodels) {
                for (Vector3 l : c.getVertexBuffer()) {
                    ind.add(l);
                }
            }
        }
        return ind;
    }

    
    /**
     * urcuje barvu objektu
     * @return barva objektu nebo null
     */
    @Override
    public Vector3 getColor() {
        return null;
    }

    private Vector3 centeroidCache = null;

    /**
     * Vrati teziste ve svetovych souradnicich
     * @return dane teziste
     */
    public Vector3 getCenteroid() {
        if (centeroidCache == null) {
            Vector3 centeroid = new Vector3(0, 0, 0);

            for (ConvexModel m : submodels) {
                centeroid = centeroid.plus(m.getCenteroid());
            }

            centeroidCache = centeroid.ms(1f / submodels.size());
        }

        return InteractiveGLJPanel.mulMatrix(centeroidCache, modelMatrix);
    }

    /**
     * Nacte model ze souboru file
     * @param file soubor model
     * @return Objekt modleu
     * @throws IOException chyba ve cteni souboru
     */
    public static BodyModel load(File file) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return load(r);
        }
    }

    /**
     * Nacte model ze vstupniho readeru
     * @param r reader
     * @return Model
     * @throws IOException chyba pri cteni souboru 
     */
    public static BodyModel load(BufferedReader r) throws IOException {
        //name
        String name = r.readLine();

        //matrix
        Matrix4 mat = new Matrix4();
        float[] mf = mat.getMatrix();

        String[] floats = r.readLine().split(" ");
        for (int i = 0; i < 16; i++) {
            mf[i] = Float.parseFloat(floats[i]);
        }

        BodyModel m = new BodyModel(mat, name);

        //load submodels
        int subCount = Integer.parseInt(r.readLine());
        for (int i = 0; i < subCount; i++) {
            m.addElement(ConvexModel.load(r));
        }
        //Vector3 t = m.getCenteroid();
        //m.getModelMatrix().translate(-t.X, -t.Y, -t.Z);//WTF
        return m;
    }

    /**
     * Ulozi tento model do souboru file
     * @param file soubor pro ulozeni
     * @throws IOException chyba pri zapisu do souboru
     */
    public void export(File file) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            name = file.getName();
            export(w);
        }
    }

    /**
     * centrovat objekt, posunout jeho teziste do pocatku souradnic
     */
    public boolean normalize = true;
    
    
    /**
     * Ulozi tento model pomoci writeru w
     * @param w writer
     * @throws IOException chyba pri zapisu 
     */
    public void export(BufferedWriter w) throws IOException {
        //name
        w.write(name + "\n");

        //////////        
        Matrix4 clone = Rotator.clone(modelMatrix);

        if (normalize) {
            Vector3 t = getCenteroid().ms(-1);
            clone.loadIdentity();
            clone.translate(t.X, t.Y, t.Z);
        }

        //matrix
        for (float f : clone.getMatrix()) {
            w.write(f + " ");
        }
        w.write("\n");

        //submodelsCount
        w.write(submodels.size() + "\n");

        //submodels
        for (ConvexModel c : submodels) {
            c.export(w);
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
