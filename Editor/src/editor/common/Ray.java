/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.OrientatedPlane;
import Polyhedron.Vector3;

/**
 * Trida reprezentujici paprsek
 * @author ggrrin_
 */
public class Ray {
    public Vector3 point;
    public Vector3 direction;
    
    /**
     * Inicializuje paprsek pomoci dvou bodu
     * @param point1 prvni bod paprsku
     * @param point2 druhy bod paprsku
     */
    public Ray(Vector3 point1, Vector3 point2)
    {
        this.point = point1;
        this.direction = point2.sub(point1);
    } 
 
    
    /**
     * Vrati bod proniku paprsku a roviny
     * @param plane rovina
     * @return  bod pruniku
     */
    public Vector3 intersects(OrientatedPlane plane)
    {
        float a = plane.getNormal().X;
        float b = plane.getNormal().Y;
        float c = plane.getNormal().Z;
        
        Vector3 d = direction;
        
        float k = -(plane.getD() + a*point.X + b*point.Y + c*point.Z)/(a*d.X + b*d.Y + c*d.Z);
        
        return point.plus(d.ms(k));
    }
   
}
