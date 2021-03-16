package dev.el_nico.dam2_psp_p4.gui;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;

public class LinkCarpeta extends Hyperlink {
 
    private final TreeItem<InfoFtpFile> ref;

    public LinkCarpeta(String nombre, TreeItem<InfoFtpFile> ref) {
        super(nombre);
        this.ref = ref;
    }

    public TreeItem<InfoFtpFile> getItem() {
        return ref;
    }

}
