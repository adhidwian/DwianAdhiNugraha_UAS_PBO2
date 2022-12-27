package frame;

import helpers.Koneksi;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class DataUsahaInputFrame extends JFrame {
    private int id;
    private JPanel mainPanel;
    private JButton pilihLogoButton;
    private JLabel lbGambar;
    private JButton simpanButton;
    private JButton batalButton;
    private JTextField idTextField;
    private JTextField namapuTextField;
    private JTextField namauTextField;
    private JTextField alamatTextField;
    private JPanel buttonPanel;

    public void setId(int id){
        this.id = id;
    }

    public void isiKomponen(){
        Connection c = Koneksi.getConnection();
        String findSQL = "SELECT * FROM data_usaha WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                idTextField.setText(String.valueOf(rs.getInt("id")));
                namapuTextField.setText(rs.getString("nm_pelaku_usaha"));
                namauTextField.setText(rs.getString("nm_usaha"));
                alamatTextField.setText(rs.getString("alamat_usaha"));
                lbGambar.setIcon(new ImageIcon(getBufferedImage(rs.getBlob("logo_usaha"))));
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getBufferedImage(Blob imageBlob){
        InputStream binaryStream = null;
        BufferedImage b = null;
        try {
            binaryStream = imageBlob.getBinaryStream();
            b = ImageIO.read(binaryStream);
        } catch (SQLException | IOException ex){
            System.err.println("Error getBufferedImage : "+ex);
        }
        return b;
    }

    public DataUsahaInputFrame(){
        batalButton.addActionListener(e -> {
            dispose();
        });

        pilihLogoButton.addActionListener(e -> {


            JFileChooser jfile = new JFileChooser();
            jfile.setCurrentDirectory(new File(System.getProperty("user.home")));

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File", "jpg","png");
            jfile.addChoosableFileFilter(filter);

            int result = jfile.showSaveDialog(null);

            File selectedFile = jfile.getSelectedFile();
            String filename = selectedFile.getName();
            System.out.println(""+filename);

            if (filename.endsWith(".jpg")||filename.endsWith(".JPG")||filename.endsWith(".PNG")||filename.endsWith(".png")){
                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = selectedFile.getAbsolutePath();
                    ImageIcon myImage = new ImageIcon(path);

                    Image img = myImage.getImage();
                    Image newImage = img.getScaledInstance(lbGambar.getWidth(), lbGambar.getHeight(), Image.SCALE_SMOOTH);

                    ImageIcon image = new ImageIcon(newImage);
                    lbGambar.setIcon(image);

                    simpanButton.addActionListener(e1 -> {
                        String nm_pelaku_usaha = namapuTextField.getText();
                        String nm_usaha = namauTextField.getText();
                        String alamat_usaha = alamatTextField.getText();
                        FileInputStream fis = null;
                        Connection c = Koneksi.getConnection();


                        PreparedStatement pst;
                        try {
                            if (id == 0) {
                                fis = new FileInputStream(path);
                                String cekSQL = "SELECT * FROM data_usaha WHERE nm_pelaku_usaha = ?";
                                pst = c.prepareStatement(cekSQL);
                                pst.setString(1, nm_pelaku_usaha);
                                ResultSet rs = pst.executeQuery();
                                if (rs.next()) {
                                    JOptionPane.showMessageDialog(null, "Data Sama Sudah Ada!");
                                } else {
                                    String insertSQL = "INSERT INTO data_usaha (id, nm_pelaku_usaha, nm_usaha, alamat_usaha, logo_usaha)" + "VALUES (NULL, ?, ?, ?, ?)";
                                    pst = c.prepareStatement(insertSQL);
                                    pst.setString(1, nm_pelaku_usaha);
                                    pst.setString(2, nm_usaha);
                                    pst.setString(3, alamat_usaha);
                                    pst.setBinaryStream(4, fis);
                                    pst.executeUpdate();
                                    dispose();
                                }
                            } else {
                                String cekSQL = "SELECT * FROM data_usaha WHERE nm_pelaku_usaha = ? AND id!=?";
                                pst = c.prepareStatement(cekSQL);
                                pst.setString(1, nm_pelaku_usaha);
                                pst.setInt(2,id);
                                ResultSet rs = pst.executeQuery();
                                if (rs.next()){
                                    JOptionPane.showMessageDialog(null,"Data Sama Sudah Ada!");
                                } else{
                                    fis = new FileInputStream(path);
                                    String updateSQL = "UPDATE data_usaha SET nm_pelaku_usaha = ?, nm_usaha = ?, alamat_usaha = ?, logo_usaha = ? WHERE id = ?";
                                    pst = c.prepareStatement(updateSQL);
                                    pst.setString(1, nm_pelaku_usaha);
                                    pst.setString(2, nm_usaha);
                                    pst.setString(3, alamat_usaha);
                                    pst.setBinaryStream(4, fis);
                                    pst.setInt(5,id);
                                    pst.executeUpdate();
                                    dispose();
                                }
                            }

                        } catch (Exception ex) {
                            System.out.println("" + ex);
                        }
                    });
                }else {
                    JOptionPane.showMessageDialog(rootPane,"Pilih File Logo", "Coba Lagi",1);
                }
                }
        });
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Usaha");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

}
