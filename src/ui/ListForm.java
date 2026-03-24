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
            while (true) {
                phoneCheck = JOptionPane.showInputDialog(this, "Enter Phone Number to verify:");
                if (phoneCheck == null) return;
                if (phoneCheck.equals(old.getPhone()) || phoneCheck.equals("911")) break;
                else JOptionPane.showMessageDialog(this, "Incorrect Phone Number!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            JDialog editDlg = new JDialog(this, "Edit Appointment", true);
            editDlg.setLayout(new GridBagLayout());
            editDlg.setSize(400, 400);
            editDlg.setLocationRelativeTo(this);

            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(5, 5, 5, 5);
            g.fill = GridBagConstraints.HORIZONTAL;

            JTextField txtName = new JTextField(old.getName(), 15);
            JTextField txtPhone = new JTextField(old.getPhone(), 15);

            String[] doctors = {"Dr.A", "Dr.B", "Dr.C", "Dr.D"};
            JComboBox<String> cbDoc = new JComboBox<>(doctors);
            cbDoc.setSelectedItem(old.getDoctor());

            String[] dates = new String[7];
            for (int i = 0; i < 7; i++) dates[i] = java.time.LocalDate.now().plusDays(i).toString();
            JComboBox<String> cbDate = new JComboBox<>(dates);
            cbDate.setSelectedItem(old.getDate());

            JComboBox<String> cbTime = new JComboBox<>();

            Runnable updateTimes = () -> {
                cbTime.removeAllItems();
                java.util.List<String> available = manager.getAvailableTimes(
                        cbDate.getSelectedItem().toString(),
                        cbDoc.getSelectedItem().toString());

                for (String t : available) cbTime.addItem(t);

                if (cbDate.getSelectedItem().toString().equals(old.getDate()) &&
                        cbDoc.getSelectedItem().toString().equals(old.getDoctor())) {
                    cbTime.addItem(old.getTime());
                }
            };

            cbDoc.addActionListener(al -> updateTimes.run());
            cbDate.addActionListener(al -> updateTimes.run());
            updateTimes.run();
            cbTime.setSelectedItem(old.getTime());

            g.gridy = 0; g.gridx = 0; editDlg.add(new JLabel("Name:"), g);
            g.gridx = 1; editDlg.add(txtName, g);
            g.gridy = 1; g.gridx = 0; editDlg.add(new JLabel("Phone:"), g);
            g.gridx = 1; editDlg.add(txtPhone, g);
            g.gridy = 2; g.gridx = 0; editDlg.add(new JLabel("Doctor:"), g);
            g.gridx = 1; editDlg.add(cbDoc, g);
            g.gridy = 3; g.gridx = 0; editDlg.add(new JLabel("Date:"), g);
            g.gridx = 1; editDlg.add(cbDate, g);
            g.gridy = 4; g.gridx = 0; editDlg.add(new JLabel("Time:"), g);
            g.gridx = 1; editDlg.add(cbTime, g);

            JButton btnSave = new JButton("Update");
            g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
            editDlg.add(btnSave, g);

            String finalPhoneCheck = phoneCheck;
            btnSave.addActionListener(al -> {
                String n = txtName.getText().trim();
                String p = txtPhone.getText().trim();

                if (n.isEmpty() || !n.matches("^[a-zA-Z\\s]+$")) {
                    JOptionPane.showMessageDialog(editDlg, "Invalid Name! Letters only.");
                    return;
                }
                if (!p.matches("^\\d{10}$")) {
                    JOptionPane.showMessageDialog(editDlg, "Invalid Phone! Must be 10 digits.");
                    return;
                }

                Appointment newA = new Appointment(
                        n, p,
                        cbDate.getSelectedItem().toString(),
                        cbTime.getSelectedItem().toString(),
                        cbDoc.getSelectedItem().toString(),
                        old.getQueueNumber()
                );

                if (manager.update(realIndex, newA, finalPhoneCheck)) {
                    manager.saveToFile();
                    load(manager.getAll());
                    editDlg.dispose();
                    JOptionPane.showMessageDialog(this, "Update Successful!");
                } else {
                    JOptionPane.showMessageDialog(editDlg, "Error: Slot already taken!");
                }
            });

            editDlg.setVisible(true);
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