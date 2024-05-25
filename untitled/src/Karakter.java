public class Karakter extends Lokasyon {
    private String ID;
    private String Ad;
    private int XEkseni;
    private int YEkseni;

    public Karakter(int x,int y) {
        super(x,y);
    }

    public int getXEkseni() {
        return XEkseni;
    }

    public void setXEkseni(int XEkseni) {
        this.XEkseni = XEkseni;
    }

    public int getYEkseni() {
        return YEkseni;
    }

    public void setYEkseni(int YEkseni) {
        this.YEkseni = YEkseni;
    }
}
