package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class KnotFileFilter extends FileFilter {

    public String getDescription () {
        return "Knot Files";
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String fileExtension = getExtension(f);
        if (fileExtension != null && fileExtension.equals("knot")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getExtension(File f) { String fileExtension = null ;
        String s = f .getName() ;
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            fileExtension = s.substring(i + 1).toLowerCase();
        }
        return fileExtension ;
    }
}
