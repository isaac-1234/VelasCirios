/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.velascirios;

/**
 *
 * @author isaac
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Productos {
    public void agregarProducto(String nombre, String descripcion, double precio, int stock) throws SQLException {
        String query = "INSERT INTO productos (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, precio);
            stmt.setInt(4, stock);
            stmt.executeUpdate();
        }
    }

    public List<String> listarProductos() throws SQLException {
        String query = "SELECT * FROM productos";
        List<String> productos = new ArrayList<>();
        try (Connection conn = Datos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                productos.add(rs.getInt("id") + ": " + rs.getString("nombre") + " - $" + rs.getDouble("precio") + " - Stock: " + rs.getInt("stock"));
            }
        }
        return productos;
    }

    public void actualizarProducto(int id, String nombre, String descripcion, double precio, int stock) throws SQLException {
        String query = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE id = ?";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, precio);
            stmt.setInt(4, stock);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    public void eliminarProducto(int id) throws SQLException {
        String query = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
