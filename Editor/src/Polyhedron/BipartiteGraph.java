package Polyhedron;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Trida reprezenutujici bipartitni graf
 * @author ggrrin_
 * @param <L> leva partita grafu
 * @param <R> prava partita grafu
 */
class BipartiteGraph<L extends IIndexable, R> {

    private CustomList<L> leftPartity;
    private ArrayList<R> rightPartity;

    private ArrayList<ArrayList<Integer>> leftEdges;
    private ArrayList<ArrayList<L>> rightEdge;

    private ArrayList<boolean[]> incidenceMatrix;

    /**
     * inicializuje dany graf s danou levou a pravou partitou 
     * @param leftPartity
     * @param rightPartity 
     */
    public BipartiteGraph(CustomList<L> leftPartity, ArrayList<R> rightPartity) {
        this.leftPartity = leftPartity;
        this.rightPartity = rightPartity;

        rightEdge = new ArrayList<>();
        for (int i = 0; i < rightPartity.size(); i++) {
            rightEdge.add(new ArrayList<>());
        }

        leftPartity.regAfterAddEvent(() -> {
            leftEdges.add(new ArrayList<>());
            incidenceMatrix.add(new boolean[rightPartity.size()]);
        });

        leftEdges = new ArrayList<>(leftPartity.size());
        for (int i = 0; i < leftPartity.size(); i++) {
            leftEdges.set(i, new ArrayList<>());
        }

        incidenceMatrix = new ArrayList<>(leftPartity.size());
        for (int i = 0; i < incidenceMatrix.size(); i++) {
            incidenceMatrix.set(i, new boolean[rightPartity.size()]);
        }
    }

    /**
     * spoji objekt z prave partyty s objektem pravy partity na indexu r
     * @param l objekt leve partity
     * @param r index prave paritty
     */
    public void Connect(L l, int r) {
        Connect(l.getIndex(), r);
    }

    
    /**
     * spoji l-ty vrchol leve partity s r-tym vrcholem prave partity
     * @param l index objektu leve partity
     * @param r index objektu prave partity
     */
    private void Connect(int l, int r) {
        if (r > rightPartity.size() || r < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (incidenceMatrix.get(l)[r]) {
            return;
        }

        leftEdges.get(l).add(r);
        rightEdge.get(r).add(leftPartity.get(l));
        incidenceMatrix.get(l)[r] = true;
    }

    /**
     * Vrati iterator iterujici pres vsechny sousedy vrcholu dane indexem v prave partity
     * @param index index vrcholu v prave partite
     * @return 
     */
    public Iterator<L> GetNeighboursOfR(int index) {
        return rightEdge.get(index).iterator();
    }

    /**
     * Vrati iterator iterujici pres vsechny sousedy vrcholu dane objektem item v leve partite
     * @param item objet v leve partite
     * @return  
     */
    public Iterator<KeyPairValue<Integer, R>> GetNeighboursOfL(L item) {
        return GetNeighboursOfL(item.getIndex());
    }

    /**
     * Vrati iterator iterujici pres vsechny sousedy vrcholu dane indexem objektu v leve partite
     * @param index objet v leve partite
     * @return  
     */
    public Iterator<KeyPairValue<Integer, R>> GetNeighboursOfL(int index) {
        return new PairIterator(index);
    }

    /**
     * iterator iterujici pres sousedy vrcholu leve partity dane indexem 
     */
    class PairIterator implements Iterator<KeyPairValue<Integer, R>> {

        Iterator<Integer> iterator;

        public PairIterator(int index) {
            iterator = leftEdges.get(index).iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public KeyPairValue<Integer, R> next() {
            if (hasNext()) {
                int val = iterator.next();
                return new KeyPairValue<>(val, rightPartity.get(val));
            } else {
                throw new IllegalStateException("Cannont call next, when hasNext returns false!");
            }
        }
    }
}
