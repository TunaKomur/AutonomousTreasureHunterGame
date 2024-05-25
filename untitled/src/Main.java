import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class Main extends JPanel {
    private int haritaBoyutu;
    private boolean[][] gorunenHarita;
    private BufferedImage karakterResmi; // Karakter resmi
    private final int KARE_BOYUTU = 10; // Kare boyutu piksel cinsinden
    private Karakter karakter;
    private Engel[][] harita;
    private boolean haritaOlusturuldu = false;
    private boolean[][] hazineler;
    private BufferedImage[] hazineResimleri; // Hazine resimlerini tutmak için dizi
    private BufferedImage[] engelResimleri; // Engel resimlerini tutmak için dizi
    private Timer timer;
    private int adimSayaci = 0; // Adım sayacı
    private Random random;
    public int toplamHazineSayisi;
    public int toplananHazineSayisi;
    private int[] hazineResimIndeksleri;
    private JLabel mesajLabel;
    public Main() {
        toplamHazineSayisi = 0;
        toplananHazineSayisi = 0;

        setBackground(Color.WHITE);
        random = new Random();
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hareketEt();
            }
        });

        // Hazine resimlerini yükle
        hazineResimleri = new BufferedImage[4]; // Örneğin 4 farklı hazine resmi kullanacağız
        try {
            hazineResimleri[0] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\altin.jpg")); // hazine1.jpg dosyasının adını güncelleyin
            hazineResimleri[1] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\gumus.jpg")); // hazine2.jpg dosyasının adını güncelleyin
            hazineResimleri[2] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\zumrut.jpg")); // hazine3.jpg dosyasının adını güncelleyin
            hazineResimleri[3] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\bakir2.jpg")); // hazine4.jpg dosyasının adını güncelleyin
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Engeller için resimleri yükle
        engelResimleri = new BufferedImage[6];
        try {
            engelResimleri[0] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\agac.jpg"));
            engelResimleri[1] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\kaya.jpg"));
            engelResimleri[2] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\duvar.jpg"));
            engelResimleri[3] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\dag.jpg"));
            engelResimleri[4] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\kus.jpg"));
            engelResimleri[5] = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\ari2.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Karakter resmini yükle
        try {
            karakterResmi = ImageIO.read(new File("C:\\Users\\Aldemir\\Desktop\\prolab 2\\untitled\\src\\pou.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Karakter nesnesini oluştur
        karakter = new Karakter(0, 0);
        mesajLabel = new JLabel("Oyun başladı.");
        mesajLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Sağa yaslanmış metin
        mesajLabel.setForeground(Color.RED);
        add(mesajLabel, BorderLayout.NORTH);
    }

    private void hazineleriYerlestir() {
        hazineler = new boolean[haritaBoyutu][haritaBoyutu];
        hazineResimIndeksleri = new int[haritaBoyutu * haritaBoyutu];
        toplamHazineSayisi = 0;

        for (int i = 0; i < haritaBoyutu; i++) {
            for (int j = 0; j < haritaBoyutu; j++) {
                hazineler[i][j] = random.nextInt(10) == 0; // 1/10 olasılıkla hazine yerleştir
                if (hazineler[i][j]) {
                    toplamHazineSayisi++;
                    hazineResimIndeksleri[i * haritaBoyutu + j] = random.nextInt(hazineResimleri.length);
                }
            }
        }
    }

    private void engelleriYerlestir() {
        // Engelleri rastgele yerleştir
        harita = new Engel[haritaBoyutu][haritaBoyutu];
        for (int i = 0; i < haritaBoyutu; i++) {
            for (int j = 0; j < haritaBoyutu; j++) {
                if (!hazineler[i][j] && random.nextInt(20) == 0) { // Engel oluştur
                    if (random.nextInt(2) == 0) { // Rastgele bir engel seç
                        int engelTurIndex = random.nextInt(3); // Toplam 3 farklı engel türü var
                        harita[i][j] = new Engel(i, j, engelResimleri[engelTurIndex]);
                    } else {
                        // 2x2 3*3 4*4 boyutunda olsun
                        if (i < haritaBoyutu - 1 && j < haritaBoyutu - 1 && !hazineler[i+1][j] && !hazineler[i][j+1] && !hazineler[i+1][j+1]) {
                            int engelTurIndex = random.nextInt(engelResimleri.length);
                            harita[i][j] = new Engel(i, j, engelResimleri[engelTurIndex]);
                            harita[i][j+1] = new Engel(i, j+1, engelResimleri[engelTurIndex]);
                            harita[i+1][j] = new Engel(i+1, j, engelResimleri[engelTurIndex]);
                            harita[i+1][j+1] = new Engel(i+1, j+1, engelResimleri[engelTurIndex]);
                        }
                    }
                }
            }
        }
    }

    private void hareketEt() {
        // Tüm hazineler toplandıysa oyunu bitir
        if (toplananHazineSayisi == toplamHazineSayisi) {
            JOptionPane.showMessageDialog(this, "Tüm hazineler toplandı!");
            timer.stop(); // Oyunu durdur

            return;
        }

        int randomYon = random.nextInt(4); // Rastgele bir yön seç
        int yeniX = karakter.getXEkseni();
        int yeniY = karakter.getYEkseni();
        adimSayaci++;
        mesajLabel.setText("Toplam Adım: " + adimSayaci);

        switch (randomYon) {
            case 0: // Yukarı
                yeniY -= KARE_BOYUTU;
                break;
            case 1: // Aşağı
                yeniY += KARE_BOYUTU;
                break;
            case 2: // Sol
                yeniX -= KARE_BOYUTU;
                break;
            case 3: // Sağ
                yeniX += KARE_BOYUTU;
                break;
        }


        // Yeni pozisyonun harita sınırları içinde olup olmadığını kontrol et
        if (yeniX >= 0 && yeniY >= 0 && yeniX < haritaBoyutu * KARE_BOYUTU && yeniY < haritaBoyutu * KARE_BOYUTU) {
            int yeniKareI = yeniY / KARE_BOYUTU;
            int yeniKareJ = yeniX / KARE_BOYUTU;

            // Yeni pozisyonun engel olup olmadığını kontrol et
            if (!engelVar(yeniKareI, yeniKareJ)) {
                karakter.setXEkseni(yeniX);
                karakter.setYEkseni(yeniY);

                // Yeni pozisyonun bir hazine içerip içermediğini kontrol et
                if (hazineler[yeniKareI][yeniKareJ]) {
                    // Karakter hazineyi alır
                    hazineler[yeniKareI][yeniKareJ] = false; // Hazineyi kaldır
                    hazineResmiBulundu(yeniKareJ, yeniKareI); // Koordinatları iletiliyor

                    toplananHazineSayisi++;

                    // Son hazine alındıysa hazine resmini kaldır
                    if (toplananHazineSayisi == toplamHazineSayisi) {
                        repaint(); // Hazine resmini ekrandan kaldırmak için yeniden boyama yap
                    }
                }
                for (int i = Math.max(0, yeniKareI - 3); i <= Math.min(haritaBoyutu - 1, yeniKareI + 3); i++) {
                    for (int j = Math.max(0, yeniKareJ - 3); j <= Math.min(haritaBoyutu - 1, yeniKareJ + 3); j++) {
                        gorunenHarita[i][j] = true;
                    }
                }

            }
            // Kuşların sadece yukarı veya aşağı yöne hareket etmesi, her adımda en fazla 5 birim hareket etmesi
            for (int i = 0; i < haritaBoyutu; i++) {
                for (int j = 0; j < haritaBoyutu; j++) {
                    if (engelVar(i, j) && harita[i][j].getResim() == engelResimleri[4]) { // Kuş resmi kontrolü
                        int kuşYon = random.nextInt(2); // Rastgele yukarı veya aşağı yön seç
                        int yeniXKuş = j * KARE_BOYUTU;
                        int yeniYKuş = i * KARE_BOYUTU;

                        switch (kuşYon) {
                            case 0: // Yukarı
                                yeniYKuş -= KARE_BOYUTU; // 1 birim yukarı hareket
                                break;
                            case 1: // Aşağı
                                yeniYKuş += KARE_BOYUTU; // 1 birim aşağı hareket
                                break;
                        }

                        // Yeni kuş pozisyonunun harita sınırları içinde olup olmadığını kontrol et
                        if (yeniXKuş >= 0 && yeniYKuş >= 0 && yeniXKuş < haritaBoyutu * KARE_BOYUTU && yeniYKuş < haritaBoyutu * KARE_BOYUTU) {
                            int yeniKuşI = yeniYKuş / KARE_BOYUTU;
                            int yeniKuşJ = yeniXKuş / KARE_BOYUTU;

                            // Kuşu maksimum 5 birim uzağa hareket ettir
                            int deltaX = Math.abs(yeniKuşJ - j);
                            if (deltaX > 5) {
                                yeniKuşJ = j + (kuşYon == 0 ? 5 : -5);
                            }

                            // Yeni kuş pozisyonunun engel olup olmadığını kontrol et
                            if (!engelVar(yeniKuşI, yeniKuşJ)) {
                                harita[i][j] = null; // Eski kuş pozisyonunu temizle
                                harita[yeniKuşI][yeniKuşJ] = new Engel(yeniKuşI, yeniKuşJ, engelResimleri[4]); // Yeni kuş pozisyonunu güncelle
                            }
                        }
                    }
                }

            }

            // arıların sadece yukarı veya aşağı yöne hareket etmesi
            // Arıların sadece sağa veya sola yöne hareket etmesi
            for (int i = 0; i < haritaBoyutu; i++) {
                for (int j = 0; j < haritaBoyutu; j++) {
                    if (engelVar(i, j) && harita[i][j].getResim() == engelResimleri[5]) { // Arı resmi kontrolü
                        int arıYon = random.nextInt(2); // Rastgele sağa veya sola yön seç
                        int yeniXArı = j * KARE_BOYUTU;
                        int yeniYArı = i * KARE_BOYUTU;

                        switch (arıYon) {
                            case 0: // Sağa
                                yeniXArı += KARE_BOYUTU; // 1 birim sağa hareket
                                break;
                            case 1: // Sola
                                yeniXArı -= KARE_BOYUTU; // 1 birim sola hareket
                                break;
                        }

                        // Yeni arı pozisyonunun harita sınırları içinde olup olmadığını kontrol et
                        if (yeniXArı >= 0 && yeniYArı >= 0 && yeniXArı < haritaBoyutu * KARE_BOYUTU && yeniYArı < haritaBoyutu * KARE_BOYUTU) {
                            int yeniArıI = yeniYArı / KARE_BOYUTU;
                            int yeniArıJ = yeniXArı / KARE_BOYUTU;

                            // Arıyı maksimum 3 birim uzağa hareket ettir
                            int deltaX = yeniArıJ - j;
                            if (deltaX > 2) {
                                yeniArıJ = j + 2;
                            } else if (deltaX < -2) {
                                yeniArıJ = j - 2;
                            }

                            // Yeni arı pozisyonunun engel olup olmadığını kontrol et
                            if (!engelVar(yeniArıI, yeniArıJ)) {
                                harita[i][j] = null; // Eski arı pozisyonunu temizle
                                harita[yeniArıI][yeniArıJ] = new Engel(yeniArıI, yeniArıJ, engelResimleri[5]); // Yeni arı pozisyonunu güncelle
                            }
                        }
                    }
                }
            }


        }

        repaint();
    }



    // Belirtilen konumda bir engel olup olmadığını kontrol eder
    private boolean engelVar(int i, int j) {
        return harita[i][j] != null;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Engelleri ve hazineleri çizme
        if (haritaOlusturuldu) {
            for (int i = 0; i < haritaBoyutu; i++) {
                for (int j = 0; j < haritaBoyutu; j++) {
                    // Görünen haritada ise çiz
                    if (gorunenHarita[i][j]) {
                        // Arka planı çiz
                        if (j < haritaBoyutu / 2) {
                            g.setColor(Color.WHITE);
                        } else {
                            g.setColor(Color.YELLOW);
                        }
                        g.fillRect(j * KARE_BOYUTU, i * KARE_BOYUTU, KARE_BOYUTU, KARE_BOYUTU);

                        // Birim karelerin çizgilerini siyah renkle çiz
                        g.setColor(Color.BLACK);
                        g.drawRect(j * KARE_BOYUTU, i * KARE_BOYUTU, KARE_BOYUTU, KARE_BOYUTU);

                        // Engelleri ve hazineleri çiz
                        if (hazineler[i][j] || engelVar(i, j)) {
                            BufferedImage resim;
                            if (hazineler[i][j]) {
                                int resimIndex = hazineResimIndeksleri[i * haritaBoyutu + j];
                                resim = hazineResimleri[resimIndex];
                            } else {
                                resim = harita[i][j].getResim();
                            }
                            g.drawImage(resim, j * KARE_BOYUTU, i * KARE_BOYUTU, KARE_BOYUTU, KARE_BOYUTU, null);
                        }
                    }
                }
            }
            // Karakteri çizme
            g.drawImage(karakterResmi, karakter.getXEkseni(), karakter.getYEkseni(), KARE_BOYUTU, KARE_BOYUTU, null);
        }
    }


    private void hazineResmiBulundu(int x, int y) {
        String hazineAdi = ""; // Hazine adını saklamak için boş bir string
        switch (hazineResimIndeksleri[y * haritaBoyutu + x]) {
            case 0:
                hazineAdi = "Altin Sandik";
                break;
            case 1:
                hazineAdi = "Gumus Sandik";
                break;
            case 2:
                hazineAdi = "Zumrut Sandik";
                break;
            case 3:
                hazineAdi = "Bakir Sandik";
                break;
            default:
                hazineAdi = "Bilinmeyen";
                break;
        }
        JOptionPane.showMessageDialog(this, hazineAdi + " toplandı! (" + x + ", " + y + ") konumunda bulundu.");
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Oyun Paneli");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JButton yeniHaritaButton = new JButton("Yeni Harita Oluştur");
        JButton baslatButton = new JButton("Başlat");
        panel.add(yeniHaritaButton);
        panel.add(baslatButton);

        Main main = new Main();
        frame.getContentPane().add(BorderLayout.CENTER, main);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);

        main.gorunenHarita = new boolean[main.haritaBoyutu][main.haritaBoyutu];

        yeniHaritaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(frame, "Harita boyutunu girin:");
                try {
                    int boyut = Integer.parseInt(input);
                    if (boyut > 0) {
                        main.haritaBoyutu = boyut;
                        main.karakter.setXEkseni(main.random.nextInt(boyut) * main.KARE_BOYUTU);
                        main.karakter.setYEkseni(main.random.nextInt(boyut) * main.KARE_BOYUTU);
                        main.haritaOlusturuldu = true;
                        main.gorunenHarita = new boolean[boyut][boyut]; // Yeni harita oluşturulduğunda görünür harita sıfırla
                        main.hazineleriYerlestir();
                        main.engelleriYerlestir();
                        main.repaint();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Geçersiz boyut! Pozitif bir tam sayı girin.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Geçersiz giriş! Lütfen bir tam sayı girin.");
                }
            }
        });

        baslatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!main.haritaOlusturuldu) {
                    JOptionPane.showMessageDialog(frame, "Lütfen önce harita oluşturun.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Oyun başladı.");
                    main.timer.start();
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


/*
  private void sisAc() {
        int karakterX = karakter.getXEkseni();
        int karakterY = karakter.getYEkseni();
        int kareI = karakterY / KARE_BOYUTU;
        int kareJ = karakterX / KARE_BOYUTU;

        // Karakterin etrafındaki karelerin görünür olmasını sağla
        for (int i = Math.max(0, kareI - 3); i <= Math.min(haritaBoyutu - 1, kareI + 3); i++) {
            for (int j = Math.max(0, kareJ - 3); j <= Math.min(haritaBoyutu - 1, kareJ + 3); j++) {
                gorunenHarita[i][j] = true;
            }
        }

        // Yeniden çizme işlemi
        repaint();
    }

    private void hareketEt() {
        // Tüm hazineler toplandıysa oyunu bitir
        if (toplananHazineSayisi == toplamHazineSayisi) {
            JOptionPane.showMessageDialog(this, "Tüm hazineler toplandı!");
            timer.stop(); // Oyunu durdur
            return; // Metodu sonlandır
        }

        int karakterX = karakter.getXEkseni();
        int karakterY = karakter.getYEkseni();
        int enYakinHazineX = -1;
        int enYakinHazineY = -1;
        double enKisaMesafe = Double.MAX_VALUE;

        // Haritadaki tüm hazinelerin konumlarını kontrol et
        for (int i = 0; i < haritaBoyutu; i++) {
            for (int j = 0; j < haritaBoyutu; j++) {
                if (hazineler[i][j]) {
                    // Karakter ile hazine arasındaki mesafeyi hesapla
                    double mesafe = Math.sqrt(Math.pow(karakterX - j * KARE_BOYUTU, 2) + Math.pow(karakterY - i * KARE_BOYUTU, 2));
                    // En yakın hazineyi bul
                    if (mesafe < enKisaMesafe) {
                        enKisaMesafe = mesafe;
                        enYakinHazineX = j * KARE_BOYUTU;
                        enYakinHazineY = i * KARE_BOYUTU;
                    }
                }
            }
        }

        // En yakın hazineye doğru hareket et
        if (enYakinHazineX != -1 && enYakinHazineY != -1) {
            int deltaX = enYakinHazineX - karakterX;
            int deltaY = enYakinHazineY - karakterY;
            // Yatay ve dikey yöndeki hareketleri kontrol et
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                // Yatay yönde hareket et
                karakterX += Integer.compare(deltaX, 0) * KARE_BOYUTU;
            } else {
                // Dikey yönde hareket et
                karakterY += Integer.compare(deltaY, 0) * KARE_BOYUTU;
            }
        }

        // Kontrol edilecek karakterin yeni konumu
        int yeniKarakterX = karakterX;
        int yeniKarakterY = karakterY;

        // Engellerin kontrolü
        int kareI = karakterY / KARE_BOYUTU;
        int kareJ = karakterX / KARE_BOYUTU;
        if (engelVar(kareI, kareJ)) {
            // Karakter engel üzerinde, hareketi engelle

            // Engeli geçmek için sağa, aşağıya, sola ve yukarıya doğru deneme
            if (!engelVar(kareI, kareJ + 1)) {
                // Sağa git
                yeniKarakterX += KARE_BOYUTU;
            } else if (!engelVar(kareI + 1, kareJ)) {
                // Aşağı git
                yeniKarakterY += KARE_BOYUTU;
            } else if (!engelVar(kareI, kareJ - 1)) {
                // Sola git
                yeniKarakterX -= KARE_BOYUTU;
            } else if (!engelVar(kareI - 1, kareJ)) {
                // Yukarı git
                yeniKarakterY -= KARE_BOYUTU;
            } else {
                // Hiçbir yöne gidilemiyor, bu durumda karakterin sonuna kadar gitmeye çalış
                if (kareJ < haritaBoyutu - 1) {
                    // Sağa git
                    yeniKarakterX += KARE_BOYUTU;
                } else if (kareI < haritaBoyutu - 1) {
                    // Aşağı git
                    yeniKarakterY += KARE_BOYUTU;
                } else if (kareJ > 0) {
                    // Sola git
                    yeniKarakterX -= KARE_BOYUTU;
                } else if (kareI > 0) {
                    // Yukarı git
                    yeniKarakterY -= KARE_BOYUTU;
                }
            }
        }

        // Karakterin yeni pozisyonunu ayarla
        karakter.setXEkseni(yeniKarakterX);
        karakter.setYEkseni(yeniKarakterY);

        // Yeni pozisyonun bir hazine içerip içermediğini kontrol et
        kareI = yeniKarakterY / KARE_BOYUTU;
        kareJ = yeniKarakterX / KARE_BOYUTU;
        if (hazineler[kareI][kareJ]) {
            // Karakter hazineyi alır
            hazineler[kareI][kareJ] = false; // Hazineyi kaldır
            hazineResmiBulundu(kareJ, kareI); // Koordinatları iletiliyor
            toplananHazineSayisi++;

            // Son hazine alındıysa hazine resmini kaldır
            if (toplananHazineSayisi == toplamHazineSayisi) {
                repaint(); // Hazine resmini ekrandan kaldırmak için yeniden boyama yap
            }
        }

        sisAc();



        // Kuşların sadece yukarı veya aşağı yöne hareket etmesi, her adımda en fazla 5 birim hareket etmesi
        for (int i = 0; i < haritaBoyutu; i++) {
            for (int j = 0; j < haritaBoyutu; j++) {
                if (engelVar(i, j) && harita[i][j].getResim() == engelResimleri[4]) { // Kuş resmi kontrolü
                    int kuşYon = random.nextInt(2); // Rastgele yukarı veya aşağı yön seç
                    int yeniXKuş = j * KARE_BOYUTU;
                    int yeniYKuş = i * KARE_BOYUTU;

                    switch (kuşYon) {
                        case 0: // Yukarı
                            yeniYKuş -= KARE_BOYUTU; // 1 birim yukarı hareket
                            break;
                        case 1: // Aşağı
                            yeniYKuş += KARE_BOYUTU; // 1 birim aşağı hareket
                            break;
                    }

                    // Yeni kuş pozisyonunun harita sınırları içinde olup olmadığını kontrol et
                    if (yeniXKuş >= 0 && yeniYKuş >= 0 && yeniXKuş < haritaBoyutu * KARE_BOYUTU && yeniYKuş < haritaBoyutu * KARE_BOYUTU) {
                        int yeniKuşI = yeniYKuş / KARE_BOYUTU;
                        int yeniKuşJ = yeniXKuş / KARE_BOYUTU;

                        // Kuşu maksimum 5 birim uzağa hareket ettir
                        int deltaX = Math.abs(yeniKuşJ - j);
                        if (deltaX > 5) {
                            yeniKuşJ = j + (kuşYon == 0 ? 5 : -5);
                        }

                        // Yeni kuş pozisyonunun engel olup olmadığını kontrol et
                        if (!engelVar(yeniKuşI, yeniKuşJ)) {
                            harita[i][j] = null; // Eski kuş pozisyonunu temizle
                            harita[yeniKuşI][yeniKuşJ] = new Engel(yeniKuşI, yeniKuşJ, engelResimleri[4]); // Yeni kuş pozisyonunu güncelle
                        }
                    }
                }
            }

        }

        // arıların sadece yukarı veya aşağı yöne hareket etmesi
        // Arıların sadece sağa veya sola yöne hareket etmesi
        for (int i = 0; i < haritaBoyutu; i++) {
            for (int j = 0; j < haritaBoyutu; j++) {
                if (engelVar(i, j) && harita[i][j].getResim() == engelResimleri[5]) { // Arı resmi kontrolü
                    int arıYon = random.nextInt(2); // Rastgele sağa veya sola yön seç
                    int yeniXArı = j * KARE_BOYUTU;
                    int yeniYArı = i * KARE_BOYUTU;

                    switch (arıYon) {
                        case 0: // Sağa
                            yeniXArı += KARE_BOYUTU; // 1 birim sağa hareket
                            break;
                        case 1: // Sola
                            yeniXArı -= KARE_BOYUTU; // 1 birim sola hareket
                            break;
                    }

                    // Yeni arı pozisyonunun harita sınırları içinde olup olmadığını kontrol et
                    if (yeniXArı >= 0 && yeniYArı >= 0 && yeniXArı < haritaBoyutu * KARE_BOYUTU && yeniYArı < haritaBoyutu * KARE_BOYUTU) {
                        int yeniArıI = yeniYArı / KARE_BOYUTU;
                        int yeniArıJ = yeniXArı / KARE_BOYUTU;

                        // Arıyı maksimum 3 birim uzağa hareket ettir
                        int deltaX = yeniArıJ - j;
                        if (deltaX > 2) {
                            yeniArıJ = j + 2;
                        } else if (deltaX < -2) {
                            yeniArıJ = j - 2;
                        }

                        // Yeni arı pozisyonunun engel olup olmadığını kontrol et
                        if (!engelVar(yeniArıI, yeniArıJ)) {
                            harita[i][j] = null; // Eski arı pozisyonunu temizle
                            harita[yeniArıI][yeniArıJ] = new Engel(yeniArıI, yeniArıJ, engelResimleri[5]); // Yeni arı pozisyonunu güncelle
                        }
                    }
                }
            }
        }

        repaint();
    }


    // Belirtilen konumda bir engel olup olmadığını kontrol eder
    private boolean engelVar(int i, int j) {
        return harita[i][j] != null;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Engelleri ve hazineleri çizme
        if (haritaOlusturuldu) {
            for (int i = 0; i < haritaBoyutu; i++) {
                for (int j = 0; j < haritaBoyutu; j++) {
                    // Görünen haritada ise çiz
                    if (gorunenHarita[i][j]) {
                        // Arka planı çiz
                        if (j < haritaBoyutu / 2) {
                            g.setColor(Color.WHITE);
                        } else {
                            g.setColor(Color.YELLOW);
                        }
                        g.fillRect(j * KARE_BOYUTU, i * KARE_BOYUTU, KARE_BOYUTU, KARE_BOYUTU);

                        // Birim karelerin çizgilerini siyah renkle çiz
                        g.setColor(Color.BLACK);
                        g.drawRect(j * KARE_BOYUTU, i * KARE_BOYUTU, KARE_BOYUTU, KARE_BOYUTU);

                        // Engelleri ve hazineleri çiz
                        if (hazineler[i][j] || engelVar(i, j)) {
                            BufferedImage resim;
                            if (hazineler[i][j]) {
                                int resimIndex = hazineResimIndeksleri[i * haritaBoyutu + j];
                                resim = hazineResimleri[resimIndex];
                            } else {
                                resim = harita[i][j].getResim();
                            }
                            g.drawImage(resim, j * KARE_BOYUTU, i * KARE_BOYUTU, KARE_BOYUTU, KARE_BOYUTU, null);
                        }
                    }
                }
            }
            // Karakteri çizme
            g.drawImage(karakterResmi, karakter.getXEkseni(), karakter.getYEkseni(), KARE_BOYUTU, KARE_BOYUTU, null);
        }
    }


    private void hazineResmiBulundu(int x, int y) {
        String hazineAdi = ""; // Hazine adını saklamak için boş bir string
        switch (hazineResimIndeksleri[y * haritaBoyutu + x]) {
            case 0:
                hazineAdi = "Altin Sandik";
                break;
            case 1:
                hazineAdi = "Gumus Sandik";
                break;
            case 2:
                hazineAdi = "Zumrut Sandik";
                break;
            case 3:
                hazineAdi = "Bakir Sandik";
                break;
            default:
                hazineAdi = "Bilinmeyen";
                break;
        }
        JOptionPane.showMessageDialog(this, hazineAdi + " toplandı! (" + x + ", " + y + ") konumunda bulundu.");
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Oyun Paneli");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JButton yeniHaritaButton = new JButton("Yeni Harita Oluştur");
        JButton baslatButton = new JButton("Başlat");
        panel.add(yeniHaritaButton);
        panel.add(baslatButton);

        Main main = new Main();
        frame.getContentPane().add(BorderLayout.CENTER, main);
        frame.getContentPane().add(BorderLayout.SOUTH, panel);

        main.gorunenHarita = new boolean[main.haritaBoyutu][main.haritaBoyutu];

        yeniHaritaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(frame, "Harita boyutunu girin:");
                try {
                    int boyut = Integer.parseInt(input);
                    if (boyut > 0) {
                        main.haritaBoyutu = boyut;
                        main.karakter.setXEkseni(main.random.nextInt(boyut) * main.KARE_BOYUTU);
                        main.karakter.setYEkseni(main.random.nextInt(boyut) * main.KARE_BOYUTU);
                        main.haritaOlusturuldu = true;
                        main.gorunenHarita = new boolean[boyut][boyut]; // Yeni harita oluşturulduğunda görünür harita sıfırlanır
                        main.hazineleriYerlestir(); // Yeni haritayı oluştururken hazineleri yerleştir
                        main.engelleriYerlestir(); // Yeni haritayı oluştururken engelleri yerleştir
                        main.repaint();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Geçersiz boyut! Pozitif bir tam sayı girin.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Geçersiz giriş! Lütfen bir tam sayı girin.");
                }
            }
        });

        baslatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!main.haritaOlusturuldu) {
                    JOptionPane.showMessageDialog(frame, "Lütfen önce harita oluşturun.");
                } else {
                    // Başlat işlemi
                    JOptionPane.showMessageDialog(frame, "Oyun başladı.");
                    // Haritayı sis ile kapat
                    main.timer.start();
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
*/ 