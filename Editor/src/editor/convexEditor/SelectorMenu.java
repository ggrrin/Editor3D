/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import editor.common.SelectorListener;
import java.util.Set;

/**
 * Menu nastroje pro vyber bodu
 * @author ggrrin_
 */
public class SelectorMenu extends javax.swing.JPanel implements SelectorListener {

    ConvexGLJPanel p;
    
    /**
     * Creates new form selectorMenu
     * @param p odpovidajici vykreslovaci panel
     */
    public SelectorMenu(ConvexGLJPanel p) {
        initComponents();
        this.p = p;
        jButton1.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jButton1.setText("Smazat vybrane");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        add(jButton1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        p.RemoveSelected();
    }//GEN-LAST:event_jButton1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemsChanged(Set<Integer> items) {        
            jButton1.setEnabled(!items.isEmpty());
    }
}
