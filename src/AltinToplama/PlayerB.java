package AltinToplama;

public class PlayerB extends Player {
    PlayerB(int currentGold, Board board, int goldCostPerRound, int goldCostPerTargetting) {
        super(currentGold, board, goldCostPerRound, goldCostPerTargetting, "B");
        setLocatedAt(board.blocks[board.getBlockIndex(0, board.getHeight() - 1)]);
    }


    @Override
    void selectTarget() {
        if (getCurrentGold() >= goldCostPerTargeting) {
            updateStatus(getPlayerName() + " oyuncusu icin en karli kare secilecek.");
            setTargetBlock(getMostProfitableBlock()); //En karli kareyi hedef ata
        } else { //hamle yapmak icin altin yetersizse oyuncu pasifleştiriliyor
            updateStatus(getPlayerName() + " oyuncusunun altini hedef secmek icin yetersiz! Oyuncu eleniyor.");
            setPlayerActive(false);
        }
    }
}