package AltinToplama;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    final JPanel[] blocks; //board classindaki blocks ile ayni degil, bu o bloklarin arayuzdeki panellerini tutuyor
    final Board board;
    final JPanel boardPanel; //oyun tahtasi
    private JPanel playersPanel; //oyuncu bilgilerini gosteren panel
    final JList<String> infoList; //durum guncellemelerini tutan liste
    final JScrollPane sp; //durum guncellemelerini tutan liste icin scroll
    final JButton endButton; //oyun bittiginde etkinlesen bitir butonu

    GamePanel(Board board) {
        setLayout(null);
        setBackground(new Color(25, 84, 150));
        this.board = board;
        blocks = new JPanel[board.width * board.height];
        boardPanel = createBoardPanel();
        playersPanel = createPlayersPanel();
        add(boardPanel);
        add(playersPanel);
        sp = new JScrollPane();
        sp.setBounds(751, 190, 448, 515);
        infoList = new JList<>(board.statusTexts);
        infoList.setForeground(new Color(25, 84, 150));
        sp.setViewportView(infoList);
        sp.setHorizontalScrollBar(null);
        add(sp);
        endButton = createEndButton();
        add(endButton);
        updateBlocks();
    }

    //oyun sonunda kullanici statlari tablosundaki labellar icin fonksiyon
    public JLabel createFinalStatsLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(25, 84, 150));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    //oyun sonunda bir oyuncu icin stat tablosu donduren fonksiyon
    public JPanel createFinalStatsPanel(Player player, int x) {
        JPanel panel = new JPanel();
        panel.setForeground(new Color(25, 84, 150));
        panel.setBackground(Color.white);
        panel.setBounds(x, 120, 150, 120);
        panel.setLayout(new GridLayout(7, 1));
        JLabel header = createFinalStatsLabel("Oyuncu " + player.getPlayerName());
        panel.add(header);
        panel.add(createFinalStatsLabel("-------"));
        panel.add(createFinalStatsLabel("Toplam Adim: " + player.getTotalMoves()));
        panel.add(createFinalStatsLabel("Harcanan Altin: " + player.getGoldSpent()));
        panel.add(createFinalStatsLabel("Kasadaki Altin: " + player.getCurrentGold()));
        panel.add(createFinalStatsLabel("Toplanan Altin: " + player.getGoldCollected()));
        return panel;
    }

    //bitir butonu tiklandiginda etkinlesen fonksiyon
    public void onEnd() {
        boardPanel.setVisible(false);
        playersPanel.setVisible(false);
        sp.setVisible(false);
        sp.setBounds(250, 300, 700, 350);
        sp.setVisible(true);
        endButton.setVisible(false);
        add(createFinalStatsPanel(board.playerA, 250));
        add(createFinalStatsPanel(board.playerB, 433));
        add(createFinalStatsPanel(board.playerC, 616));
        add(createFinalStatsPanel(board.playerD, 800));
    }

    //bitir butonunu donduren fonksiyon
    public JButton createEndButton() {
        JButton button = new JButton("Bitir");
        button.setBounds(1050, 77, 100, 50);
        button.setForeground(new Color(25, 84, 150));
        button.addActionListener(e -> onEnd()); //butona tiklanince onEnd fonksiyonunu cagir
        button.setBackground(Color.white);
        button.setEnabled(false); //basta buton deaktif olsun
        return button;
    }

    //oyuncu bilgilerini gosteren paneli donduren fonksiyon
    public JPanel createPlayersPanel() {
        JPanel panel = new JPanel();
        panel.setBounds(760, 25, 240, 150);
        panel.setLayout(new GridLayout(2, 2));
        panel.add(createPlayerPanel(board.playerA, Color.white));
        panel.add(createPlayerPanel(board.playerB, Color.lightGray));
        panel.add(createPlayerPanel(board.playerD, Color.lightGray));
        panel.add(createPlayerPanel(board.playerC, Color.white));
        return panel;
    }

    //oyuncu bilgisi tablosundaki yazilari donduren fonksiyon
    public JLabel playerInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(25, 84, 150));
        return label;
    }

    //her bir oyuncunun bilgi panelini donduren fonksiyon
    public JPanel createPlayerPanel(Player player, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.setBackground(color);
        panel.add(playerInfoLabel(" Oyuncu:"));
        panel.add(playerInfoLabel(player.getPlayerName()));
        panel.add(playerInfoLabel(" Altin:"));
        panel.add(playerInfoLabel("" + player.getCurrentGold()));
        panel.add(playerInfoLabel(" Durum:"));
        panel.add(playerInfoLabel("" + (player.isPlayerActive() ? "Aktif" : "Pasif")));
        return panel;
    }

    //oyun tahtasini donduren fonksiyon
    public JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setBounds(50, 10, 700, 700);
        boardPanel.setBackground(new Color(25, 84, 150));
        int index = 0;

        /*kareler disinda o karelerin numarasini gosteren yazilar da olacagi icin
        hem satir hem de sutun sayisinin 1 fazlasi aliniyor*/
        for (int i = 0; i < board.width + 1; i++) {
            for (int j = 0; j < board.height + 1; j++) {
                JPanel block = new JPanel();
                block.setBackground(new Color(25, 84, 150));
                if (i == 0 || j == 0) { //Eger ki i veya j sifir ise bu bir etikettir
                    JLabel text = new JLabel("" + (i == 0 && j == 0 ? "" : i == 0 ? "j" + j : "i" + i));
                    text.setForeground(Color.white);
                    block.add(text);
                } else { //eger i veya j sifir degilse bu gercek karedir ve blocks arrayine eklenir
                    block.setBackground(Color.white);
                    block.setBorder(BorderFactory.createRaisedBevelBorder());
                    blocks[index++] = block;
                }
                boardPanel.add(block);
            }
        }
        //GridLayout kullanilarak farkli sayida kare girildiginde arayuzdeki yerlesim icin ugras geregi kalkiyor
        boardPanel.setLayout(new GridLayout(board.width + 1, board.height + 1));
        return boardPanel;
    }

    //oyunun o anki durumuna gore arayuzu guncelleyen fonksiyon
    public void updateBlocks() {
        //tum karelerin ici temizleniyor ve layoutlari sifirlaniyor
        for (JPanel block : blocks) {
            block.removeAll();
            block.setLayout(new GridBagLayout());
        }

        for (Player player : board.players) { //tum oyuncularda gez
            //oyuncu aktifse bulundugu kareye buyuk yaziyla adini yaz
            if (player.isPlayerActive())
                blocks[player.getLocatedAt().index].add(getPlayerLabel(player.getPlayerName() + " "));

            //oyuncunun hedefi varsa hedefledigi kareye kucuk yaziyla adini yaz
            if (player.getTargetBlock() != null)
                blocks[player.getTargetBlock().index].add(getTargetLabel(player.getPlayerName() + " "));

        }

        //icinde altin bulunan karelere altin etiketi ekle
        for (int i = 0; i < blocks.length; i++) {
            if (board.blocks[i].getContainsGold()) {
                blocks[i].add(getGoldLabel(board.blocks[i].getGoldAmount(), board.blocks[i].isRevealedLater()));
            }
        }


        JPanel newPlayersPanel = createPlayersPanel(); //yeni bir oyuncu bilgi paneli yarat
        remove(playersPanel); //eski paneli kaldir
        add(newPlayersPanel); //yerine yenisini ekle
        setVisible(false);
        setVisible(true);
        playersPanel = newPlayersPanel;
    }

    //oyuncularin bulundugu karelere adi yazilirkenki etiketi yaratan fonksiyon
    public JLabel getPlayerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(25, 84, 150));
        label.setFont(new Font(null, Font.BOLD, label.getFont().getSize()));
        return label;
    }

    //altin etiketi yaratan fonksiyon, altin bastan aciksa kirmizi, sonradan acilmissa magenta yaratir
    public JLabel getGoldLabel(int amount, boolean isRevealedLater) {
        JLabel label = new JLabel("" + amount);
        label.setForeground(isRevealedLater ? Color.MAGENTA : Color.red);
        label.setFont(new Font(null, Font.BOLD, label.getFont().getSize()));
        return label;
    }

    //hedef etiketi yaratan fonksiyon
    public JLabel getTargetLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);
        label.setFont(new Font(null, Font.BOLD, 7));
        return label;
    }
}
