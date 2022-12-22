package frame;

import com.sun.tools.javac.Main;
import helpers.Koneksi;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginFrame extends JFrame{
    private JPanel mainPanel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JPanel buttonPanel;

    public LoginFrame(){
        loginButton.addActionListener(e -> {
            Connection c = Koneksi.getConnection();
            try {
                ResultSet rs = c.createStatement().executeQuery("SELECT * FROM user_usaha WHERE username='"+usernameTextField.getText()+"' AND password='"+String.valueOf(passwordField.getText())+"'");
                if (rs.next()){
                    Preferences pref = Preferences.userRoot().node(Main.class.getName());
                    pref.put("USER_ID","1");
                    DataUsahaViewFrame duvf = new DataUsahaViewFrame();
                    duvf.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Username atau Password Salah!!!");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        cancelButton.addActionListener(e -> {
            dispose();
        });

        init();
    }


    private void init(){
        setContentPane(mainPanel);
        setTitle("Login Form");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
