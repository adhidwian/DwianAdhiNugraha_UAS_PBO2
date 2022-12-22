package frame;

import com.sun.tools.javac.Main;
import helpers.Koneksi;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.prefs.Preferences;

public class DataUsahaViewFrame extends JFrame {

    private JPanel mainPanel;
    private JTextField cariTextField;
    private JButton cariButton;
    private JTable viewTable;
    private JPanel buttonPanel;
    private JScrollPane viewScrollPane;
    private JPanel cariPanel;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton tutupButton;
    private JButton keluarButton;

    public BufferedImage getBufferedImage(Blob imageBlob){
        InputStream binaryStream = null;
        BufferedImage b = null;
        try {
            binaryStream = imageBlob.getBinaryStream();
            b = ImageIO.read(binaryStream);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return b;
    }
    public DataUsahaViewFrame(){
        tambahButton.addActionListener(e -> {
            DataUsahaInputFrame inputFrame = new DataUsahaInputFrame();
            inputFrame.setVisible(true);
        });

        ubahButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if (barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,"Pilih Data Dulu");
                return;
            }
            TableModel tm = viewTable.getModel();
            int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
            DataUsahaInputFrame inputFrame = new DataUsahaInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
            inputFrame.setVisible(true);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                isiTable();
            }
        });

        cariButton.addActionListener(e -> {
            if (cariTextField.getText().equals("")){
                JOptionPane.showMessageDialog(null,
                        "Isi kata kunci pencarian",
                        "Validasi kata kunci kosong",
                        JOptionPane.WARNING_MESSAGE);
                cariTextField.requestFocus();
                return;
            }
            Connection c = Koneksi.getConnection();
            String keyword = "%" + cariTextField.getText() + "%";
            String searchSQL = "SELECT * FROM data_usaha WHERE nm_pelaku_usaha like ?";
            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1, keyword);
                ResultSet rs = ps.executeQuery();
                DefaultTableModel dtm = (DefaultTableModel) viewTable.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[5];
                while (rs.next()) {
                    Icon icon = new ImageIcon(getBufferedImage(rs.getBlob("logo_usaha")));


                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("nm_pelaku_usaha");
                    row[2] = rs.getString("nm_usaha");
                    row[3] = rs.getString("alamat_usaha");
                    row[4] = icon;
                    dtm.addRow(row);
                }
            } catch (SQLException ex){
                throw new RuntimeException(ex);
            }
        });

        hapusButton.addActionListener(e -> {
            int barisTerpilih = viewTable.getSelectedRow();
            if (barisTerpilih < 0){
                JOptionPane.showMessageDialog(null, "Pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null,
                    "Yakin mau hapus?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );
            if (pilihan == 0){
                TableModel tm = viewTable.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
                Connection c = Koneksi.getConnection();
                String deleteSQL = "DELETE FROM data_usaha WHERE id = ?";
                try {
                    PreparedStatement ps = c.prepareStatement(deleteSQL);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                } catch (SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        });

        tutupButton.addActionListener(e -> {
            dispose();
        });

        batalButton.addActionListener(e -> {
            isiTable();
        });

        keluarButton.addActionListener(e -> {
            Preferences pref = Preferences.userRoot().node(Main.class.getName());
            pref.put("USER_ID","");
            dispose();
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
            lf.setSize(320,200);
            lf.setLocationRelativeTo(null);
        });



        init();
        isiTable();
    }

    private void init(){
        setContentPane(mainPanel);
        setTitle("Data Usaha");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTable(){
        Connection c = Koneksi.getConnection();
        String selectSQL = "SELECT * FROM data_usaha";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            String header[] = {"Id", "Nama Pelaku Usaha","Nama Usaha", "Alamat Usaha", "Logo Usaha"};
            DefaultTableModel dtm = new DefaultTableModel(header, 0){
                public Class getColumnClass(int column)
                {
                    return getValueAt(0, column).getClass();
                }
            };
            viewTable.setModel(dtm);
            viewTable.setPreferredScrollableViewportSize(viewTable.getPreferredSize());
            viewTable.setRowHeight(200);
            Object[] row = new Object[5];
            while (rs.next()){
                Icon icon = new ImageIcon(getBufferedImage(rs.getBlob("logo_usaha")));

                row[0] = rs.getInt("id");
                row[1] = rs.getString("nm_pelaku_usaha");
                row[2] = rs.getString("nm_usaha");
                row[3] = rs.getString("alamat_usaha");
                row[4] = icon;
                dtm.addRow(row);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
