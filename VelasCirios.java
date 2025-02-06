package com.mycompany.velascirios;

import java.sql.*;
import java.util.Scanner;

public class VelasCirios {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Login login = new Login();
        login.setVisible(true);
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
                Menu(role);
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
    private static void Menu(String role) {
    while (true) {
        System.out.println("\n=== Menú Principal ===");
        System.out.println("1. Ver productos");
        System.out.println("2. Realizar un pedido");
        System.out.println("3. Ver pedidos");
        System.out.println("4. Registrar devolución por producto");
        System.out.println("5. Ver piezas devueltas");
        if (role.equals("admin")) {
            System.out.println("6. Eliminar un pedido");
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
                registrarDevolucionPorProducto();
                break;
            case 5:
                listarPiezasDevueltas();
                break;
            case 6:
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
    String query = "INSERT INTO detalle_pedidos (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
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
private static void registrarDevolucionPorProducto() {
    System.out.println("\n=== Registrar Devolución por Producto ===");
    System.out.print("ID del pedido: ");
    int ordenId = scanner.nextInt();
    System.out.print("ID del producto devuelto: ");
    int productoId = scanner.nextInt();
    scanner.nextLine(); // Limpiar buffer

    if (!verificarOrdenYProducto(ordenId, productoId)) {
        System.out.println("Error: El producto no existe en la orden.");
        return;
    }

    System.out.print("Piezas buenas: ");
    int piezasBuenas = scanner.nextInt();
    System.out.print("Piezas reparables: ");
    int piezasReparables = scanner.nextInt();
    System.out.print("Piezas dañadas: ");
    int piezasDanadas = scanner.nextInt();
    System.out.print("Piezas faltantes: ");
    int piezasFaltantes = scanner.nextInt();
    scanner.nextLine(); // Limpiar buffer
    System.out.print("Observaciones: ");
    String observaciones = scanner.nextLine();

    String queryInsert = "INSERT INTO condiciones_entrega (orden_id, producto_id, piezas_buenas, piezas_reparables, piezas_danadas, piezas_faltantes, observaciones) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";

    String queryUpdateDetalle = "UPDATE detalle_pedidos SET cantidad = cantidad - (? + ?) " +
                                "WHERE pedido_id = ? AND producto_id = ?";

    String queryUpdateStock = "UPDATE productos SET cantidad = cantidad + ? " +
                              "WHERE id = ?";

    try (Connection conn = Datos.getConnection();
         PreparedStatement stmtInsert = conn.prepareStatement(queryInsert);
         PreparedStatement stmtUpdateDetalle = conn.prepareStatement(queryUpdateDetalle);
         PreparedStatement stmtUpdateStock = conn.prepareStatement(queryUpdateStock)) {
        
        // Insertar en condiciones_entrega
        stmtInsert.setInt(1, ordenId);
        stmtInsert.setInt(2, productoId);
        stmtInsert.setInt(3, piezasBuenas);
        stmtInsert.setInt(4, piezasReparables);
        stmtInsert.setInt(5, piezasDanadas);
        stmtInsert.setInt(6, piezasFaltantes);
        stmtInsert.setString(7, observaciones);
        stmtInsert.executeUpdate();

        // Actualizar detalle_pedidos restando piezas dañadas y faltantes
        stmtUpdateDetalle.setInt(1, piezasDanadas);
        stmtUpdateDetalle.setInt(2, piezasFaltantes);
        stmtUpdateDetalle.setInt(3, ordenId);
        stmtUpdateDetalle.setInt(4, productoId);
        stmtUpdateDetalle.executeUpdate();

        // Actualizar productos sumando piezas reparables al stock
        stmtUpdateStock.setInt(1, piezasReparables);
        stmtUpdateStock.setInt(2, productoId);
        stmtUpdateStock.executeUpdate();

        System.out.println("Devolución registrada correctamente.");
    } catch (SQLException e) {
        System.out.println("Error al registrar devolución: " + e.getMessage());
    }
}

private static boolean verificarOrdenYProducto(int ordenId, int productoId) {
    String query = "SELECT * FROM detalle_pedidos WHERE pedido_id = ? AND producto_id = ?";
    try (Connection conn = Datos.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, ordenId);
        stmt.setInt(2, productoId);
        ResultSet rs = stmt.executeQuery();
        return rs.next(); // Si hay resultados, el producto pertenece al pedido
    } catch (SQLException e) {
        System.out.println("Error al verificar producto en orden: " + e.getMessage());
        return false;
    }
}
private static boolean verificarOrdenExistente(int ordenId) {
    String query = "SELECT id FROM pedidos WHERE id = ?";
    try (Connection conn = Datos.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, ordenId);
        ResultSet rs = stmt.executeQuery();
        return rs.next(); // Si hay resultados, la orden existe
    } catch (SQLException e) {
        System.out.println("Error al verificar orden: " + e.getMessage());
        return false;
    }
}
private static void mostrarDevoluciones() {
      System.out.println("\n=== Lista de Devoluciones ===");
    
    String query = "SELECT * FROM condiciones_entrega";

    try (Connection conn = Datos.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Orden ID: " + rs.getInt("orden_id"));
            System.out.println("Piezas Buenas: " + rs.getInt("piezas_buenas"));
            System.out.println("Piezas Reparables: " + rs.getInt("piezas_reparables"));
            System.out.println("Piezas Dañadas: " + rs.getInt("piezas_danadas"));
            System.out.println("Piezas Faltantes: " + rs.getInt("piezas_faltantes"));
            System.out.println("Observaciones: " + rs.getString("observaciones"));
            System.out.println("-----------------------------");
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener devoluciones: " + e.getMessage());
    }
  }
private static void listarPiezasDevueltas() {
    System.out.println("\n=== Piezas Devueltas ===");

    String query = "SELECT ce.id, ce.orden_id, p.nombre AS producto, ce.piezas_buenas, ce.piezas_reparables, ce.piezas_danadas, ce.piezas_faltantes, ce.observaciones " +
                   "FROM condiciones_entrega ce " +
                   "JOIN productos p ON ce.producto_id = p.id";

    try (Connection conn = Datos.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        System.out.printf("%-5s %-10s %-20s %-10s %-10s %-10s %-10s %-30s\n", 
                          "ID", "Orden", "Producto", "Buenas", "Reparables", "Dañadas", "Faltantes", "Observaciones");
        System.out.println("-------------------------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-5d %-10d %-20s %-10d %-10d %-10d %-10d %-30s\n", 
                              rs.getInt("id"), rs.getInt("orden_id"), rs.getString("producto"), 
                              rs.getInt("piezas_buenas"), rs.getInt("piezas_reparables"), 
                              rs.getInt("piezas_danadas"), rs.getInt("piezas_faltantes"), 
                              rs.getString("observaciones"));
        }
    } catch (SQLException e) {
        System.out.println("Error al listar piezas devueltas: " + e.getMessage());
    }
  }
} 
