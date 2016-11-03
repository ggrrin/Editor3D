package Polyhedron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Trida reprezentujici hranu mezi dvemi body
 * @author ggrrin_
 */
class Edge implements Iterable<OrientatedPlane> {

    private List<Vector3> source;
    int vertex1, vertex2;

    /**
     * pomecna promena pro algoritmus, urcuje kolikrat je hrana v kroku alg. videna
     */
    public int quantities;

    /**
     * Index prvniho vrcholu hrany v modelu
     * @return prvni index
     */
    public int getVertex1Ptr() {
        return vertex1;
    }

    /**
     * Index druheho vrcholu hrny v modelu
     * @return druhy index
     */
    public int getVertex2Ptr() {
        return vertex2;
    }

    
    /**
     * Prvni vrchol hrany
     * @return prvni vrchol
     */
    public Vector3 getVertex1() {
        return source.get(vertex1);
    }

    
    /**
     * Druhy vrchol hrany
     * @return druhy vrchol
     */
    public Vector3 getVertex2() {
        return source.get(vertex2);
    }

    /**
     * Roviny v kterych je hrana pouzita
     */
    private ArrayList<OrientatedPlane> planes = new ArrayList<>(2);

    /**
     * Vrati zda je hrana pouzita tzn. zda je obsazena prave v e 2 rovinach
     * @return true je pouzita flase jinak
     */
    public boolean isUsed() {
        return planes.size() == 2;
    }

    /**
     * Ulozi ze hrana je pouzita v stene p
     * @param p stena pro ulozeni
     */
    public void useEdgeIn(OrientatedPlane p) {
        if (planes.size() >= 2 || planes.contains(p)) {
            throw new Error();
        } else {
            planes.add(p);
        }
    }

    
    /**
     * Oznaci hranu ze neni pouzita v stena p
     * @param p stena
     */
    public void removeEdgeFrom(OrientatedPlane p) {
        boolean res = planes.remove(p);
        assert res;
    }

    
    /**
     * Zkonstruuje hranu dle index vrcholu do vertexBufferu source
     * @param source vretex buffer
     * @param index1 index prvniho vrcholu
     * @param index2 index druheho vrcholu
     */
    public Edge(List<Vector3> source, int index1, int index2) {
        this.source = source;
        vertex1 = index1;
        vertex2 = index2;
    }

    /**
     * Vrati vrchol, ktery je pouzity v teto hrane a hrane e
     * vzdy hrany musi mit spolecny vrchol jinak se jedna o chybu
     * @param e druha hrana
     * @return vrchol v obou hranch
     */
    public Vector3 getVertex(Edge e) {
        if (vertex1 == e.vertex1 || vertex1 == e.vertex2) {
            return getVertex1();
        } else if (vertex2 == e.vertex1 || vertex2 == e.vertex2) {
            return getVertex2();
        } else {
            throw new Error();
        }
    }

    
    /**
     * Vrati index vrchol, ktery je pouzity v teto hrane a hrane e
     * vzdy hrany musi mit spolecny vrchol jinak se jedna o chybu
     * @param e druha hrana
     * @return index vrchol v obou hranch
     */
    public int getVertexIndex(Edge e) {
        if (vertex1 == e.vertex1 || vertex1 == e.vertex2) {//TODO Error
            return vertex1;
        } else if (vertex2 == e.vertex1 || vertex2 == e.vertex2) {
            return vertex2;
        } else {
            throw new Error();
        }
    }

    /**
     * Nalezne vrchol, ktery nasleduje ve stene v ktere je tato hrana pouzita
     * po smeru hodinovych rucicek
     * @return index dalsiho vrcholu
     */
    public int getCLWNextVertex() {
        OrientatedPlane p = findRemovedPlane();
        //nalezt hranu ktera je po teto
        for (int i = 0; i < 3; i++) {
            if (p.get(i) == this) {
                if (p.get(i).getVertexIndex(p.get(++i)) == this.vertex1) {
                    return this.vertex2;
                } else {
                    return this.vertex1;
                }
            }
        }
        throw new Error();
    }

    /**
     * Nalezne stenu, ktera byla v kroku aloritmu oznacena jako zrusena
     * @return 
     */
    public OrientatedPlane findRemovedPlane() {
        for (OrientatedPlane p : planes) {
            for (Edge e : p) {
                if (e.quantities == -1) {
                    return p;
                }
            }
        }

        //there has to be only one plane to remove, so we have to finde plane whose edges#quatities == 1
        for (OrientatedPlane p : planes) {
            boolean isRemovedPlane = true;
            for (Edge e : p) {
                if (e.quantities != 1) {
                    isRemovedPlane = false;
                    break;
                }
            }

            if (isRemovedPlane) {
                return p;
            }
        }
        throw new Error();
    }

    @Override
    public Iterator<OrientatedPlane> iterator() {
        return planes.iterator();
    }

    @Override
    public String toString() {
        return vertex1 + " : " + vertex2;
    }

}
