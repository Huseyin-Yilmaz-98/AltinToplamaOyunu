package AltinToplama;

import javax.swing.*;
import java.util.*;

public class Board {
    final int width, height, goldPercentage, hiddenGoldPercentage, initialGold;
    final int movesPerRound;
    public Block[] blocks; //tum bloklari icinde tutan array
    final Player playerA, playerB, playerC, playerD;
    public GamePanel panel;
    final int sleepDuration; //her aksiyon arasi bekleme suresi (ms)
    final Player[] players; //for looplarini basitlestirmek icin tum playerlari icinde tutan array
    final DefaultListModel<String> statusTexts; //oyundaki olaylar bu listeye ekleniyor ve arayuzde gosteriliyor

    public Board(int width, int height, int goldPercentage, int hiddenGoldPercentage, int movesPerRound, int initialGold, int[] playerPrefs, int sleepDuration) {
        this.width = width;
        this.height = height;
        this.goldPercentage = goldPercentage;
        this.hiddenGoldPercentage = hiddenGoldPercentage;
        this.movesPerRound = movesPerRound;
        this.initialGold = initialGold;
        this.sleepDuration = sleepDuration;
        statusTexts = new DefaultListModel<>();
        createBlocks();
        playerA = new PlayerA(initialGold, this, playerPrefs[0], playerPrefs[1]);
        playerB = new PlayerB(initialGold, this, playerPrefs[2], playerPrefs[3]);
        playerC = new PlayerC(initialGold, this, playerPrefs[4], playerPrefs[5]);
        playerD = new PlayerD(initialGold, this, playerPrefs[6], playerPrefs[7]);
        players = new Player[]{playerA, playerB, playerC, playerD};
    }

    public void createBlocks() {
        //tahtadaki bloklarin objelerini tutacak array tanimlaniyor
        blocks = new Block[width * height];
        int index = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //her blokun objesi yaratilip arraye ekleniyor
                blocks[index] = new Block(index, i, j);
                index++;
            }
        }
        //karistirma islemi icin blocks arrayi bir listeye kopyalaniyor
        List<Block> blockList = new ArrayList<>(Arrays.asList(blocks));

        //koseler oyunculari tutacagi icin bu listeden siliniyor
        blockList.remove(blocks[getBlockIndex(0, 0)]);
        blockList.remove(blocks[getBlockIndex(0, height - 1)]);
        blockList.remove(blocks[getBlockIndex(width - 1, height - 1)]);
        blockList.remove(blocks[getBlockIndex(width - 1, 0)]);

        //liste karistiriliyor
        Collections.shuffle(blockList);

        //toplam altin sayisi uzerinden belirtilen yuzdede altin ve gizli altin sayilari bulunuyor
        int totalGold = (int) (width * height * ((float) goldPercentage / 100));
        int hiddenGold = (int) (totalGold * ((float) hiddenGoldPercentage / 100));
        Random rand = new Random();

        //listemiz karisik sirada oldugu icin 0dan baslayarak indekslere altin atansa bile rastgele olacak
        //altin sayilari ise 1'den 4'e kadar rastgele sayi uretilip 5 ile carpilarak belirleniyor
        for (int i = 0; i < hiddenGold; i++) {
            blockList.get(i).setContainsHiddenGold(true);
            blockList.get(i).setGoldAmount((rand.nextInt(4) + 1) * 5);
        }
        for (int i = hiddenGold; i < totalGold; i++) {
            blockList.get(i).setContainsGold(true);
            blockList.get(i).setGoldAmount((rand.nextInt(4) + 1) * 5);
        }

        /*arrayden olusturdugumuz liste objeleri tuttugu icin listede karisik siradaki objelerde yaptigimiz islemler
        arrayde de gecerli oldu ama arrayi karistirmadigimiz icin sirasi da bozulmamis oldu*/
    }

    //aldigi durum guncellemesini konsola yazdirir ve arayuzde gosterilen listeye ekler
    public void updateStatus(String text) {
        System.out.println(text);
        statusTexts.addElement(text);
        panel.infoList.ensureIndexIsVisible(panel.infoList.getModel().getSize() - 1); //scrollu dibe getir
    }

    //programin beklemesini saglayan fonksiyon, constructorda aldigimiz sure kadar bekletiyor
    public void sleep() {
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    //sira ve sutun alip, o degerlere sahip olan blokun arraydeki indisini dondurur
    public int getBlockIndex(int row, int col) {
        for (int i = 0; i < width * height; i++) {
            if (blocks[i].row == row && blocks[i].col == col) {
                return i;
            }
        }
        return -1;
    }

    //iki blok objesi alip, aralarindaki uzakligi hamle sayisini baz alarak dondurur
    //ornegin uzakliklari 4 bloksa ve bir hamlede 3 adim atilabiliyorsa 2 dondurur (3+1)
    public int getBlockDistance(Block b1, Block b2) {
        int distance = getRealBlockDistance(b1, b2);
        return (distance / movesPerRound + (distance % movesPerRound == 0 ? 0 : 1));
    }

    //iki blok objesi alip aralarindaki gercek uzakligi adim bazinda dondurur
    public int getRealBlockDistance(Block b1, Block b2) {
        return Math.abs(b1.row - b2.row) + Math.abs(b1.col - b2.col);
    }
}
