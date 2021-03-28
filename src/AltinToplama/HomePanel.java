package AltinToplama;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    final JFrame f;
    final JTextField widthField, heightField;
    final JTextField initialGoldField;
    final JTextField goldPercentageField;
    final JTextField hiddenGoldPercentageField;
    final JTextField movesPerRoundField;
    final JTextField sleepDurationField;
    final JTextField aPerRound, aPerTarget;
    final JTextField bPerRound, bPerTarget;
    final JTextField cPerRound, cPerTarget;
    final JTextField dPerRound, dPerTarget;
    JLabel warningText;

    HomePanel(JFrame f) {
        this.f = f;
        warningText = new JLabel("");
        warningText.setForeground(Color.white);
        warningText.setFont(new Font(null, Font.BOLD, 20));
        initialGoldField = new JTextField("200");
        goldPercentageField = new JTextField("20");
        hiddenGoldPercentageField = new JTextField("10");
        movesPerRoundField = new JTextField("3");
        sleepDurationField = new JTextField("1000");
        widthField = new JTextField("20");
        heightField = new JTextField("20");
        aPerRound = new JTextField("5");
        aPerTarget = new JTextField("5");
        bPerRound = new JTextField("5");
        bPerTarget = new JTextField("10");
        cPerRound = new JTextField("5");
        cPerTarget = new JTextField("15");
        dPerRound = new JTextField("5");
        dPerTarget = new JTextField("20");
        add(createGameSettingsPanel());
        add(createMargin(60, 0));
        add(createPlayerSettingsPanel1());
        add(createMargin(60, 0));
        add(createPlayerSettingsPanel2());
        add(createMargin(1300, 0));
        add(createMargin(0, 200));
        add(createButtonsPanel());
        add(createMargin(1000, 0));
        add(createMargin(0, 50));
        add(warningText);
        setBackground(new Color(25, 84, 150));
        setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));
    }

    //oyunu baslat butonuna basince calisan fonksiyon
    public void onSubmitPress() {
        warningText.setText("");
        int initialGold = getInteger(initialGoldField.getText());
        int goldPercentage = getInteger(goldPercentageField.getText());
        int hiddenGoldPercentage = getInteger(hiddenGoldPercentageField.getText());
        int movesPerRound = getInteger(movesPerRoundField.getText());
        int sleepDuration = getInteger(sleepDurationField.getText());
        int width = getInteger(widthField.getText());
        int height = getInteger(heightField.getText());
        int APerRound = getInteger(aPerRound.getText());
        int APerTarget = getInteger(aPerTarget.getText());
        int BPerRound = getInteger(bPerRound.getText());
        int BPerTarget = getInteger(bPerTarget.getText());
        int CPerRound = getInteger(cPerRound.getText());
        int CPerTarget = getInteger(cPerTarget.getText());
        int DPerRound = getInteger(dPerRound.getText());
        int DPerTarget = getInteger(dPerTarget.getText());
        if (!(goldPercentage > 0 && goldPercentage <= 100)) {
            warningText.setText("Altin yuzdesi gecersiz!");
            return;
        }
        if (!(hiddenGoldPercentage >= 0 && hiddenGoldPercentage <= 100)) {
            warningText.setText("Gizli altin yuzdesi gecersiz!");
            return;
        }
        if (initialGold < 1 || movesPerRound < 1 || APerRound < 1 || APerTarget < 1 || BPerRound < 1 || width < 1
                || BPerTarget < 1 || CPerRound < 1 || CPerTarget < 1
                || DPerRound < 1 || DPerTarget < 1 || height < 1 || sleepDuration < 1) {
            warningText.setText("Tum degerler 0'dan buyuk olmali!");
            return;
        }

        //eger tum degerler dogruysa bu paneli yok et, board ve gamePaneli yarat,
        //sonra da frame'e ekleyip oyun threadini calistir.
        setVisible(false);
        Board board = new Board(width, height, goldPercentage, hiddenGoldPercentage, movesPerRound, initialGold,
                new int[]{APerRound, APerTarget, BPerRound, BPerTarget, CPerRound, CPerTarget, DPerRound, DPerTarget}, sleepDuration);
        GamePanel gamePanel = new GamePanel(board);
        board.panel = gamePanel;
        f.add(gamePanel);
        GameThread t = new GameThread(board, gamePanel);
        t.start();
    }

    //aldigi stringi integer olarak donduren, integer degilse -1 donduren fonksiyon
    public static int getInteger(String s) {
        int number;
        try {
            number = Integer.parseInt(s);
        } catch (Exception e) {
            number = -1;
        }
        return number;
    }

    //ana sayfadaki butonlarin bulundugu paneli yaratip donduren fonksiyon
    public JPanel createButtonsPanel() {
        JPanel panel = new JPanel();
        JButton submitButton = createButton("Baslat");
        JButton exitButton = createButton("Cikis");
        exitButton.addActionListener(e -> System.exit(0));
        submitButton.addActionListener(e -> onSubmitPress());
        panel.add(submitButton);
        panel.add(createMargin(100, 0));
        panel.add(exitButton);
        panel.setBackground(new Color(25, 84, 150));
        return panel;
    }

    //ana sayfadaki butonlardan birini yaratip donduren fonksiyon
    public JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.white);
        button.setForeground(new Color(25, 84, 150));
        button.setFont(new Font(null, Font.BOLD, 16));
        return button;
    }

    //verilen width ve heighta gore bosluk yaratacak bir panel yaratip donduren fonksiyon
    public JPanel createMargin(int width, int height) {
        JPanel margin = new JPanel();
        margin.setBorder(BorderFactory.createEmptyBorder(height / 2, width / 2, height / 2, width / 2));
        margin.setBackground(new Color(25, 84, 150));
        return margin;
    }

    //oyun ayarlarinin bulundugu paneli donduren fonksiyon
    public JPanel createGameSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));
        panel.add(createLabel(" Baslangic Altini:"));
        panel.add(initialGoldField);
        panel.add(createLabel(" Hamle Basi Adim: "));
        panel.add(movesPerRoundField);
        panel.add(createLabel(" Altin Yuzdesi: "));
        panel.add(goldPercentageField);
        panel.add(createLabel(" Gizli Altin Yuzdesi:    "));
        panel.add(hiddenGoldPercentageField);
        panel.add(createLabel(" Yatay Kare Sayisi: "));
        panel.add(widthField);
        panel.add(createLabel(" Dikey Kare Sayisi: "));
        panel.add(heightField);
        panel.add(createLabel(" Bekleme Suresi (ms): "));
        panel.add(sleepDurationField);
        panel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
        panel.setBackground(new Color(25, 84, 150));
        return panel;
    }

    //ana sayfadaki yazilardan birini yaratip donduren fonksiyon
    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.white);
        return label;
    }

    //A ve B oyuncularinin ayar panelini yaratip donduren fonksiyon
    public JPanel createPlayerSettingsPanel1() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        panel.add(createLabel(" A Hamle Maliyeti:  "));
        panel.add(aPerRound);
        panel.add(createLabel(" A Hedef Maliyeti:   "));
        panel.add(aPerTarget);
        panel.add(createMargin(20, 5));
        panel.add(createMargin(20, 5));
        panel.add(createLabel(" B Hamle Maliyeti:  "));
        panel.add(bPerRound);
        panel.add(createLabel(" B Hedef Maliyeti:   "));
        panel.add(bPerTarget);
        panel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
        panel.setBackground(new Color(25, 84, 150));
        return panel;
    }

    //C ve D oyuncularinin ayar panelini yaratip donduren fonksiyon
    public JPanel createPlayerSettingsPanel2() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        panel.add(createLabel(" C Hamle Maliyeti:  "));
        panel.add(cPerRound);
        panel.add(createLabel(" C Hedef Maliyeti:   "));
        panel.add(cPerTarget);
        panel.add(createMargin(20, 5));
        panel.add(createMargin(20, 5));
        panel.add(createLabel(" D Hamle Maliyeti:  "));
        panel.add(dPerRound);
        panel.add(createLabel(" D Hedef Maliyeti:   "));
        panel.add(dPerTarget);
        panel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
        panel.setBackground(new Color(25, 84, 150));
        return panel;
    }

}
