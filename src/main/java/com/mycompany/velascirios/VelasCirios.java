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
                System.out.println("ID: " + rs.getInt("id") + " | Nombre: " + rs.getString("nombre") + " | Precio: $" + rs.getDouble("precio") + " | Cantidad: " + rs.getInt("cantidad"));
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
    System.out.print("Nombre del cliente: ");
    String cliente = scanner.nextLine();

    // Insertar pedido y obtener ID generado
    int pedidoId = insertarPedido(cliente);
    if (pedidoId == -1) {
        System.out.println("Error al crear el pedido.");
        return;
    }

    double totalPedido = 0;
    while (true) {
        System.out.print("ID del producto (0 para terminar): ");
        int productoId = scanner.nextInt();
        if (productoId == 0) break;

        System.out.print("Cantidad: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        // Obtener precio unitario y verificar disponibilidad
        double precioUnitario = obtenerPrecioProducto(productoId);
        int cantidadDisponible = obtenerCantidadProducto(productoId);

        if (precioUnitario == -1) {
            System.out.println("Producto no encontrado.");
            continue;
        }
        if (cantidad > cantidadDisponible) {
            System.out.println("Cantidad insuficiente. Disponible: " + cantidadDisponible);
            continue;
        }

        double subtotal = precioUnitario * cantidad;
        totalPedido += subtotal;

        // Insertar el detalle del pedido
        insertarDetallePedido(pedidoId, productoId, cantidad, subtotal);

        // Restar la cantidad del inventario
        actualizarCantidadProducto(productoId, cantidad);

        System.out.println("Producto agregado al pedido.");
    }

    // Actualizar el total del pedido
    actualizarTotalPedido(pedidoId, totalPedido);
    System.out.println("Pedido finalizado. Total: $" + totalPedido);
}
// Método para insertar un pedido y obtener su ID
// Método para insertar un pedido y obtener su ID


// Método para insertar un detalle de pedido
private static void insertarDetallePedido(int pedidoId, int productoId, int cantidad, double subtotal) {
    String query = "INSERT INTO detalle_pedidos (pedido_id, producto_id, cantidad, subtotal) VALUES (?, ?, ?, ?)";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, pedidoId);
        stmt.setInt(2, productoId);
        stmt.setInt(3, cantidad);
        stmt.setDouble(4, subtotal);
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al agregar producto al pedido: " + e.getMessage());
    }
}
// Método para actualizar el total del pedido


// Método para insertar un pedido y obtener su ID
private static int insertarPedido(String cliente) {
    String query = "INSERT INTO pedidos (cliente) VALUES (?) RETURNING id";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, cliente);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.out.println("Error al crear pedido: " + e.getMessage());
    }
    return -1;
}

// Método para insertar un detalle de orden con precio unitario
private static void insertarDetalleOrden(int pedidoId, int productoId, int cantidad, double precioUnitario, double subtotal) {
    String query = "INSERT INTO detalle_orden (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, pedidoId);
        stmt.setInt(2, productoId);
        stmt.setInt(3, cantidad);
        stmt.setDouble(4, precioUnitario);
        stmt.setDouble(5, subtotal);
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al agregar producto al pedido: " + e.getMessage());
    }
}

// Método para actualizar el total del pedido
private static void actualizarTotalPedido(int pedidoId, double total) {
    String query = "UPDATE pedidos SET total = ? WHERE id = ?";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setDouble(1, total);
        stmt.setInt(2, pedidoId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error al actualizar total del pedido: " + e.getMessage());
    }
}

// Método para obtener el precio de un producto
private static double obtenerPrecioProducto(int productoId) {
    String query = "SELECT precio FROM productos WHERE id = ?";
    try (Connection conn = Datos.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, productoId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("precio");
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al obtener el precio del producto: " + e.getMessage());
    }
    return -1; // Si el producto no existe
}
private static void listarPedidos() {
    System.out.println("\n=== Lista de Pedidos ===");

    String query = "SELECT p.id, p.cliente, p.fecha, p.total, dp.producto_id, pr.nombre AS producto, dp.cantidad, dp.subtotal " +
                   "FROM pedidos p " +
                   "JOIN detalle_pedidos dp ON p.id = dp.pedido_id " +
                   "JOIN productos pr ON dp.producto_id = pr.id " +
                   "ORDER BY p.id";

    try (Connection conn = Datos.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        int lastPedidoId = -1;
        while (rs.next()) {
            int pedidoId = rs.getInt("id");
            if (pedidoId != lastPedidoId) {
                System.out.println("\nPedido ID: " + pedidoId +
                                   " | Cliente: " + rs.getString("cliente") +
                                   " | Fecha: " + rs.getTimestamp("fecha") +
                                   " | Total: $" + rs.getDouble("total"));
                lastPedidoId = pedidoId;
            }
            System.out.println("  → Producto: " + rs.getString("producto") +
                               " | Cantidad: " + rs.getInt("cantidad") +
                               " | Subtotal: $" + rs.getDouble("subtotal"));
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
private static int obtenerCantidadProducto(int productoId) {
    String sql = "SELECT cantidad FROM productos WHERE id = ?";
    try (Connection conn = Datos.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, productoId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("cantidad");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1; // Error o producto no encontrado
}

private static void actualizarCantidadProducto(int productoId, int cantidad) {
    String sql = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
    try (Connection conn = Datos.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, cantidad);
        stmt.setInt(2, productoId);
        stmt.executeUpdate();
        System.out.println("Cantidad actualizada para producto ID: " + productoId);
    } catch (SQLException e) {
        e.printStackTrace();
    }
  }
} 
