/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import editor.EditorWindow;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Abstraktni panel urcujci funkce ktere by mel mit kazdy panel editoru
 * @author ggrrin_
 */
public abstract class EditorPanel extends JPanel {

    protected EditorWindow myEditor;
  
    
    public EditorPanel(EditorWindow parent)
    {
        myEditor = parent;
    }
    
    /**
     * Vrati menu do menubaru daneho panelu
     * @return dane menu
     */
    public abstract JMenu getPanelMenu();

    /**
     * Odstraneni panelu, panel si muze vynuti preruseni pomoci odeslani false
     * @return true ok , false panel si vynucuje preruseni 
     */
    public abstract boolean dispose();

    /**
     * Vrati vykreslovaci panel tohoto panelu
     * @return vykreslovaci panel
     */
    public abstract ToolsGLJPanel getCanvas();
    
    /**
     * Zobrazeni dialogu pro ulozeni
     * @return true v akci je mozno poracovat, false pozadavek na zruseni akce
     */
    protected boolean askAndMove() {
        if(!getCanvas().save)
            return true;
        
        Object[] options = {"Ano", "Ne", "Zrusit"};
        int n = JOptionPane.showOptionDialog(this,
                "Chcete ulozit zmeny?",
                "Ulozit...",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        switch (n) {
            case 0:
                if (!save()) {
                    return false;
                }
                break;
            case 1:
                return true;
            case 2:
                return false;
        }

        return true;
    }

    /**
     * Rutina pro vytoreni noveho modelu panelu
     */
    protected abstract void newr();
    
    /**
     * Rutina pro otevreni modelu ze souboru
     */
    protected abstract void open();
    
    /**
     * rutina pro otevratni modelu z daneho souboru
     * @param f soubor s modelem
     */
    public void openFile(File f){}
    
    /**
     * Rutina pro ulozeni modelu do souboru
     * @return true pokud se podarilo ulozit, jiank false
     */
    protected abstract boolean save();
    
    /**
     * Rutina pro zaverni panelu
     */
    protected abstract void close();
}
