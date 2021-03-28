package AltinToplama;

public class PlayerC extends Player {
    PlayerC(int currentGold, Board board, int goldCostPerRound, int goldCostPerTargetting) {
        super(currentGold, board, goldCostPerRound, goldCostPerTargetting, "C");
        setLocatedAt(board.blocks[board.getBlockIndex(board.getWidth() - 1, board.getHeight() - 1)]);
    }

    @Override
    void selectTarget() {
        if (getCurrentGold() >= goldCostPerTargeting) {
            //iki defa en yakin gizli altin aciga cikariliyor, ilki basarisizsa ikinci kez denenmiyor
            for (int i = 0; i < 2; i++) {
                if (!revealClosestHiddenGold())
                    break;
            }
            updateStatus(getPlayerName() + " oyuncusu icin en karli kare secilecek.");
            setTargetBlock(getMostProfitableBlock()); //en karli kare hedef seciliyor
        } else { //hamle yapmak icin altin yetersizse oyuncu pasifleştiriliyor
            updateStatus(getPlayerName() + " oyuncusunun altini hedef secmek icin yetersiz! Oyuncu eleniyor.");
            setPlayerActive(false);
        }
    }

    //en yakindaki gizli altini acan fonksiyon
    public boolean revealClosestHiddenGold() {
        Block closest = null;
        for (int i = 0; i < board.blocks.length; i++) {
            if (board.blocks[i].getContainsHiddenGold()) {
                if (closest == null || board.getRealBlockDistance(getLocatedAt(), closest) > board.getRealBlockDistance(getLocatedAt(), board.blocks[i])) {
                    closest = board.blocks[i];
                }
            }
        }
        if (closest != null) {
            updateStatus("C oyuncusu " + closest.toString() + " karesindeki altini ortaya cikardi! ");
            closest.revealHiddenGold();
            return true;
        } else { //closest nullsa acilacak gizli altin kalmamis
            updateStatus("C oyuncusunun acabilecegi gizli altin yok!");
            return false;
        }
    }
}