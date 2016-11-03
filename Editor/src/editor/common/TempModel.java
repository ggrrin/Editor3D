/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;
import java.util.List;

/**
 * Docasny model implementujici zakladni funkce
 * @author ggrrin_
 */
public class TempModel implements IModel{

    List<Integer> i;
    List<Vector3> v;
    
    /**
     * Inicializuje docasny model indexbufferem i a vertexBuferem v
     * @param i indexBuffer
     * @param v vertexByffer
     */
    public TempModel(List<Integer> i, List<Vector3> v)
    {
        this.i = i;
        this.v = v;
    }
    
    @Override
    public List<Vector3> getVertexBuffer() {
        return  v;
    }

    @Override
    public List<Integer> getIndexBuffer() {
        return i;
    }
    
}
