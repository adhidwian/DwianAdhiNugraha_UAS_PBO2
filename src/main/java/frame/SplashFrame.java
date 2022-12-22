package frame;

import com.sun.tools.javac.Main;

import javax.swing.*;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public class SplashFrame extends JFrame{
    private JPanel mainPanel;
    private JLabel lbSplash;

    public SplashFrame() {
        setContentPane(mainPanel);
        setSize(438,438);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        lbSplash.setIcon(new ImageIcon("C:\\Users\\dwian\\IdeaProjects\\dwian_usaha\\splash.jpg"));



        Preferences pref = Preferences.userRoot().node(Main.class.getName());
        String userID = pref.get("USER_ID", "");
        System.out.println(userID);

        try {
            TimeUnit.SECONDS.sleep(2);
            dispose();
            if(userID.equals("")){
                setVisible(false);
                LoginFrame lf = new LoginFrame();
                lf.setVisible(true);
                lf.setSize(320,200);
                lf.setLocationRelativeTo(null);
            } else {
                setVisible(false);
                DataUsahaViewFrame mf = new DataUsahaViewFrame();
                mf.setVisible(true);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

}
