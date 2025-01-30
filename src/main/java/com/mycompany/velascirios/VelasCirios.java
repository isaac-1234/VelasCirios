package com.mycompany.velascirios;

import java.sql.*;
import java.util.Scanner;

public class VelasCirios {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Sistema Velas y Cirios ===");

        // Solicitar credenciales
        System.out.print("Usuario: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        try {
            String role = authenticate(username, password);
            if (role != null) {
                System.out.println("\nInicio de sesión exitoso. Rol: " + role);
                showMenu(role);
            } else {
                System.out.println("Error: Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión a la base de datos: " + e.getMessage());
        }
    }

    // Método para autenticar usuario y obtener su rol
    private static String authenticate(String username, String password) throws SQLException {
        String query = "SELECT rol FROM usuarios WHERE nombre = ? AND contraseña = ?";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rol"); // Devuelve el rol del usuario
                }
            }
        }
        return null; // Retorna null si no encuentra el usuario
    }

    // Menú de opciones según el rol
   private static void showMenu(String role) {
    while (true) {
        System.out.println("\n=== Menú Principal ===");
        System.out.println("1. Ver productos");
        System.out.println("2. Realizar un pedido");
        System.out.println("3. Ver pedidos");
        if (role.equals("admin")) {
            System.out.println("4. Eliminar un pedido");
        }
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        switch (option) {
            case 1:
                listarProductos();
                break;
            case 2:
                realizarPedido();
                break;
            case 3:
                listarPedidos();
                break;
            case 4:
                if (role.equals("admin")) eliminarPedido();
                else System.out.println("Acceso denegado.");
                break;
            case 0:
                System.out.println("Saliendo del sistema...");
                return;
            default:
                System.out.println("Opción inválida.");
        }
    }
}

    // Método para listar productos
    private static void listarProductos() {
        System.out.println("\n=== Lista de Productos ===");
        String query = "SELECT * FROM productos";
        try (Connection conn = Datos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Nombre: " + rs.getString("nombre") + " | Precio: $" + rs.getDouble("precio"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener productos: " + e.getMessage());
        }
    }

    // Método para agregar un producto (solo para admin)
    private static void agregarProducto() {
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine();
        System.out.print("Precio: ");
        double precio = scanner.nextDouble();
        scanner.nextLine(); // Limpiar buffer
String query = "INSERT INTO productos (nombre, precio) VALUES (?, ?)";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombre);
            stmt.setDouble(2, precio);
            stmt.executeUpdate();
            System.out.println("Producto agregado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error al agregar producto: " + e.getMessage());
        }
    }

    // Método para eliminar un producto (solo para admin)
    private static void eliminarProducto() {
        System.out.print("ID del producto a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        String query = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Producto eliminado con éxito.");
            } else {
                System.out.println("No se encontró el producto con ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
    }

private static void realizarPedido() {
    System.out.println("\n=== Realizar Pedido ===");
    
    // Pedir datos del pedido
    System.out.print("ID del producto: ");
    int productoId = scanner.nextInt();
    System.out.print("Cantidad: ");
    int cantidad = scanner.nextInt();
    scanner.nextLine(); // Limpiar buffer
    System.out.print("Nombre del cliente: ");
    String cliente = scanner.nextLine();

    String query = "INSERT INTO pedidos (producto_id, cantidad, cliente) VALUES (?, ?, ?)";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, productoId);
        stmt.setInt(2, cantidad);
        stmt.setString(3, cliente);
        stmt.executeUpdate();
        System.out.println("Pedido realizado con éxito.");
    } catch (SQLException e) {
        System.out.println("Error al realizar el pedido: " + e.getMessage());
    }
}
private static void listarPedidos() {
    System.out.println("\n=== Lista de Pedidos ===");
    String query = "SELECT p.id, pr.nombre, p.cantidad, p.cliente FROM pedidos p JOIN productos pr ON p.producto_id = pr.id";
    try (Connection conn = Datos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") +
                               " | Producto: " + rs.getString("nombre") +
                               " | Cantidad: " + rs.getInt("cantidad") +
                               " | Cliente: " + rs.getString("cliente"));
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener pedidos: " + e.getMessage());
    }
}


private static void eliminarPedido() {
    System.out.print("\nID del pedido a eliminar: ");
    int id = scanner.nextInt();
    scanner.nextLine(); // Limpiar buffer

    String query = "DELETE FROM pedidos WHERE id = ?";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, id);
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Pedido eliminado con éxito.");
        } else {
            System.out.println("No se encontró el pedido con ID " + id);
        }
    } catch (SQLException e) {
        System.out.println("Error al eliminar pedido: " + e.getMessage());
    }
  }
} 
