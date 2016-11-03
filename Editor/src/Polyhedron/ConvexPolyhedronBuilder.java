package Polyhedron;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 * Trida reprezentujici algoritmus hledani konvexniho polyhedrony
 *
 * @author ggrrin_
 */
public class ConvexPolyhedronBuilder {

    /**
     * model s kterym algortmus pracuje
     */
    public Model model;
    TetrahedronBuilder tetrahedron;
    BipartiteGraph<OrientatedPlane, Vector3> graph;

    public Model backUp;

    /**
     * inicializuje tridu kolekci vrcholu
     *
     * @param vIn kolekce vrcholu na vstup
     * @param randomized pouzit randomizovanou variantu
     */
    public ConvexPolyhedronBuilder(Collection<Vector3> vIn, boolean randomized) {
        ArrayList<Vector3> v = new ArrayList<>();
        for (Vector3 x : vIn) {
            v.add(x/*.Clone()*/);
        }

        if (v.size() > 1)//remove idetical vertices
        {
            v.sort(null);

            ArrayList<Vector3> uniq = new ArrayList<>(v.size());
            Vector3 prev = v.get(0);
            uniq.add(prev);
            for (int i = 1; i < v.size(); i++) {
                if (v.get(i) != prev) {
                    uniq.add(v.get(i));
                    prev = v.get(i);
                }
            }

            v = uniq;
        }

        if (randomized) {
            Random rnd = new Random();
            v.sort((t, t1) -> rnd.nextInt());
        }

        intiBackUp(v);

        tetrahedron = new TetrahedronBuilder(model = new Model(v));
        graph = new BipartiteGraph<>(model.Planes, v);
    }

    /**
     * Provede algoritmu
     *
     * @return true polyhdron lze vytvorit, false naopak
     */
    public boolean Calculate() {
        try {
            return Calculate1();
        } catch (Exception e) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Calendar cal = Calendar.getInstance();

            try {
                backUp.export("Input" + dateFormat.format(cal.getTime()) + ".txt");
                System.out.println("Error reported");
            } catch (IOException q) {
                System.out.println("Unable to report error -> " + q.getMessage());
            }

            throw e;
        }

    }

    /**
     * Provede algoritmu
     *
     * @return true polyhdron lze vytvorit, false naopak
     */
    private boolean Calculate1() {
        int initialVertexIndex;
        try {
            initialVertexIndex = tetrahedron.Build();
        } catch (UnsupportedOperationException e) {
            return false;
        }

        InitializeBipartiteGraph();

        for (int vertexIndex = initialVertexIndex; vertexIndex < model.Vertices.size(); vertexIndex++) {
//            System.out.println("vertex " + vertexIndex);
            ArrayList<OrientatedPlane> planesToRemove = new ArrayList<>();
            //sedma iterace
            Edge[] horizontEdges = FindHorizont(vertexIndex, planesToRemove);

            if (horizontEdges.length == 0) {
                continue;
            }

            Edge[] newEdges = GetNewEdges(horizontEdges, vertexIndex);//Error
            OrientatedPlane[] newPlanes = GetNewPlanes(horizontEdges, newEdges);

            model.Edges.addAll(Arrays.asList(newEdges));
            model.Planes.addAll(Arrays.asList(newPlanes));

            //////////////////////////////////
//            System.out.println("horizontEdges");
//            for (Edge e : horizontEdges) {
//                System.out.println(e);
//            }
//            System.out.println("newEdges");
//            for (Edge e : newEdges) {
//                System.out.println(e);
//            }
//            System.out.println("newPlanes");
//            for (OrientatedPlane e : newPlanes) {
//                System.out.println(e);
//            }
//            System.out.println("planesToRemove");
//            for (OrientatedPlane e : planesToRemove) {
//                System.out.println(e);
//            }
            ////////////////////////////////
            UpdateGraph(horizontEdges, newPlanes);

            RemoveOldPlanes(planesToRemove);

            for (OrientatedPlane k : newPlanes) {
                k.registerEdgesConnection();
            }
        }

        model.finish();
        return true;
    }

    /**
     * Oznaci steny planes jako oznacene v modelu
     * @param planes steny k odebrani
     */
    private void RemoveOldPlanes(ArrayList<OrientatedPlane> planes) {
        for (OrientatedPlane i : planes) {
            i.Remove();
        }
    }

    
    /**
     * Aktualizuje graf viditelnosti mezi vrcholy a stenami, prida nove steny
     * @param horizontEdges
     * @param newPlanes 
     */
    private void UpdateGraph(Edge[] horizontEdges, OrientatedPlane[] newPlanes) {
        // najit 2 steny ktere maji hrozont edge
        //otestovat ktere vrcholy vidi novou stenu z mnoziny vrcholu ktere vidi puvodni 2 steny 

        for (int i = 0; i < horizontEdges.length; i++) {
            ArrayList<KeyPairValue<OrientatedPlane, Integer>> commits = new ArrayList<>();

            for (OrientatedPlane plane : horizontEdges[i]) {
                Iterator<KeyPairValue<Integer, Vector3>> verticies0 = graph.GetNeighboursOfL(plane);

                while (verticies0.hasNext()) {
                    KeyPairValue<Integer, Vector3> vertex = verticies0.next();
                    if (newPlanes[i].intersect(vertex.value)) {
                        commits.add(new KeyPairValue<OrientatedPlane, Integer>(newPlanes[i], vertex.key));
                    }
                }
            }

            for (KeyPairValue<OrientatedPlane, Integer> l : commits) {
                graph.Connect(l.key, l.value);
            }
        }
    }

    /**
     * Vytvori nove steny vznikle pridanim vrcholu
     * @param horizontEdges  hrany na horizontu viditelnosti steny
     * @param newEdges novehrany rovin ve vzestupnem poradi
     * @return seznam novych sten
     */
    private OrientatedPlane[] GetNewPlanes(Edge[] horizontEdges, Edge[] newEdges) {
        OrientatedPlane[] newPlanes = new OrientatedPlane[horizontEdges.length];

        newPlanes[0] = new OrientatedPlane(horizontEdges[0], newEdges[newPlanes.length - 1], newEdges[0], false);

        for (int j = 1; j < horizontEdges.length; j++) {
            newPlanes[j] = new OrientatedPlane(horizontEdges[j], newEdges[j - 1], newEdges[j], false);
        }
        return newPlanes;
    }

    /**
     * Vytvori nove hrany mezi horizontem a danem novym vrcholem
     * @param horizontEdges hrany na horiznotuy
     * @param vertexIndex index noveho vrcholu
     * @return nove hrany
     */
    private Edge[] GetNewEdges(Edge[] horizontEdges, int vertexIndex) {
        Edge[] newEdges = new Edge[horizontEdges.length];

        for (int j = 0; j < horizontEdges.length - 1; j++) {
            newEdges[j] = new Edge(model.Vertices, horizontEdges[j].getVertexIndex(horizontEdges[j + 1]), vertexIndex);//Error
        }

        newEdges[newEdges.length - 1] = new Edge(model.Vertices, horizontEdges[0].getVertexIndex(horizontEdges[horizontEdges.length - 1]), vertexIndex);
        return newEdges;
    }

    
    /**
     * Nalezne hrany ktere jsou na horizntu, hrany za kterymi uz nejsou videt steny
     * @param vertexIndex index vrcholu z ktereho se divame
     * @param planesToRemove vystupni parametr sten, ktere bude treba ostranit po pridani vrcholu
     * @return seznam hran na horizontu
     */
    private Edge[] FindHorizont(int vertexIndex, ArrayList<OrientatedPlane> planesToRemove) {

        Iterator<OrientatedPlane> planes = graph.GetNeighboursOfR(vertexIndex);

        ArrayList<Edge> edges = new ArrayList<>();
        Edge[] ts = new Edge[0];//EDITED
        if (!planes.hasNext()) {
            return edges.toArray(ts);
        }

        int nonHorizontCount = 0;

        //pruchod sten ktere jsou z bodu videt, hrany na horizontu jsou ty, ktere projdeme jen jednou
        do {
            OrientatedPlane p = planes.next();
            if (p.getIndex() != -1) {
                for (Edge e : p) {
                    if (e.quantities++ == 0) {
                        edges.add(e);
                    } else if (e.quantities == 2)//pokud se zvysil pocet z 1->2 neni na horizontu
                    {
                        nonHorizontCount++;
                    }
                }
            }
        } while (planes.hasNext());

        if (edges.isEmpty()) {
            return edges.toArray(ts);
        }

        //ze vsech viditelnych hran vyber pouze tech na horizntu
        Edge[] horizontEdges = new Edge[edges.size() - nonHorizontCount];

        int index = 0;
        for (Edge e : edges) {
            if (e.quantities == 1) {
                horizontEdges[index++] = e;
            } else {
                e.quantities = -1; //mark that edge should be removed
            }
        }

        Edge[] res = FinalHoriznot(horizontEdges);

        planes = graph.GetNeighboursOfR(vertexIndex);
        while (planes.hasNext()) {
            OrientatedPlane pl = planes.next();
            if (pl.getIndex() != -1) {
                planesToRemove.add(pl);
            }
        }

        return res;
    }

    
    /**
     * Usoprada hrany v horizntu tak aby sly postupne
     * @param horizontEdges hrany na horizontu
     * @return setridene hrany
     */
    private Edge[] FinalHoriznot(Edge[] horizontEdges) {
        Edge[] sortedHoriznt = new Edge[horizontEdges.length];
        Edge last;

        sortedHoriznt[0] = (last = horizontEdges[0]);//Error 
        int connectVertexIndex = last.getCLWNextVertex();
        last.quantities = 0;

        for (int i = 1; i < horizontEdges.length; i++) {
            for (int j = 0; j < horizontEdges.length; j++) {
                if (horizontEdges[j].quantities != 0 && (horizontEdges[j].getVertex1Ptr() == connectVertexIndex || horizontEdges[j].getVertex2Ptr() == connectVertexIndex)) {
                    sortedHoriznt[i] = last = horizontEdges[j];
                    break;
                }
            }

            last.quantities = 0;
            connectVertexIndex = last.getVertex1Ptr() == connectVertexIndex ? last.getVertex2Ptr() : last.getVertex1Ptr();
        }

        return sortedHoriznt;
    }

    /**
     * Pocatecni inicializace grafu viditelnosti
     */
    private void InitializeBipartiteGraph() {
        for (int i = 0; i < model.Vertices.size(); i++) {
            for (OrientatedPlane plane : model.Planes) {
                if (plane.intersect(model.Vertices.get(i))) {
                    graph.Connect(plane, i);
                }
            }
        }
    }

    
    private void intiBackUp(ArrayList<Vector3> v) {
        ArrayList<Vector3> newv = new ArrayList<>();
        for (Vector3 q : v) {
            newv.add(q.Clone());
        }

        backUp = new Model(newv);
    }
}
