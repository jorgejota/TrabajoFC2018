//package com.tfg.futureWork;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
//import org.apache.pdfbox.rendering.PDFRenderer;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.awt.image.RenderedImage;
//import java.io.File;
//import java.io.IOException;
//
//class RegionPDFRenderer {
//
//    private static final int POINTS_IN_INCH = 72;
//
//    private final PDDocument document;
//    private final PDFRenderer renderer;
//    private final int resolutionDotPerInch;
//
//    RegionPDFRenderer(PDDocument document, int resolutionDotPerInch) {
//        this.document = document;
//        this.renderer = new PDFRenderer(document);
//        this.resolutionDotPerInch = resolutionDotPerInch;
//    }
//
//    public RenderedImage renderRect(int pageIndex, Rectangle2D rect) {
//        BufferedImage image = createImage(rect);
//        Graphics2D graphics = createGraphics(image, rect);
//        try {
//			renderer.renderPageToGraphics(pageIndex, graphics);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        graphics.dispose();
//        return image;
//    }
//
//    private BufferedImage createImage(Rectangle2D rect) {
//        double scale = resolutionDotPerInch / POINTS_IN_INCH;
//        int bitmapWidth  = (int) (rect.getWidth()  * scale);
//        int bitmapHeight = (int) (rect.getHeight() * scale);
//        return new BufferedImage(bitmapWidth, bitmapHeight, BufferedImage.TYPE_INT_RGB);
//    }
//
//    private Graphics2D createGraphics(BufferedImage image, Rectangle2D rect) {
//        double scale = resolutionDotPerInch / POINTS_IN_INCH;
//        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
//        transform.concatenate(AffineTransform.getTranslateInstance(-rect.getX(), -rect.getY()));
//        Graphics2D graphics = image.createGraphics();
//        graphics.setBackground(Color.WHITE);
//        graphics.setTransform(transform);
//        return graphics;
//    }
//
//    public static void main(String[] args) throws InvalidPasswordException, IOException {
//    	String auxiliar = "C:\\Users\\jorge\\Desktop\\";
//        String filePath = auxiliar + "paper4.pdf";
//    	int pageIndex = 0;
//        Rectangle region = new Rectangle(70, 472, 498, 289);
//        int resolutionForHiDPIScreenRendering = 220; /* dpi */
//        PDDocument doc = PDDocument.load(new File(filePath));
//        try {
//        	RegionPDFRenderer renderer = new RegionPDFRenderer(doc, resolutionForHiDPIScreenRendering);
//            BufferedImage image = (BufferedImage) renderer.renderRect(pageIndex, region);
//            ImageIO.write(image, "png", new File(auxiliar + "image.png"));
//        } finally {
//            doc.close();
//        }
//    }
//
//}