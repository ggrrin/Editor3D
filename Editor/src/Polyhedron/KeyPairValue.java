/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Polyhedron;

/**
 *Trida reprezentujici generickou dvojici klic hodnota
 * @author ggrrin_
 */
public class KeyPairValue<K,V> {
    public K key;
    public V value;
    
    /**
     * inicializuje dvojici klicem a hodnotou
     * @param key klic
     * @param value  hodnota
     */
    public KeyPairValue(K key, V value)
    {
        this.key = key;
        this.value = value;
    }
}
