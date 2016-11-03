/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Polyhedron;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Trida s ktera v dane kolekci nalezne index objektu s danou vlastnosti
 * @author ggrrin_
 * @param <T> Typ kolekce
 */
public class Extension<T> {
   
    /**
     * Nalezn index objektu v kolekci list od indexu from dle vlastnosti p
     * @param from pocatecni index
     * @param p vlastnost pro urceni vyberu
     * @param list kolekce v ktere hledame
     * @return index nalezenoeho objektu, nebo -1 pro nenalezeno
     */
    public int FindIndex(int from, Predicate<T> p, ArrayList<T> list) {

        int index = from;
        for (; index < list.size(); index++) {
            if (p.test(list.get(index))) 
            {
                return index;                
            }
        }
        return -1;
    }

}
