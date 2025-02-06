/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.velascirios;

/**
 *
 * @author isaac
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    public Menu(String role) {
        setTitle("Menú Principal - VelasCirios");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 5, 5));

        JButton btnVerProductos = new JButton("Ver productos");
        JButton btnRealizarPedido = new JButton("Realizar un pedido");
        JButton btnVerPedidos = new JButton("Ver pedidos");
        JButton btnRegistrarDevolucion = new JButton("Registrar devolución");
        JButton btnVerDevoluciones = new JButton("Ver devoluciones");
        JButton btnEliminarPedido = new JButton("Eliminar un pedido");
        JButton btnSalir = new JButton("Salir");

        btnVerProductos.addActionListener(e -> listarProductos());
        btnRealizarPedido.addActionListener(e -> realizarPedido());
        btnVerPedidos.addActionListener(e -> listarPedidos());
        btnRegistrarDevolucion.addActionListener(e -> registrarDevolucionPorProducto());
        btnVerDevoluciones.addActionListener(e -> listarDevoluciones());
        btnEliminarPedido.addActionListener(e -> eliminarPedido());
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnVerProductos);
        add(btnRealizarPedido);
        add(btnVerPedidos);
        add(btnRegistrarDevolucion);
        add(btnVerDevoluciones);
        if (role.equals("admin")) {
            add(btnEliminarPedido);
        }
        add(btnSalir);

        setVisible(true);
    }

    private void listarProductos() {
        JOptionPane.showMessageDialog(this, "Lista de productos (implementar)");
    }

    private void realizarPedido() {
        JOptionPane.showMessageDialog(this, "Realizar pedido (implementar)");
    }

    private void listarPedidos() {
        JOptionPane.showMessageDialog(this, "Lista de pedidos (implementar)");
    }

    private void registrarDevolucionPorProducto() {
        JOptionPane.showMessageDialog(this, "Registrar devolución (implementar)");
    }

    private void listarDevoluciones() {
        JOptionPane.showMessageDialog(this, "Lista de devoluciones (implementar)");
    }

    private void eliminarPedido() {
        JOptionPane.showMessageDialog(this, "Eliminar pedido (implementar)");
    }
}