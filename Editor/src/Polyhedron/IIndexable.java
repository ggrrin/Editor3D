/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Polyhedron;

/**
 * Rozhrani poskytujici na kazdem objektu index v kolekci v ktere je obekt ulozeny
 * slouzi k rychlemu pristupu
 * @author ggrrin_
 */
public interface IIndexable {
    
    /**
     * Index v kolekci
     * @return index kolekci
     */
    int getIndex();
    
    /**
     * Nastavi na kterem indexu v kolekci je objekt
     * @param i index
     */
    void setIndex(int i);
}
