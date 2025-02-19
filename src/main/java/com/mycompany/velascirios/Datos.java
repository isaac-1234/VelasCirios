/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.velascirios;

/**
 *
 * @author IsaacdeJesús
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Datos {
    private static final String URL = "postgresql://postgres:AkUVKvfvREvkMkTpbIRivNHjsNIQCtHq@postgres.railway.internal:5432/railway";
    private static final String USUARIO = "postgres";
    private static final String CONTRASEÑA = "AkUVKvfvREvkMkTpbIRivNHjsNIQCtHq";
    
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar con la base de datos.");
        }
    }
}