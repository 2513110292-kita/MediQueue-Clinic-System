package ui;

import javax.swing.*;
import java.awt.*;
import model.*;
import java.time.LocalDate;

public class AddForm extends JFrame {

    public AddForm(AppointmentManager manager) {

        setTitle("Add Appointment");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color primary = new Color(0, 153, 204);
        Color bg = new Color(245, 250, 255);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(bg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Clinic Appointment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(primary);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        JTextField name = new JTextField();
        JTextField phone = new JTextField();

        String[] doctors = {"Dr.A", "Dr.B", "Dr.C", "Dr.D"};
        JComboBox<String> cbDoctor = new JComboBox<>(doctors);

        String[] dates = new String[7];
        for (int i = 0; i < 7; i++) {
            dates[i] = LocalDate.now().plusDays(i).toString();
        }
        JComboBox<String> cbDate = new JComboBox<>(dates);

        JComboBox<String> cbTime = new JComboBox<>();

        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        name.setFont(font);
        phone.setFont(font);
        cbDoctor.setFont(font);
        cbDate.setFont(font);
        cbTime.setFont(font);

        addField(panel, gbc, 1, "Name:", name);
        addField(panel, gbc, 2, "Phone:", phone);
        addField(panel, gbc, 3, "Doctor:", cbDoctor);
        addField(panel, gbc, 4, "Date:", cbDate);
        addField(panel, gbc, 5, "Time:", cbTime);

        JButton save = new JButton("Book Now");
        save.setBackground(primary);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(save, gbc);

        add(panel);

        Runnable update = () -> {
            cbTime.removeAllItems();
            for (String t : manager.getAvailableTimes(
                    cbDate.getSelectedItem().toString(),
                    cbDoctor.getSelectedItem().toString())) {
                cbTime.addItem(t);
            }
        };

        cbDate.addActionListener(e -> update.run());
        cbDoctor.addActionListener(e -> update.run());
        update.run();

        save.addActionListener(e -> {
            String n = name.getText().trim();
            String p = phone.getText().trim();

            if (n.isEmpty() || !n.matches("^[a-zA-Z\\s]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid Name! Please use letters only.");
                return;
            }

            if (!p.matches("^\\d{10}$")) {
                JOptionPane.showMessageDialog(this, "Invalid Phone! Please enter exactly 10 digits.");
                return;
            }

            if (manager.isDuplicatePerson(n, p)) {
                JOptionPane.showMessageDialog(this, "You already have an existing appointment!");
                return;
            }

            String doctor = cbDoctor.getSelectedItem().toString();
            String date = cbDate.getSelectedItem().toString();
            String time = cbTime.getSelectedItem().toString();

            int queue = manager.getNextQueue(date, doctor);

            Appointment a = new Appointment(n, p, date, time, doctor, queue);

            if (manager.add(a)) {
                manager.saveToFile();
                JOptionPane.showMessageDialog(this, "Booking Successful! Queue Number: " + queue);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "This time slot is already taken!");
            }
        });

        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}