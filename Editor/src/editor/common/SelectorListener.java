/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import java.util.Set;

/**
 * Listener pro Selector Tool
 * @author ggrrin_
 */
public interface SelectorListener {
    
    /**
     * Oznamena ze mnozina vybranych bodu byla zmenena
     * @param items mnozina vybranych bodu
     */
    public void itemsChanged(Set<Integer> items);
}
