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
    public static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null) {
            throw new SQLException("No se encontró la variable DATABASE_URL");
        }
        return DriverManager.getConnection(dbUrl);
    }
}
