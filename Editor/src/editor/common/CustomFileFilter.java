/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.common;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * File filter s urcenim filtrovani del pripony v konstruktoru
 * @author ggrrin_
 */
public class CustomFileFilter extends FileFilter {

    String[] exten;
    String description;
    
    
    /**
     * Inicializuje file filter dle pripon extensions  a popisu descriptin
     * @param description popis filtru
     * @param extensions pripony prochazejici filterm
     */
    public CustomFileFilter(String description, String... extensions) {
        exten = extensions;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        String extension = getExtension(f.getPath());
        if (extension == null) {
            return false;
        } else {
            boolean res = false;

            for (String exten1 : exten) {
                if (extension.equals(exten1.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Z cesty souboru vrati jeho pripotnu
     * @param fileName cesta k souboru
     * @return pripona
     */
    public static String getExtension(String fileName) {
        String extension = null;

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        return extension;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
