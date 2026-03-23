package ui;

import javax.swing.*;
import java.awt.*;
import model.AppointmentManager;

public class MainMenuForm extends JFrame {

    private AppointmentManager manager = new AppointmentManager();

    private AddForm addForm;
    private ListForm listForm;

    public MainMenuForm() {

        manager.loadFromFile();

        setTitle("MediQueue");
        setSize(420, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color primary = new Color(0, 153, 204);
        Color bg = new Color(245, 250, 255);

        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel imageLabel;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/clinic.png"));
            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel = new JLabel("🏥");
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }

        gbc.gridy = 0;
        panel.add(imageLabel, gbc);

        JLabel title = new JLabel("MediQueue");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("Clinic Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 2;
        panel.add(subtitle, gbc);

        JButton add = new JButton("Add Appointment");
        JButton list = new JButton("View Appointments");

        styleButton(add, primary);
        styleButton(list, new Color(40, 167, 69));

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(add, gbc);

        gbc.gridy = 4;
        panel.add(list, gbc);

        add(panel);

        add.addActionListener(e -> {
            if (addForm == null || !addForm.isDisplayable()) {
                addForm = new AddForm(manager);
            } else {
                addForm.toFront();
            }
        });

        list.addActionListener(e -> {
            if (listForm == null || !listForm.isDisplayable()) {
                listForm = new ListForm(manager);
            } else {
                listForm.toFront();
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton btn, Color color){
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(220, 45));
    }
}