package AltinToplama;

public class PlayerA extends Player {
    PlayerA(int currentGold, Board board, int goldCostPerRound, int goldCostPerTargetting) {
        super(currentGold, board, goldCostPerRound, goldCostPerTargetting, "A");
        setLocatedAt(board.blocks[board.getBlockIndex(0, 0)]);
    }

    @Override
    void selectTarget() {
        if (getCurrentGold() >= goldCostPerTargeting) {
            updateStatus(getPlayerName() + " oyuncusu icin en yakindaki kare secilecek.");
            setTargetBlock(getClosestBlockWithGold()); //en yakin kareyi hedef ata
        } else { //hamle yapmak icin altin yetersizse oyuncu pasifleştiriliyor
            updateStatus(getPlayerName() + " oyuncusunun altini hedef secmek icin yetersiz! Oyuncu eleniyor.");
            setPlayerActive(false);
        }
    }
}
