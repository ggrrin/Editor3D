/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import Polyhedron.Vector3;

/**
 * Trida reprezentujici krabici, interakce s ni
 * @author ggrrin_
 */
public class BoundingBox {

    Vector3 min;
    Vector3 max;

    /**
     * inicializuje krabici dle minimalniho rohu a maximalniho rohu
     * @param min minimum krabice
     * @param max maximum krabice
     */
    public BoundingBox(Vector3 min, Vector3 max) {
        if (min.X >= max.X && min.Y >= max.Y && min.Z >= max.Z) {
            throw new IllegalStateException("min has to be lower then max!");
        }
        this.min = min;
        this.max = max;
    }

    /**
     * Vrati minimalni bod krabice
     * @return minumum
     */
    public Vector3 getMin() {
        return min;
    }

    /**
     * Vrati maximalni bod krabice
     * @return maximum
     */
    public Vector3 getMax() {
        return max;
    }

    /**
     * Vrati rozmer krabice v X ose
     * @return rozmer
     */
    public float getXSize() {
        return max.X - min.X;
    }

    
    /**
     * Vrati rozmer krabice v Y ose
     * @return rozmer
     */
    public float getYSize() {
        return max.Y - min.Y;
    }

    
    /**
     * Vrati rozmer krabice v Z ose
     * @return rozmer
     */
    public float getZSize() {
        return max.Z - min.Z;
    }

    
    /**
     * Urci zda dany bod v je uvnitr krabice
     * @param v bod pro test
     * @return true bod je uvnitr krabice, false naopak
     */
    public boolean intersects(Vector3 v) {
        if (min.X <= v.X && v.X <= max.X)//a b a			
        {
            if (min.Y <= v.Y && v.Y <= max.Y)//a b a				
            {
                if (min.Z <= v.Z && v.Z <= max.Z)//a b a					
                {
                    return true;
                }
            }
        }
        return false;
    }
    
}
