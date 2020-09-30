import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    static String windowsVersion = "";
    static String build = "";
    public static void main(String[] args) {
        Overlay ol = new Overlay("WindowsCopy", 420, 50);
        ol.add(new DrawCanvas());
        ol.setVisible(true);
        ol.setAlwaysOnTop(true);
        ol.startGameLoop();
        ol.setBackground(new Color(1f, 1f, 1f, 0f));

    }
}
class Overlay extends JFrame implements Runnable {
    private Thread th = null;
    public Overlay(String title, int x, int y) {
        super(title);
        JFrame.setDefaultLookAndFeelDecorated(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(x, y);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screenSize.width;
        int h = screenSize.height;
        setLocation(w-x, h-y-50);
        setUndecorated(true);
        setResizable(false);

        Runtime runtime = Runtime.getRuntime();
        Process process;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        String stdOutLine = null;

        try {
            process = runtime.exec("cmd.exe /c ver");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((stdOutLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(stdOutLine);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while getting Windows version");
        }
        Main.windowsVersion = Arrays.stream(stringBuilder.toString().split("\\."))
                .filter(s -> s.length() == 5 && s.matches("[0-9]+"))
                .findFirst()
                .get();
        Main.build = "ビルド "+Main.windowsVersion;
    }
    public synchronized void startGameLoop(){
        if ( th == null ) {
            th = new Thread(this);
            th.start();
        }
    }

    public synchronized void stopGameLoop(){
        if ( th != null ) {
            th = null;
        }
    }

    public void run(){
        while(th != null){
            try{
                Thread.sleep(500);
                repaint();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}

class DrawCanvas extends JPanel {
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        setBackground(new Color(0f, 0f, 0f, 0f));
        g.setFont(new Font("Yu Gothic UI", Font.TRUETYPE_FONT, 16));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics fontMetrics = g.getFontMetrics();
        g.drawString(""+System.getProperty("os.name"), 405-fontMetrics.stringWidth(""+System.getProperty("os.name")), 16);
        g.drawString(Main.build, 405-fontMetrics.stringWidth(Main.build), 32);
        g.drawString("この Windows のコピーは正規品ではありません", 405-fontMetrics.stringWidth("この Windows のコピーは正規品ではありません"), 48);
    }
}