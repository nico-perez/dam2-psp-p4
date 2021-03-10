module dev.el_nico.dam2_psp_p4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens dev.el_nico.dam2_psp_p4 to javafx.fxml;
    exports dev.el_nico.dam2_psp_p4;
}