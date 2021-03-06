/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.convexEditor;

import editor.EditorWindow;
import editor.common.CustomFileFilter;
import editor.common.EditorPanel;
import editor.common.ToolsGLJPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Rozhrani panelu editoru konvexnich objektu
 * @author ggrrin_
 */
public class ConvexObjUI extends EditorPanel {

    ConvexGLJPanel plane = new ConvexGLJPanel();
    SelectorMenu sMenu;
    JMenu menu;

  

    /**
     * Vytvori ui
     * @param w  odkaz na frame
     */
    public ConvexObjUI(EditorWindow w) {
        super(w);
        initComponents();      
        jPanel2.add(plane);
        initMenu();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(200, 240, 240));
        jPanel1.setToolTipText("");

        jButton1.setText("vyber");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jButton2.setText("bod");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jLabel1.setText("tool");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 200, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetToolbar() {
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);

        if (sMenu != null) {
            jPanel3.remove(sMenu);
        }
    }

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        resetToolbar();
        plane.selectSelectorTool();
        jLabel1.setText(jButton1.getText());
        jButton1.setEnabled(false);

        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(sMenu = new SelectorMenu(plane));
        plane.getSelector().addSelectorListener(sMenu);
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        resetToolbar();
        plane.selectPointTool();
        jLabel1.setText(jButton2.getText());
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton2MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

    @Override
    public JMenu getPanelMenu() {
        return menu;
    }

    @Override
    public boolean dispose() {
//        final String yes = ;
//        final String no = ;
//        final String cancel = ;
        if(!askAndMove())
            return false;
        //Custom button text
        plane.Stop();

        return true;
    }



    /**
     * inicializuje menu panelu
     */
    private void initMenu() {
        menu = new JMenu("ConvexObj Panel");
        JMenuItem newItem = new JMenuItem("Novy ...");
        JMenuItem openItem = new JMenuItem("Otevrit...");
        JMenuItem saveItem = new JMenuItem("Ulozit...");
        JMenuItem closePan = new JMenuItem("Zavrit panel");

        menu.add(newItem);
        menu.add(openItem);
        menu.add(saveItem);
        menu.add(closePan);

        newItem.addActionListener((ActionEvent e) -> newr());
        openItem.addActionListener((ActionEvent e) -> open());
        saveItem.addActionListener((ActionEvent e) -> save());
        closePan.addActionListener((ActionEvent e) -> close());
    }

    @Override
    protected void newr() {        
        if(!askAndMove())
            return;
        
        resetToolbar();
        plane.reset();
    }

    @Override
    protected void open() {
        if(!askAndMove())
            return;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new CustomFileFilter("Convex object .cob", "cob"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            openFile(file);
        }

    }
    
    @Override
    public void openFile(File file)
    {
        
        try {
                plane.open(file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid file acess",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
    }

    @Override
    protected boolean save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new CustomFileFilter("ConvexObject .cob", "cob"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String file = fileChooser.getSelectedFile().getPath();
            if (!file.endsWith(".cob")) {
                file += ".cob";
            }

            try {
                plane.save(new File(file));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Invalid file acess",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void close() {
        myEditor.selectPanel(null);
    }

    @Override
    public ToolsGLJPanel getCanvas() {
        return plane;
    }
}
