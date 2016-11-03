package  Polyhedron;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Rozsiruje Arraylist, tak ze obsahuje objekty typu indexable, pro pristup
 * indexu v kolkci z objektu
 * @author ggrrin_
 * @param <T> 
 */
class CustomList<T extends IIndexable> implements List<T>, RandomAccess
{
    private ArrayList<T> source = new ArrayList<>();        
    
    private ArrayList<Runnable> evHandlers = new ArrayList<>();
    
    /**
     * registruje do udalosti po pridani objektu Runnable r
     * @param r rutina ktera se provede pri udalosti
     */
    public void regAfterAddEvent(Runnable r)
    {
        evHandlers.add(r);
    }
    
    private void raisEvent()
    {
        for(Runnable r : evHandlers)
            r.run();
    }
    
    /**
     * Prida do kolekce vsechny prvky kolekce c
     * @param c
     * @return 
     */
    public boolean addAll( Collection<? extends T> c)
    {
        for(T t : c)
            this.add(t);
        return  true;
    }
    
    public boolean add(T item)
    {
        item.setIndex(source.size());
        boolean res = source.add(item);
        raisEvent();  
        return res;
    }

    @Override
    public int size() {
        return source.size();
    }

    @Override
    public boolean isEmpty() {
        return source.isEmpty(); 
    }

    @Override
    public boolean contains(Object o) {
        return source.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return source.iterator(); 
    }

    @Override
    public Object[] toArray() {
        return source.toArray(); 
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return source.toArray(ts);
    }

    
    @Override
    public boolean remove(Object o) {
        return source.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        return source.containsAll(clctn);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        return source.removeAll(clctn);
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        return source.retainAll(clctn);
    }

    @Override
    public void clear() {
        source.clear(); 
    }

    @Override
    public T get(int i) {
        return source.get(i);
    }

    @Override
    public T set(int i, T e) {
        return source.set(i, e);
    }

    @Override
    public void add(int i, T e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T remove(int i) {
        return source.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return ((IIndexable)o).getIndex();    
    }

    @Override
    public int lastIndexOf(Object o) {
        return  source.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return source.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return source.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        return source.subList(i, i1);
    }    
    
}