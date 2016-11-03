/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.heighMapEditor;

import Polyhedron.Vector3;
import com.jogamp.opengl.math.Matrix4;
import editor.bodyEditor.BodyModel;
import editor.common.CompositeModel;
import java.awt.Point;
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
 * Model reprezentujici vyskovou mapu vcte vsech jejich submodelu
 * @author ggrrin_
 */
public class HeightMapModel extends CompositeModel<BodyModel> {

    private ArrayList<Vector3> vb = new ArrayList<>();
    private ArrayList<Integer> ib = new ArrayList<>();

    int width;
    int depth;

    /**
     * Inicializuje mapu dle dne vysky a hloubky, a transformacni matice
     * @param width vyska 
     * @param depth hloubka
     * @param model transformatcni matice cle mapy
     */
    public HeightMapModel(int width, int depth, Matrix4 model) {
        super(model, true);

        this.width = width;
        this.depth = depth;

        vb.ensureCapacity(width * depth);
        for (int i = 0; i < width * depth; i++) {
            int x = i % width;
            int z = i / width;

            vb.add(new Vector3(x - (width / 2f), 0, z - (depth / 2f)));
        }

        ib.ensureCapacity((width - 1) * (depth - 1) * 2);

        for (int i = 0; i < depth - 1; i++) {
            for (int j = 0; j < width - 1; j++) {
                //a-b
                //c-d
                int a = i * width + j;
                int b = a + 1;
                int c = (i + 1) * width + j;
                int d = c + 1;

                ib.add(a);
                ib.add(c);
                ib.add(d);

                ib.add(a);
                ib.add(d);
                ib.add(b);
            }
        }

    }
    


    /**
     * Vrati sousedy bodu dane indexy do indexbufferu v dannem polomeru 
     * @param index index bodu
     * @param radius polomer hledani
     * @return seznam sousedu
     */
    public List<Integer> getNeigbours(int index, int radius) {
        if (index >= ib.size()) {
            throw new IllegalArgumentException();
        }

        List<Integer> res = new ArrayList<>();
        Point x = new Point(index % width, index / width);

        for (int i = -radius; i < radius; i++) {
            for (int j = -radius; j < radius; j++) {
                if ((i * i + j * j) < (radius * radius)) {
                    Point curPoint = new Point((x.x + j), (x.y + i));
                    if (curPoint.x >= 0 && curPoint.y >= 0 && curPoint.x < width && curPoint.y < depth) {
                        res.add(width * curPoint.y + curPoint.x);
                    }
                }
            }
        }
        return res;
    }

    @Override
    public List<Integer> getIndexBuffer() {
        return ib;
    }

    @Override
    public List<Vector3> getVertexBuffer() {
        return vb;
    }

    @Override
    public Vector3 getColor() {
        return new Vector3(0, 1, 0);
    }

    /**
     * Vrati index do index bufferu na bod ktery je nekde priblizne uprstred mapy
     * @return 
     */
    int getOriginIndex() {
        return ib.size() / 12;
    }

    /**
     * Nacte mapu se vsemi jejimy subobjekty z souboru file
     * @param file soubor s mapou
     * @return model mapy 
     * @throws IOException chyba pri cteni souboru 
     */
    public static HeightMapModel load(File file) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return load(r);
        }
    }

    /**
     * Nacte mapu z daneho readeru
     * @param r reader
     * @return vrati objekt Mapy 
     * @throws IOException chyba pri cteni souboru
     */
    public static HeightMapModel load(BufferedReader r) throws IOException {
        int width = Integer.parseInt(r.readLine());
        int depth = Integer.parseInt(r.readLine());

        HeightMapModel m = new HeightMapModel(width, depth, new Matrix4());

        int vbCount = Integer.parseInt(r.readLine());

        for (int i = 0; i < vbCount; i++) {
            String[] line = r.readLine().split(" ");
            m.vb.get(i).X = Float.parseFloat(line[0]);
            m.vb.get(i).Y = Float.parseFloat(line[1]);
            m.vb.get(i).Z = Float.parseFloat(line[2]);
        }

        int ibCount = Integer.parseInt(r.readLine());

        for (int i = 0; i < ibCount; i++) {
            m.ib.set(i, Integer.parseInt(r.readLine()));
        }

        //load submodels
        int subCount = Integer.parseInt(r.readLine());
        for (int i = 0; i < subCount; i++) {
            m.addElement(BodyModel.load(r));
        }

        return m;
    }

    /**
     * Ulozi tento model do souboru file
     * @param file soubor kam ulozit mapu
     * @throws IOException  chyba pri zapisu do souboru
     */
    public void export(File file) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            export(w);
        }
    }

    /**
     * Ulozi tento model pomoci daneho writeru
     * @param w writer
     * @throws IOException chyba pri zapisu 
     */
    public void export(BufferedWriter w) throws IOException {
        w.write(width + "\n");
        w.write(depth + "\n");

        //vertices count
        w.write(vb.size() + "\n");
        //wertices
        for (Vector3 Vertice : vb) {
            w.write(Vertice.X + " ");
            w.write(Vertice.Y + " ");
            w.write(Vertice.Z + "\n");
        }

        //indices count
        w.write(ib.size() + "\n");

        //indices
        for (int i : ib) {
            w.write(i + "\n");
        }

        //submodelsCount
        w.write(submodels.size() + "\n");

        //submodels
        for (BodyModel c : submodels) {
            c.normalize = false;
            c.export(w);
        }
    }

}
