package Polyhedron;

import java.util.Iterator;

/**
 * Trida reprezentujici rovinu v 3D prostoru
 *
 * @author ggrrin_
 */
public class OrientatedPlane implements Iterable<Edge>, IIndexable {

    private float d;
    private Vector3 normal;

    private Edge E1;
    private Edge E2;
    private Edge E3;

    /**
     * Prvni hrana mezi body reprezentujici roviny
     *
     * @return hrana
     */
    public Edge getE1() {
        return E1;
    }

    /**
     * Druha hrana mezi body reprezentujici roviny
     *
     * @return hrana
     */
    public Edge getE2() {
        return E2;
    }

    /**
     * Treti hrana mezi body reprezentujici roviny
     *
     * @return hrana
     */
    public Edge getE3() {
        return E3;
    }

    
    /**
     * Normala roviny
     * @return  normala roviny
     */
    public Vector3 getNormal() {
        return normal;
    }

    /**
     * Zda je rovina pouzivana v modelu
     * @return true pokud je pouzivana
     */
    public boolean isAlive() {
        return index > -1;
    }

    /**
     * Nastavi zda je rovina oznacena jako pouzivana
     * @param v true pouzivana false naopak
     */
    public void setLive(boolean v) {
        if (!v) {
            index = -1;
        }
    }

    /**
     * Rovna reprezentovna rovnici ax + by + cz + d = 0 vrati d
     * @return vratid  z rovnice
     */
    
    public float getD() {
        return d;
    }

    /**
     * Zkonstruuje rovni pomoci bodu a normaly
     * @param point bod lezici v rovine
     * @param normal normala roviny
     */
    public OrientatedPlane(Vector3 point, Vector3 normal) {
        this.normal = normal;
        d = -(normal.X * point.X + normal.Y * point.Y + normal.Z * point.Z);
    }

    /**
     * Zkonstruuje rovinu pomoci trech bodu
     * @param x prvni bod v rovine
     * @param y druhy bod v rovine
     * @param z treti bod v rovine
     */
    public OrientatedPlane(Vector3 x, Vector3 y, Vector3 z) {
//        normal = Vector3.Cross(x.sub(y), x.sub(z)).invert();
//
//        d = -(normal.X * x.X + normal.Y * x.Y + normal.Z * x.Z);
        this(x, Vector3.Cross(x.sub(y), x.sub(z)).invert());
    }

    
    /**
     * Zkonstruuje rovinu pomoci 3 hran. Kde kazde 2 hrany maji prave 1 spolecny
     * bod. Pokud registerEdge je true, tak se v hrane nastavi ze pouziva tuto
     * rovinu
     * @param x prvni hrana
     * @param y druha hrana
     * @param z treti hrana
     * @param registerEdge Zda ulozit do hrany informaci v kterych je rovinach
     */
    public OrientatedPlane(Edge x, Edge y, Edge z, boolean registerEdge) {
        this(x.getVertex(y), y.getVertex(z), z.getVertex(x));
        this.E1 = x;
        this.E2 = y;
        this.E3 = z;

        if (registerEdge) {
            registerEdgesConnection();
        }
    }

    /**
     * Hranam urcujici tuto roviny, ulozi ze tuto rovinu pouzivaji
     */
    public final void registerEdgesConnection() {
        this.E1.useEdgeIn(this);
        this.E2.useEdgeIn(this);
        this.E3.useEdgeIn(this);
    }

    /**
     * Otoci normalu roviny, rovina samotna nezmenena
     */
    public void InvertPlane() {
        normal.invert();
        d *= -1;

        Edge e1 = E1;
        E1 = E3;
        E3 = e1;
    }

    /**
     * Presnost na kterou se pocita prunik
     */
    float epsilon = 0.0001f;

    /**
     * Vrati zda je rovina privracena k danemu bodu x
     * @param x bod z ktereho se divame
     * @return true rovina je privracena false jinak
     */
    public boolean intersect(Vector3 x) {
        float res = normal.X * x.X + normal.Y * x.Y + normal.Z * x.Z + d;
//        System.out.print(this);
//        //System.out.print(" ");
//        //System.out.print( x );
//        System.out.print(" => " );
        res = Math.abs(res) < epsilon ? 0 : res;
//        System.out.println( res >= 0 );

        return res >= 0;
    }

    /**
     * Urci zda lezi bod presne v rovine
     * @param x bod pro test
     * @return  true lezi flase naopak
     */
    public boolean liesIn(Vector3 x) {
        float res = normal.X * x.X + normal.Y * x.Y + normal.Z * x.Z + d;
        res = Math.abs(res) < epsilon ? 0 : res;
        return res == 0;
    }

    /**
     * Nastavi ze tato rovina je nepouzivana a odstrani sebe ze  vsech hran 
     * ktere pouziva
     */
    public void Remove() {
        for (Edge i : this) {
            i.removeEdgeFrom(this);
        }

        this.setLive(false);
    }

    private int index;

    
    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int i) {
        index = i;
    }

    /**
     * Vrati hranu urcujici rovinu dle indexu (moduleneho)
     * @param i index hrany
     * @return hrana odpovidajici danemu indexu i
     */
    public Edge get(int i) {
        int current = i % 3;
        if (current < 0) {
            current += 3;
        }
        switch (current) {
            case 0:
                return E1;
            case 1:
                return E2;
            case 2:
                return E3;
            default:
                throw new Error();
        }
    }

    @Override
    public Iterator<Edge> iterator() {
        return new EdgeIterator();
    }

    /**
     * Iterator iterujici skrze hrany roviny
     */
    class EdgeIterator implements Iterator<Edge> {

        int current = -1;

        @Override
        public boolean hasNext() {
            return (current + 1) < 3;
        }

        @Override
        public Edge next() {
            if (hasNext()) {
                return get(++current);
            } else {
                throw new IllegalStateException();
            }
        }

    }

    //CCLW
    /**
     * Iterator iterujici pres indexy vrcholu roviny proti smeru hod. rucicek
     * @return iterator indexu
     */
    public Iterator<Integer> getVertexIndiceIterator() {
        return new IndiceIterator();
    }

    /**
     * Iterator iterujci indexy vrcholu roviny
     */
    class IndiceIterator implements Iterator<Integer> {

        int current = 3;

        @Override
        public boolean hasNext() {
            return (current - 1) >= 0;
        }

        @Override
        public Integer next() {
            if (hasNext()) {
                return get(--current).getVertexIndex(get(current - 1));
            } else {
                throw new IllegalStateException();
            }
        }

    }

    @Override
    public String toString() {
        return E1 + " : " + E2 + " : " + E3 + " || " + isAlive();
    }

}
