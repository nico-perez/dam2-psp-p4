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

    private static String usuarioLogeado;

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

    public static String getUser() {
        return usuarioLogeado;
    }

    public static boolean login(String user, String pass) {
        try {
            if (!ftpClient.isConnected()) ftpClient = establecerConexion();
            if (ftpClient.login(user, pass)) {
                usuarioLogeado = user;
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            ftpClient.getReplyString();
            return false;
        }
    }

    public static void logout() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
            usuarioLogeado = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
                
                boolean ok = ftpClient.storeFile(rutaRemota + file.getName(), fis);

                if (ok) {
                    System.out.println("Archivo subido a " + rutaRemota);
                } else {
                    System.out.println("Error al subir archivo");
                }

                return ok;
                
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static void crearDirectorio(String ruta) {
        try {
            if (ftpClient.makeDirectory(ruta)) {
                System.out.println("Directorio creado: " + ruta);
            } else {
                System.out.println("Directorio no creado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean eliminarFichero(String ruta) {
        try {
            boolean ok = ftpClient.deleteFile(ruta);
            if (ok) {
                System.out.println("Fichero eliminado: " + ruta);
            } else {
                System.out.println("Fichero no eliminado");
            }
            return ok;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getReplyCode() {
        return ftpClient.getReplyCode();
    }

    public static String getReplyString() {
        return ftpClient.getReplyString();
    }

    public static String descargarVistaPrevia(String ruta) {
        return "No vista previa";
        // try {

        //     InputStream is = ftpClient.retrieveFileStream(ruta);
        //     String s = new String(is.readNBytes(369));
        //     ftpClient.completePendingCommand();
        //     System.out.println(ruta);
        //     return s;

        //     // File f = File.createTempFile("movida", null);
        //     // boolean is = ftpClient.retrieveFile(ruta, new FileOutputStream(f));

        //     // if (is) {
        //     //     try (var fis = new FileInputStream(f)) {
        //     //         return new String(fis.readNBytes(369));
        //     //     }
        //     // } else {
        //     //     return "";
        //     // }
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     return "";
        // }
    }

    public static boolean eliminarDirectorio(String ruta) {

        try {
            boolean ok = ftpClient.removeDirectory(ruta);
            if (ok) {
                System.out.println("Directorio eliminado: " + ruta);
            } else {
                System.out.println("Directorio no eliminado");
            }
            return ok;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } 

    }

}
