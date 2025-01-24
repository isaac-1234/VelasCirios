package com.mycompany.velascirios;
import java.util.Scanner;

public class VelasCirios {
    public static void main(String[] args) {
        AccesoDatos accesoDatos = new AccesoDatos();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SISTEMA DE GESTIÓN DE PRODUCTOS ===");
            System.out.println("1. Añadir Producto");
            System.out.println("2. Listar Productos");
            System.out.println("3. Actualizar Producto");
            System.out.println("4. Eliminar Producto");
            System.out.println("5. Salir");
            System.out.print("Elige una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            try {
                switch (opcion) {
                    case 1:
                        // Añadir Producto
                        System.out.print("Ingrese el nombre del producto: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Ingrese la descripción: ");
                        String descripcion = scanner.nextLine();
                        System.out.print("Ingrese el precio: ");
                        double precio = scanner.nextDouble();
                        System.out.print("Ingrese la cantidad: ");
                        int cantidad = scanner.nextInt();
                        accesoDatos.addProduct(nombre, descripcion, precio, cantidad);
                        System.out.println("¡Producto añadido con éxito!");
                        break;

                    case 2:
                        // Listar Productos
                        accesoDatos.listProducts();
                        break;

                    case 3:
                        // Actualizar Producto
                        System.out.print("Ingrese el ID del producto a actualizar: ");
                        int idActualizar = scanner.nextInt();
                        scanner.nextLine(); // Consumir salto de línea
                        System.out.print("Ingrese el nuevo nombre: ");
                        String nuevoNombre = scanner.nextLine();
                        System.out.print("Ingrese la nueva descripción: ");
                        String nuevaDescripcion = scanner.nextLine();
                        System.out.print("Ingrese el nuevo precio: ");
                        double nuevoPrecio = scanner.nextDouble();
                        System.out.print("Ingrese la nueva cantidad: ");
                        int nuevaCantidad = scanner.nextInt();
                        accesoDatos.updateProduct(idActualizar, nuevoNombre, nuevaDescripcion, nuevoPrecio, nuevaCantidad);
                        System.out.println("¡Producto actualizado con éxito!");
                        break;

                    case 4:
                        // Eliminar Producto
                        System.out.print("Ingrese el ID del producto a eliminar: ");
                        int idEliminar = scanner.nextInt();
                        accesoDatos.deleteProduct(idEliminar);
                        System.out.println("¡Producto eliminado con éxito!");
                        break;

                    case 5:
                        // Salir
                        System.out.println("Saliendo... ¡Adiós!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Opción inválida. Intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }
}