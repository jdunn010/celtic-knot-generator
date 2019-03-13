package gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PictureFileFilter extends FileFilter {
    public String getDescription() {
        return "Picture Files";
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String fileExtension = getExtension(f);
        if (fileExtension != null
                && (fileExtension.equals("jpg") || fileExtension.equals("png"))) {
            return true;
        } else {
            return false;
        }
    }

    public static String getExtension(File f) {
        String fileExtension = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            fileExtension = s.substring(i + 1).toLowerCase();
        }
        return fileExtension;
    }
}
