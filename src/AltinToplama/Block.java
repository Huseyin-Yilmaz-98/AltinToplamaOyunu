package AltinToplama;

public class Block {
    final int index, row, col; //karenin blocks arrayindeki indexi ve tahtadaki satir ve sutun numarasi koordinatlari
    private boolean containsGold, containsHiddenGold; //karede normal ve gizli altin bulunma durumu
    private int goldAmount; //varsa karede bulunan altin, default deger 0'dir
    private boolean revealedLater; //eger gizli altin sonradan acilmissa true olur, renk degisimi icin

    public Block(int index, int row, int col) {
        this.row = row;
        this.col = col;
        this.index = index;
        this.containsGold = false;
        this.containsHiddenGold = false;
        this.goldAmount = 0;
        revealedLater = false;
    }

    //tahtada sira ve sutun 1'den basladigi icin row ve col'un bir fazlasi yazdiriliyor
    @Override
    public String toString() {
        return "(i" + (row + 1) + ", j" + (col + 1) + ")";
    }

    public void setContainsGold(boolean containsGold) {
        this.containsGold = containsGold;
    }

    public boolean getContainsGold() {
        return this.containsGold;
    }

    public void setContainsHiddenGold(boolean containsHiddenGold) {
        this.containsHiddenGold = containsHiddenGold;
    }

    public boolean getContainsHiddenGold() {
        return this.containsHiddenGold;
    }

    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public boolean isRevealedLater() {
        return revealedLater;
    }

    //gizli altini gorunur hale getiren fonksiyon
    public void revealHiddenGold() {
        setContainsGold(true);
        setContainsHiddenGold(false);
        revealedLater = true;
    }
}
