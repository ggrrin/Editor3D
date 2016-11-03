/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Rozhrani urcujici zakladni funkce ktere musi obsahovat nastroj editoru
 * Pozito k "automatickemu" meni nastroju
 * @author ggrrin_
 */
public interface ITool extends MouseMotionListener, MouseListener, MouseWheelListener {
    
    /**
     * inicializace pre pouzitim nastroje
     */
    public  default void init(){};
    
    /**
     * Zavola se pred pouzitim jineho nastroje, k vynulovani stavu
     */
    public void reset();
    
}
