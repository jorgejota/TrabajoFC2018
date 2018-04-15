package com.tfg.extractPdf;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @since 8/26/16
 */
public class Sample {
    public static void main(String[] args)
            throws IOException, TikaException, SAXException {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);

        TesseractOCRConfig config = new TesseractOCRConfig();
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);

        ParseContext parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(PDFParserConfig.class, pdfConfig);
        //need to add this to make sure recursive parsing happens!
        parseContext.set(Parser.class, parser);

        FileInputStream stream = new FileInputStream("C:\\Users\\jorge\\Desktop\\WorkSpace-Eclipse\\crawler\\DownloadPdf\\Descargas1.pdf");
        Metadata metadata = new Metadata();
        parser.parse(stream, handler, metadata, parseContext);
        System.out.println(metadata);
        String content = handler.toString();
        System.out.println("===============");
        System.out.println(content);
        System.out.println("Done");
    }
}