package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import model.*;

public class ListForm extends JFrame {

    private AppointmentManager manager;
    private DefaultTableModel model;
    private JTable table;

    private int page = 0;
    private int size = 10;

    private boolean showPhone = false;

    public ListForm(AppointmentManager manager) {
        this.manager = manager;

        setTitle("Appointment List");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color primary = new Color(0, 153, 204);
        Color bg = new Color(245, 250, 255);

        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        model = new DefaultTableModel(
                new String[]{"Queue", "Name", "Phone", "Doctor", "Date", "Time"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setFont(font);
        table.setRowHeight(25);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(primary);
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);

        JTextField searchField = new JTextField(15);
        searchField.setFont(font);

        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, primary);

        ActionListener searchAction = e -> {
            page = 0;
            load(manager.search(searchField.getText()));
        };

        btnSearch.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        JPanel top = new JPanel();
        top.setBackground(bg);
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(btnSearch);

        JButton prev = new JButton("<<");
        JButton next = new JButton(">>");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton unlock = new JButton("Unlock");
        JButton lock = new JButton("Hide");

        styleButton(prev, Color.GRAY);
        styleButton(next, Color.GRAY);
        styleButton(edit, new Color(255, 153, 0));
        styleButton(delete, new Color(220, 53, 69));
        styleButton(unlock, new Color(40, 167, 69));
        styleButton(lock, new Color(108, 117, 125));

        JPanel bottom = new JPanel();
        bottom.setBackground(bg);
        bottom.add(prev);
        bottom.add(next);
        bottom.add(edit);
        bottom.add(delete);
        bottom.add(unlock);
        bottom.add(lock);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        load(manager.getAll());

        next.addActionListener(e -> {
            if ((page + 1) * size < manager.getAll().size()) {
                page++;
                load(manager.getAll());
            }
        });

        prev.addActionListener(e -> {
            if (page > 0) {
                page--;
                load(manager.getAll());
            }
        });

        unlock.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Master Password:");
            if (input != null && input.equals("911")) {
                showPhone = true;
                load(manager.getAll());
            } else if (input != null) {
                JOptionPane.showMessageDialog(this, "Invalid Password!");
            }
        });

        lock.addActionListener(e -> {
            showPhone = false;
            load(manager.getAll());
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item first.");
                return;
            }

            int realIndex = row + page * size;
            String phone = JOptionPane.showInputDialog("Enter Phone Number to Confirm:");

            if (phone == null || phone.trim().isEmpty()) return;

            if (manager.remove(realIndex, phone)) {
                manager.saveToFile();
                load(manager.getAll());
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed! Incorrect phone number.");
            }
        });

        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item first.");
                return;
            }

            int realIndex = row + page * size;
            Appointment old = manager.getAll().get(realIndex);

            String phoneCheck = "";
            boolean authenticated = false;

            while (true) {
                phoneCheck = JOptionPane.showInputDialog(this, "Enter Phone Number to verify:");
                if (phoneCheck == null) return;

                if (phoneCheck.equals(old.getPhone()) || phoneCheck.equals("911")) {
                    authenticated = true;
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect Phone Number! Please try again.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (authenticated) {
                String newDate = JOptionPane.showInputDialog("New Date:", old.getDate());
                if (newDate == null) return;

                String newTime = JOptionPane.showInputDialog("New Time:", old.getTime());
                if (newTime == null) return;

                String newDoctor = JOptionPane.showInputDialog("New Doctor:", old.getDoctor());
                if (newDoctor == null) return;

                Appointment newA = new Appointment(
                        old.getName(),
                        old.getPhone(),
                        newDate,
                        newTime,
                        newDoctor,
                        manager.getNextQueue(newDate, newDoctor)
                );

                if (manager.update(realIndex, newA, phoneCheck)) {
                    manager.saveToFile();
                    load(manager.getAll());
                    JOptionPane.showMessageDialog(this, "Update Successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed! The selected time slot might be taken.");
                }
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton btn, Color color){
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void load(java.util.List<Appointment> list) {
        model.setRowCount(0);

        int start = page * size;
        int end = Math.min(start + size, list.size());

        for (int i = start; i < end; i++) {
            Appointment a = list.get(i);

            model.addRow(new Object[]{
                    a.getQueueNumber(),
                    a.getName(),
                    (showPhone ? a.getPhone() : "****"),
                    a.getDoctor(),
                    a.getDate(),
                    a.getTime()
            });
        }
    }
}