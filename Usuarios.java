/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author isaac
 */
package com.mycompany.velascirios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Usuarios {
    public void agregarUsuario(String nombre, String email, String rol) throws SQLException {
        String query = "INSERT INTO usuarios (nombre, email, rol) VALUES (?, ?, ?)";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, rol);
            stmt.executeUpdate();
        }
    }

    public List<String> listarUsuarios() throws SQLException {
        String query = "SELECT * FROM usuarios";
        List<String> usuarios = new ArrayList<>();
        try (Connection conn = Datos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                usuarios.add(rs.getInt("id") + ": " + rs.getString("nombre") + " - " + rs.getString("email") + " - Rol: " + rs.getString("rol"));
            }
        }
        return usuarios;
    }

    public void actualizarUsuario(int id, String nombre, String email, String rol) throws SQLException {
        String query = "UPDATE usuarios SET nombre = ?, email = ?, rol = ? WHERE id = ?";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, rol);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

    public void eliminarUsuario(int id) throws SQLException {
        String query = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
