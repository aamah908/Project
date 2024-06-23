package project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SingleThread {

    public static short [][] grayImage;
    public static int width;
    public static int height;
    private static BufferedImage image;
    
    public static void main(String[] args) throws IOException {
    	// TODO Auto-generated method stub
    	
        String fileNameInp = "TenCardG.jpg";
    	short [][] source_img = readColourImage(fileNameInp);        
        
        String fileNameTemplate = "Template.jpg";
        short [][] template_img = readColourImage(fileNameTemplate);

        long startTime = System.currentTimeMillis();
        templateMatchingSingleThread(source_img, template_img, ImageIO.read(new File(fileNameInp)));
        long endTime = System.currentTimeMillis();
        
        System.out.println("Single-threaded execution time: " + (endTime - startTime) + " ms");
    }

    public static void templateMatchingSingleThread(short[][] source_img, short[][] template_img, BufferedImage image_source) throws IOException {
        int r1 = source_img.length;
        int c1 = source_img[0].length;
        int r2 = template_img.length;
        int c2 = template_img[0].length;
        double minimum = Double.MAX_VALUE;
        double[][] absDiffMat = new double[r1 - r2 + 1][c1 - c2 + 1];

        for (int i = 0; i <= r1 - r2; i++) {
            for (int j = 0; j <= c1 - c2; j++) {
                double absDiff = calculateMeanAbsoluteDifference(source_img, template_img, i, j);
                absDiffMat[i][j] = absDiff;
                if (absDiff < minimum) {
                    minimum = absDiff;
                }
            }
        }

        double threshold = 10 * minimum;
        drawRectangles(image_source, absDiffMat, threshold, r2, c2, "ResultSingleThread.png");
    }

    public static double calculateMeanAbsoluteDifference(short[][] source_img, short[][] template_img, int x, int y) {
        int r2 = template_img.length;
        int c2 = template_img[0].length;
        double sum = 0.0;
        for (int i = 0; i < r2; i++) {
            for (int j = 0; j < c2; j++) {
                sum += Math.abs(source_img[x + i][y + j] - template_img[i][j]);
            }
        }
        return sum / (r2 * c2);
    }

    public static void drawRectangles(BufferedImage sourceImage, double[][] absDiffMat, double threshold, int r2, int c2, String outputFileName) throws IOException {
        BufferedImage resultImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultImage.createGraphics();
        g.drawImage(sourceImage, 0, 0, null);
        g.setColor(Color.RED);

        for (int i = 0; i < absDiffMat.length; i++) {
            for (int j = 0; j < absDiffMat[0].length; j++) {
                if (absDiffMat[i][j] <= threshold) {
                    g.drawRect(j, i, c2, r2);
                }
            }
        }

        g.dispose();
        ImageIO.write(resultImage, "png", new File(outputFileName));
    }

    public static short[][] readColourImage(String fileName) {
        try {
            byte[] pixels;

            File inp = new File(fileName);
            image = ImageIO.read(inp);
            width = image.getWidth();
            height = image.getHeight();          

            pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.out.println("Dimension of the image: WxH = " + width + "x" + height + " | Num of pixels: " + pixels.length);

            grayImage = new short[height][width];
            int coord;
            for (int i = 0 ; i < height ; i++) {
                for (int j = 0 ; j < width ; j++) {                         
                    coord = 3 * (i * width + j);
                    int pr = ((short) pixels[coord] & 0xff); // red
                    int pg = (((short) pixels[coord + 1] & 0xff)); // green
                    int pb = (((short) pixels[coord + 2] & 0xff)); // blue                
                    grayImage[i][j] = (short) Math.round(0.299 * pr + 0.587 * pg + 0.114 * pb);         
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return grayImage;
    }
}
