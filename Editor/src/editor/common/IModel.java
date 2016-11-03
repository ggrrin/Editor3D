/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import java.util.List;

/**
 * Rozhrani urcujici minimalni funkce modelu
 * @author ggrrin_
 */
public interface IModel {    
    /**
     * Vertexbuffer daneho modelu
     * @return buffer
     */
    public List<Vector3> getVertexBuffer();
    
    /**
     * Indexbuffer daneho modelu
     * @return buffer
     */
    public List<Integer> getIndexBuffer();            
}
