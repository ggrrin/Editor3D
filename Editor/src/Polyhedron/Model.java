package Polyhedron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Trida reprezentujici uloziste pro vytvareny polyhdron
 * @author ggrrin_
 */
public class Model implements editor.common.IModel {

    /**
     * Vytvori prazdny model
     */
    public Model() {
        Vertices = new ArrayList<>();
        Planes = new CustomList<>();
        Edges = new ArrayList<>();
    }

    /**
     * Vytvori model a jako body pouzije v
     * @param v body modelu
     */
    public Model(ArrayList<Vector3> v) {
        Vertices = v;
        Planes = new CustomList<>();
        Edges = new ArrayList<>();
    }

    /**
     * Steny polyhdronu
     */
    public CustomList<OrientatedPlane> Planes;
    
    /**
     * Vrcholy polyhedronu
     */
    public ArrayList<Vector3> Vertices;
    
    /**
     * hrany polyhderonu
     */
    public ArrayList<Edge> Edges;
    
    /**
     * IndexBuffer polyhdronu
     */
    public ArrayList<Integer> Indices = new ArrayList<>();
    

    /**
     * Vymaze nepouzite steny, hrany, vrcholy a inicializuje vertex a index buffer
     */
    public void finish() {
        CustomList<OrientatedPlane> planes = new CustomList<>();
        for (OrientatedPlane p : Planes) {
            if (p.isAlive()) {
                planes.add(p);
            }
        }

        Planes = planes;

        ArrayList<Edge> edges = new ArrayList<>();
        for (Edge e : Edges) {
            if (e.isUsed()) {
                edges.add(e);
            }
        }
        Edges = edges;
        
        initBuffers();
    }
    
    /**
     * vetvor vertex a index buffer
     */
    private void initBuffers() {
        ArrayList<Vector3> newVerts = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        
        Integer[] projection = new Integer[Vertices.size()];

        int currentVertex = 0;

        for (Edge e : Edges) {
            if (projection[e.vertex1] == null) {
                projection[e.vertex1] = currentVertex;
                currentVertex++;
                newVerts.add(e.getVertex1());
            }

            if (projection[e.vertex2] == null) {
                projection[e.vertex2] = currentVertex;
                currentVertex++;
                newVerts.add(e.getVertex2());
            }
        }

        for (OrientatedPlane p : Planes) {
            Iterator<Integer> it = p.getVertexIndiceIterator();

            while (it.hasNext()) {
                int vIndex = (int) it.next();
                if (projection[vIndex] == null) {
                    throw new Error();
                } else {
                    indices.add((int) projection[vIndex]);
                }
            }
        }
        
        Vertices = newVerts;
        Indices = indices;
    }

    @Override
    public ArrayList<Vector3> getVertexBuffer() {
        return Vertices;
    }

    @Override
    public ArrayList<Integer> getIndexBuffer() {
        return Indices;
    }



    /**
     * Ulozi do soboru tento model
     * @param path misto kam ulozit
     * @throws IOException pri chybe souboru
     */
    public void export(String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)))) {

            w.write(Vertices.size() + "\n");

            for (Vector3 Vertice : Vertices) {
                w.write(Vertice.X + " ");
                w.write(Vertice.Y + " ");
                w.write(Vertice.Z + "\n");
            }
        } 
    }
    
    
    /**
     * Nacte ze soboru body ulozene v modelu
     * @param path dany soubor
     * @return body ulozene v modelu
     * @throws IOException pri chybe souboru
     */
     public static ArrayList<Vector3> readInputFile(String path) throws IOException {

        ArrayList<Vector3> data = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            int ccc = Integer.parseInt(r.readLine());

            data = new ArrayList<>(ccc);
            for (int i = 0; i < ccc; i++) {
                String line = r.readLine();
                String[] vector = line.trim().split(" +");
                if (vector.length != 3) {
                    return null;
                }

                data.add(new Vector3(
                        Float.parseFloat(vector[0]),
                        Float.parseFloat(vector[1]),
                        Float.parseFloat(vector[2])));
            }

            return data;
        } 

    }
 
}
