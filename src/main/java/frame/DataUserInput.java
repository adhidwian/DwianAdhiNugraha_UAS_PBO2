package frame;

import helpers.Koneksi;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataUserInput extends JFrame{
    private JTextField idTextField;
    private JTextField namaUserTextField;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JButton simpanDataUserButton;
    private JButton batalButton;
    private JPanel mainPanel;
    private int id;

    public void setId(int id){
        this.id = id;
    }

    public DataUserInput(){
        batalButton.addActionListener(e -> {
            dispose();
        });

        simpanDataUserButton.addActionListener(e -> {
            String nm_user = namaUserTextField.getText();
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            if (nm_user.equals("") && username.equals("") && password.equals("")){
                JOptionPane.showMessageDialog(null,
                        "Isi Kolom Data User!!!",
                        "Validasi data kosong", JOptionPane.WARNING_MESSAGE);
                namaUserTextField.requestFocus();
                return;
            }
            Connection c = Koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String cekSQL = "SELECT * FROM user_usaha WHERE nm_user = ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1, nm_user);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,
                                "Data sama sudah ada");
                    } else {
                        String insertSQL = "INSERT INTO user_usaha VALUES (NULL, ?, ?, ?)";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1, nm_user);
                        ps.setString(2, username);
                        ps.setString(3, password);
                        ps.executeUpdate();
                        dispose();
                    }
                } else {
                    String cekSQL = "SELECT * FROM user_usaha WHERE nm_user = ? AND id !=?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1, nm_user);
                    ps.setInt(2, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,
                                "Data sama sudah ada");
                    } else {
                        String updateSQL = "UPDATE user_usaha SET nm_user = ?, username = ?, password = ? WHERE id = ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1, nm_user);
                        ps.setString(2, username);
                        ps.setString(3, password);
                        ps.setInt(4, id);
                        ps.executeUpdate();
                        dispose();
                    }
                }
            } catch (SQLException ex){
                throw new RuntimeException(ex);
            }
        });




        init();
    }

    public void isiKomponen(){
        Connection c = Koneksi.getConnection();
        String findSQL = "SELECT * FROM user_usaha WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namaUserTextField.setText(rs.getString("nm_user"));
                usernameTextField.setText(rs.getString("username"));
                passwordTextField.setText(rs.getString("password"));
                }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Tambah/Ubah Data User");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
