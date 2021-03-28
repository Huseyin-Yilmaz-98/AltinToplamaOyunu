package AltinToplama;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerD extends Player {
    PlayerD(int currentGold, Board board, int goldCostPerRound, int goldCostPerTargetting) {
        super(currentGold, board, goldCostPerRound, goldCostPerTargetting, "D");
        setLocatedAt(board.blocks[board.getBlockIndex(board.getWidth() - 1, 0)]);
    }

    //kiyaslanacak oyuncunun konumunu ve hedefi alir, o hedefe daha once gidilebilecekse true dondurur
    private boolean isReachableFirst(Block theirLocation, Block target) {
        return board.getBlockDistance(getLocatedAt(), target) <= board.getBlockDistance(theirLocation, target);
    }

    //diger oyuncularin mevcut veya tahmin edilen hedeflerinden ulasilabilir ve en karli olani dondurur
    private Block getTargetOfAnotherPlayer() {
        Block target = null;

        //kolayca tum oyuncularda gezmek icin diger oyuncularin hepsi arraye aliniyor
        Player[] players = new Player[]{board.playerA, board.playerB, board.playerC};
        updateStatus("");
        updateStatus("D oyuncusu, diger oyuncularin hedeflerini inceliyor...");

        //bir kareyi birden fazla oyuncu hedef alabilecegi icin hedeflenen her kareye karsilik
        //onu hedefleyen oyunculari arraylist halinde tutacak bir hashmap tanimlaniyor
        HashMap<Block, ArrayList<Player>> otherTargets = new HashMap<>();

        for (Player player : players) {
            if (player.isPlayerActive()) {
                Block otherTarget = null;
                if (player.getTargetBlock() == null) {//eger kullanicinin hedefi yoksa sececegi kareyi tahmin et
                    otherTarget = player.getPlayerName().equals("A") ? player.getClosestBlockWithGold() : player.getMostProfitableBlock();
                    if (otherTarget == null)
                        continue;
                    else
                        updateStatus(player.getPlayerName() + " oyuncusunun " + otherTarget + " karesini sececegi tahmin edildi.");
                } else { //eget kullanicinin zaten hedefi varsa o hedefi al
                    otherTarget = player.getTargetBlock();
                    updateStatus(player.getPlayerName() + " oyuncusu, " + otherTarget + " karesini hedeflemistir.");
                }

                //eger o kare hashmapte yoksa once hashmape ekle, sonra da her sekilde oyuncuyu ekle
                if (!otherTargets.containsKey(otherTarget)) {
                    otherTargets.put(otherTarget, new ArrayList<Player>());
                }
                otherTargets.get(otherTarget).add(player);
            }
        }

        //simdi bulunan karelerden ulasilabilir ve en karli olani bul, yoksa null dondur
        for (Block otherTarget : otherTargets.keySet()) {
            boolean canReachFirst = true; //eger o kareyi hedefleyen oyunculardan birisi bile daha once ulasacaksa false olacak
            for (Player player : otherTargets.get(otherTarget)) {
                if (!isReachableFirst(player.getLocatedAt(), otherTarget)) {
                    canReachFirst = false;
                }
            }
            if (canReachFirst) {
                updateStatus(otherTarget + " karesine diger oyunculardan once ulasilabilir.");
                //Eger o kareye diger hedefleyenlerden once ulasilabiliyorsa
                //daha once bulunan hedeften daha karliysa yeni hedef olarak belirle
                if (target == null || getBlockProfit(target) < getBlockProfit(otherTarget)) {
                    target = otherTarget;
                }
            } else {
                updateStatus(otherTarget + " karesine diger oyunculardan once ulasilamaz.");
            }
        }
        updateStatus("");
        return target;
    }

    @Override
    void selectTarget() {
        if (getCurrentGold() >= goldCostPerTargeting) {
            setTargetBlock(getTargetOfAnotherPlayer()); //baska bir oyuncunun hedefi secilmeye calisiliyor
            if (getTargetBlock() == null) {
                updateStatus("Diger oyuncularin hedefleri secilemedigi icin en karli kare secilecek...");
                setTargetBlock(getMostProfitableBlock()); //digerlerinin hedefi secilemediyse en karli olan seciliyor
            }
        } else { //hamle yapmak icin altin yetersizse oyuncu pasiflestiriliyor
            updateStatus(getPlayerName() + " oyuncusunun altini hedef secmek icin yetersiz! Oyuncu eleniyor.");
            setPlayerActive(false);
        }
    }
}