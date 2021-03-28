package AltinToplama;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

abstract class Player {
    private int currentGold; //oyuncunun anlik altini
    final int goldCostPerRound; //hamle altin bedeli
    final int goldCostPerTargeting; //hedef belirleme altin bedeli
    private Block locatedAt; //oyuncunun bulundugu kare
    private Block targetBlock; //oyuncunun hedefledigi kare, eger o an bir hedef yoksa null'dır
    final Board board; //oyun tahtasi
    private int totalMoves; //atilan adim sayisi
    private int goldSpent; //harcanan altin miktari
    private int goldCollected; //toplanan altin miktari
    private boolean isPlayerActive; //oyuncunun hala oyunda olup olmama durumu
    final String playerName;
    final ArrayList<String> statusTexts; //hareket gecmisi

    Player(int currentGold, Board board, int goldCostPerRound, int goldCostPerTargeting, String playerName) {
        this.currentGold = currentGold;
        this.board = board;
        this.playerName = playerName;
        this.targetBlock = null;
        totalMoves = 0;
        goldSpent = 0;
        goldCollected = 0;
        this.goldCostPerRound = goldCostPerRound;
        this.goldCostPerTargeting = goldCostPerTargeting;
        isPlayerActive = true;
        statusTexts = new ArrayList<>();
    }

    //hareket gecmisini dosyaya kaydeden fonksiyon
    public void saveMovesToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("oyuncu" + playerName + ".txt"));
            for (String text : statusTexts) {
                writer.write(text + "\n");
            }
            writer.close();
            board.updateStatus(playerName + " oyuncusunun hareket kayitlari dosyaya yazdirildi.");
        } catch (Exception e) {
            board.updateStatus(playerName + " oyuncusunun hareket kayitlari yazdirilamadi!");
            board.updateStatus("Hata sebebi: " + e.toString());
        }
    }

    //gelen durum guncellemesini once oyuncunun gecmisine ekleyip
    //daha sonra arayuze yazdirilmasi icin boarddaki updateStatus fonksiyonunu cagirir
    public void updateStatus(String text) {
        statusTexts.add(text);
        board.updateStatus(text);
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isPlayerActive() {
        return isPlayerActive;
    }

    public void setPlayerActive(boolean playerActive) {
        isPlayerActive = playerActive;
    }

    public void setLocatedAt(Block locatedAt) {
        this.locatedAt = locatedAt;
    }

    public void setTargetBlock(Block targetBlock) {
        this.targetBlock = targetBlock;
    }

    public Block getLocatedAt() {
        return locatedAt;
    }

    public int getCurrentGold() {
        return currentGold;
    }

    public Block getTargetBlock() {
        return targetBlock;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public int getGoldSpent() {
        return goldSpent;
    }

    public int getGoldCollected() {
        return goldCollected;
    }

    abstract void selectTarget(); // Hedef belirleyen fonksiyon, her oyuncuda farkli oldugu icin abstract

    //icinde altin olan en yakin kareyi donduren fonksiyon, eger altin olan kare yoksa null doner
    public Block getClosestBlockWithGold() {
        Block closest = null;
        for (int i = 0; i < board.blocks.length; i++) {
            if (board.blocks[i].getContainsGold()) {
                if (closest == null) { //eger bulunan ilk kareyse dogrudan en yakin ata
                    closest = board.blocks[i];
                } else {
                    if (board.getRealBlockDistance(getLocatedAt(), closest) > board.getRealBlockDistance(getLocatedAt(), board.blocks[i])) {
                        closest = board.blocks[i]; //daha once bulunan en yakindan daha yakinsa yerine ata
                    } else if (board.getRealBlockDistance(getLocatedAt(), closest) == board.getRealBlockDistance(getLocatedAt(), board.blocks[i])) {
                        if (board.blocks[i].getGoldAmount() > closest.getGoldAmount()) { //eger ikisi esit mesafedeyse altini fazla olani ata
                            closest = board.blocks[i];
                        }
                    }
                }
            }
        }
        if (closest == null) {
            updateStatus(getPlayerName() + " oyuncusunun secebilecegi bir kare bulunamadi.");
        }
        return closest;
    }

    //su anki konumdan bir kareye gidilirse elde edilecek kari donduren fonksiyon
    public int getBlockProfit(Block block) {
        int rounds = board.getBlockDistance(getLocatedAt(), block);
        return (block.getGoldAmount() - (rounds * goldCostPerRound));
    }

    //su anki konumdan gitmesi en karli kareyi donduren fonksiyon
    public Block getMostProfitableBlock() {
        Block closest = null;
        for (int i = 0; i < board.blocks.length; i++) {
            if (board.blocks[i].getContainsGold()) {
                if (closest == null) { //bulunan ilk altin olan kareyse dogrudan en karli olarak al
                    closest = board.blocks[i];
                } else if (getBlockProfit(closest) < getBlockProfit(board.blocks[i])) { //daha once bulunandan daha karliysa yerine yaz
                    closest = board.blocks[i];
                }
            }
        }
        if (closest == null) {
            updateStatus(getPlayerName() + " oyuncusunun secebilecegi bir kare bulunamadi.");
        }
        return closest;
    }

    //oyuncunun o raundda yapacagi islemleri gerceklesiren fonksiyon
    public boolean playRound() {
        //hedef yoksa veya hedefteki altin alinmissa hedef secme fonksiyonunu cagirir
        if (targetBlock == null || !targetBlock.getContainsGold()) {
            targetBlock = null;
            selectTarget();
            //hedef basariyla secilmisse hedef secme altinini duser
            if (targetBlock != null) {
                updateStatus(getPlayerName() + " oyuncusu hedef belirleme icin odeme yapti, yeni altini " + currentGold + ".");
                updateStatus(board.getRealBlockDistance(getLocatedAt(), targetBlock) + " adim (" + board.getBlockDistance(getLocatedAt(), targetBlock) + " hamle) uzaktaki, " + getBlockProfit(targetBlock) + " kar saglayacak " + targetBlock.toString() + " karesi secildi.");
                currentGold -= goldCostPerTargeting;
                goldSpent += goldCostPerTargeting;
            }
        }

        //arayuzu gunceller
        board.panel.updateBlocks();
        board.sleep();

        //eger hamle yapmaya altin yetmiyorsa oyuncu oyundan alinir
        if (currentGold < goldCostPerRound) {
            updateStatus(getPlayerName() + " oyuncusunun hamle yapacak altini yok! Oyuncu eleniyor.");
            setPlayerActive(false);
            targetBlock = null;
            return false;
        }
        //eger hedef hala null ise ya secilecek hedef yoktur ya da hedef secmeye altin yetmiyor, null degilse oynanir
        else if (targetBlock != null) {
            currentGold -= goldCostPerRound;
            goldSpent += goldCostPerRound;
            updateStatus(getPlayerName() + " oyuncusu hamle icin odeme yapti, yeni altini " + currentGold + ".");
            for (int i = 0; i < board.movesPerRound; i++) {
                Block prevBlock = locatedAt; //bulundugumuz blok kaydediliyor

                //hedefe dogru bir adim ilerleniyor
                if (locatedAt.row < targetBlock.row) {
                    locatedAt = board.blocks[board.getBlockIndex(locatedAt.row + 1, locatedAt.col)];
                } else if (locatedAt.row > targetBlock.row) {
                    locatedAt = board.blocks[board.getBlockIndex(locatedAt.row - 1, locatedAt.col)];
                } else if (locatedAt.col < targetBlock.col) {
                    locatedAt = board.blocks[board.getBlockIndex(locatedAt.row, locatedAt.col + 1)];
                } else if (locatedAt.col > targetBlock.col) {
                    locatedAt = board.blocks[board.getBlockIndex(locatedAt.row, locatedAt.col - 1)];
                }

                //Eger hareket edilmisse atilan adim sayisina ekleniyor
                if (locatedAt != prevBlock) {
                    totalMoves += 1;
                    updateStatus(getPlayerName() + " oyuncusu " + prevBlock.toString() + " karesinden " + locatedAt.toString() + " karesine ilerledi.");
                }

                //Eger hedef kareye gelinmisse altini aliniyor ve hedef siliniyor
                if (getLocatedAt() == getTargetBlock()) {
                    updateStatus(getPlayerName() + " oyuncusu " + getLocatedAt().toString() + " karesindeki " + getTargetBlock().getGoldAmount() + " altini aldi.");
                    currentGold += targetBlock.getGoldAmount();
                    goldCollected += targetBlock.getGoldAmount();
                    targetBlock.setGoldAmount(0);
                    targetBlock.setContainsGold(false);
                    targetBlock = null;
                    break;
                }

                //Eger bulunulan karede gizli altin varsa aciga cikariliyor
                if (locatedAt.getContainsHiddenGold()) {
                    updateStatus(getPlayerName() + " oyuncusu " + getLocatedAt().toString() + " karesindeki " + getLocatedAt().getGoldAmount() + " gizli altini ortaya cikardi.");
                    locatedAt.revealHiddenGold();
                }
                board.panel.updateBlocks();
                board.sleep();
            }
            return true;
        }
        //Hedef secme basarisiz olmussa hamle basarisiz diye false donduruluyor
        else {
            return false;
        }
    }

}

