/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image12;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import static java.lang.Math.round;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author fleabag
 */
public class showPGM extends Component {

    // image buffer for graphical display
    private BufferedImage img;
    private int[][] px;

    private void pix2img() {
        int g;
        img = new BufferedImage(px[0].length, px.length, BufferedImage.TYPE_INT_ARGB);
        // copy the px values
        for (int row = 0; row < px.length; ++row) {
            for (int col = 0; col < px[row].length; ++col) {
                g = px[row][col];
                img.setRGB(col, row, ((255 << 24) | (g << 16) | (g << 8) | g));
            }
        }
    }

    public showPGM(String filename) {
        px = null;
        readFile(filename);
        if (px != null) {
            pix2img();
        }
        
        JFrame f = new JFrame("PGM");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.add(this);
        f.pack();
        f.setVisible(true);
    }

    public void readFile(String filename) {
        try {
            String filePath = filename;
            FileInputStream fileInputStream = new FileInputStream(filePath);

            Scanner scan = new Scanner(fileInputStream);
            scan.nextLine();
            scan.nextLine();
            
            int picHeight = scan.nextInt();
            int picWidth = scan.nextInt();
            int maxvalue = scan.nextInt();

            fileInputStream.close();

            fileInputStream = new FileInputStream(filePath);
            DataInputStream dis = new DataInputStream(fileInputStream);

            int numnewlines = 4;
            while (numnewlines > 0) {
                char c;
                do {
                    c = (char) (dis.readUnsignedByte());
                } while (c != '\n');
                numnewlines--;
            }

            int a;
            px = new int[picWidth][picHeight];
            int[][] data2D = new int [picWidth][picHeight];
            for (int row = 0; row < picWidth; row++) {
                for (int col = 0; col < picHeight; col++) {
                    px[row][col] = dis.readUnsignedByte();

                    //if px[row][col] is 0 120 160 80 200
                    if (px[row][col] == 0) {
                        px[row][col] = 255;
                        data2D[row][col]=1;
                    } else {
                        px[row][col] = 0;
                        data2D[row][col]=0;
                    }
                }
            }

            double m00 = pqmoment(data2D, 0, 0);
            double m10 = pqmoment(data2D, 1, 0);
            double m01 = pqmoment(data2D, 0, 1);
            double xp = m10 / m00;
            double yp = m01 / m00;
            System.out.println("m00 = " + m00);
            System.out.println("m01 = " + m01);
            System.out.println("m10 = " + m10);
            System.out.println("xp = " + xp);
            System.out.println("yp = " + yp);
            
            px[round((float) xp)][round((float) yp)] = -255700;

            double u20 = cen_moment(data2D, xp, yp, 2, 0);
            double u02 = cen_moment(data2D, xp, yp, 0, 2);
            double u00 = cen_moment(data2D, xp, yp, 0, 0);
            System.out.println("u20 = " + u20);
            System.out.println("u02 = " + u02);
            System.out.println("u00 = " + u00);

            double n20 = normallizer(u20, u00, 2, 0);
            double n02 = normallizer(u02, u00, 0, 2);
            System.out.println("n20 = " + n20);
            System.out.println("n02 = " + n02);

            double quan = n20 + n02;

            System.out.println("fee1 = " + quan);

            fileInputStream.close();
        } catch (FileNotFoundException fe) {
            System.out.println("Had a problem opening a file.");
        } catch (Exception e) {
            System.out.println(e.toString() + " caught in readPPM.");
        }
    }

    public double pqmoment(int data2D[][], int p, int q) 
    {
        double mo = 0;
        for (int x = 0; x < data2D.length; x++) {
            for (int y = 0; y < data2D[0].length; y++) {
                mo += data2D[x][y] * Math.pow(x, p) * Math.pow(y, q);

            }
        }
        return mo;
    }

    public double normallizer(double n, double n00, int p, int q) 
    {
        int pow = ((p + q) / 2) + 1;
        double normalize = n / Math.pow(n00, pow);
        return normalize;
    }

    public double cen_moment(int[][] data2D, double xp, double yp, int p, int q) 
    {
        double cen_moment = 0;
        for (int x = 0; x < data2D.length; x++) {
            for (int y = 0; y < data2D[0].length; y++) {
                cen_moment += Math.pow((x - xp), p) * Math.pow((y - yp), q) * data2D[x][y];
            }
        }
        return cen_moment;
    }

    public void paint(Graphics g) 
    {
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }

    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(Math.max(100, img.getWidth(null)),
                    Math.max(100, img.getHeight(null)));
        }
    }
}
