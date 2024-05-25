import java.awt.image.BufferedImage;

class Engel {
    protected int x;
    protected int y;
    protected BufferedImage resim;

    public Engel(int x, int y, BufferedImage resim) {
        this.x = x;
        this.y = y;
        this.resim = resim;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BufferedImage getResim() {
        return resim;
    }
}


class HareketliEngel extends Engel {
    private int hizX;
    private int hizY;

    public HareketliEngel(int x, int y, int hizX, int hizY, BufferedImage resim) {
        super(x, y, resim);
        this.hizX = hizX;
        this.hizY = hizY;
    }

    public void hareketEt() {
        x += hizX;
        y += hizY;
    }
}
