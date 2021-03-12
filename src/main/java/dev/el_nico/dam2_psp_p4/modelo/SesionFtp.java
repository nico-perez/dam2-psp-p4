package dev.el_nico.dam2_psp_p4.modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

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

    public static boolean login(String user, String pass) {
        try {
            return ftpClient.login(user, pass);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<FTPFile> listarArchivos(String ruta) {
        try {
            var arrayList = new ArrayList<>(Arrays.asList(ftpClient.listFiles(ruta)));
            arrayList.removeIf(f -> f.isDirectory());
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<FTPFile> listarDirectorios(String ruta) {
        try {
            return new ArrayList<>(Arrays.asList(ftpClient.listDirectories(ruta)));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static boolean subirFichero(String rutaRemota, String rutaLocal, boolean reemplazar) {
        File file = new File(rutaLocal);
        if (file.exists()) {
            try (var fis = new FileInputStream(file)) {
                if (reemplazar) {
                    return ftpClient.storeFile(rutaRemota + file.getName(), fis);
                } else {
                    return ftpClient.storeUniqueFile(rutaRemota + file.getName(), fis);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean eliminarFichero(String ruta) {
        try {
            return ftpClient.deleteFile(ruta);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SesionFtp.login("nico", "nico");
        String unaImg = "C:/Users/Nico/Desktop/Nueva imagen de mapa de bits.bmp";
        System.out.println(subirFichero("/", unaImg, true));
        eliminarFichero("/Nueva imagen de mapa de bits.bmp");
    }

}
