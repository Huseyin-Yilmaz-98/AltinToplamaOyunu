package AltinToplama;

public class GameThread extends Thread {
    final Board board;
    final GamePanel panel;
    private boolean isGameOver; //oyunun bittigini bildiren degisken

    GameThread(Board board, GamePanel panel) {
        this.board = board;
        this.panel = panel;
        isGameOver = false;
    }

    //oyunun dondugu fonksiyondur, arayuz kitlenmemesi icin thread olarak calistiriliyor
    @Override
    public void run() {
        board.updateStatus("Oyun basliyor...");
        while (!isGameOver) {
            //basta oyunun bittigi varsayiliyor
            isGameOver = true;
            for (Player player : board.players) {
                if (player.isPlayerActive()) {
                    boolean didMakeAMove = player.playRound(); //oyuncudan hamle yapmasi isteniyor

                    //tum oyuncularin hedeflerinde hala altin olup olmadigina bakiliyor, altin alinmissa hedef siliniyor
                    for (Player p : board.players) {
                        if (p.getTargetBlock() != null && p.getTargetBlock().getGoldAmount() == 0) {
                            p.board.updateStatus(p.getPlayerName() + " oyuncusunun hedefi silindi.");
                            p.setTargetBlock(null);
                        }
                    }

                    //eger oyuncu hamle yaptiysa oyun bitmedi sayiliyor
                    isGameOver = didMakeAMove ? false : isGameOver;
                    panel.updateBlocks();
                    board.sleep();
                }
            }
        }
        board.updateStatus("Oyun bitti...");

        //tum oyuncularin hareket gecmisi yazdiriliyor
        for (Player p : board.players) {
            p.saveMovesToFile();
        }

        panel.endButton.setEnabled(true); //bitir butonu etkinlestiriliyor
    }
}
