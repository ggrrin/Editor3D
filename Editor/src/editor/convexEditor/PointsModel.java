/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import Polyhedron.Vector3;
import editor.common.IModel;
import java.util.ArrayList;

/**
 * Docasny model implementujici IModel
 * @author ggrrin_
 */
public class PointsModel implements IModel{

    private ArrayList<Vector3> vertices = new ArrayList<>();
    
    /**
     * inicializuje model body
     * @param points bode vertexbufferu
     */
    public PointsModel(Iterable<Vector3> points)
    {
        for(Vector3 v : points)
            vertices.add(v);
    }
    
    @Override
    public ArrayList<Vector3> getVertexBuffer() {
        return vertices;
    }

    @Override
    public ArrayList<Integer> getIndexBuffer() {
        return null;
    }
    
}
