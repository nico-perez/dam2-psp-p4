package dev.el_nico.dam2_psp_p4.modelo;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public class SesionFtp {
    
    private static FTPClient ftpClient = establecerConexion();

    private SesionFtp() {}

    private static synchronized FTPClient establecerConexion() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect("localhost");
            return ftpClient;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("No ha sido posible conectar a localhost");
        }
    }

    public void () {

    }

}
