package com.tfg.jPedal;


/*
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.idrsolutions.com
 * Help section for developers at http://www.idrsolutions.com/support/
 *
 * (C) Copyright 1997-2015 IDRsolutions and Contributors.
 *
 * This file is part of JPedal/JPDF2HTML5
 *
 
 *
 * ---------------
 * ExtractTextInRectangleAsTable.java
 * ---------------
 */


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.jpedal.PdfDecoderInt;
import org.jpedal.PdfDecoderServer;
import org.jpedal.exception.PdfException;
import org.jpedal.grouping.PdfGroupingAlgorithms;
import org.jpedal.objects.PdfPageData;
import org.jpedal.utils.LogWriter;

/**
 * <h2><b>Sample Code Showing how JPedal Library can be used with
 * PDF to Extract Text from a Specified Rectangle as a Table
 * Structure (which the software will attempt to guess).</b></h2>
 * 
 * <p><b>Debugging tip: Set verbose=true in LogWriter to see what is going on.</b></p>
 *
 * <p>It can run from jar directly using the command:
 * <br><b>java -cp libraries_needed org/jpedal/examples/text/ExtractTextInRectangleAsTable inputValues</b></p>
 *
 * <p>Where inputValues are between one and five space delimited input values:</p>
 * <ul>
 *  <li>First value:          The PDF filename (including the path if needed) or a directory containing PDF files. If it contains spaces it must be enclosed by double quotes (ie "C:/Path with spaces/").</li>
 *  <li>Remaining values:  If you wish to extract PDF text from a specified area of the screen, enter the co-ordinates of the bounding rectangle in the order x1 y1 x2 y2.</li>
 * </ul>
 * 
 * <p><a href="http://www.idrsolutions.com/how-to-extract-text-from-pdf-files/">See our Support Pages for more information on Text Extraction.</a></p>
 * 
 */
public class ExtractTextInRectangleAsTable {

    /**output where we put files*/
    private String user_dir = System.getProperty("user.dir");

    /**correct separator for OS */
    final String separator = System.getProperty("file.separator");

    /**the decoder object which decodes the pdf and returns a data object*/
    PdfDecoderInt decodePdf;

    /**flag to show if we display messages*/
    public static final boolean showMessages=true;

    /**location output files written to*/
    private String outputDir = "";

    /**user-supplied co-ords*/
    private static int defX1=-1,defX2,defY1,defY2;

    /**option to set output as Xxml or CSV*/
    private boolean isCSV=true;

    /**sample file which can be setup - substitute your own.
     * If a directory is given, all the files in the directory will be processed*/
    private static final String testFile = "/mnt/shared/storypad/input/samples_table_grouping/sampleTable.pdf";

    /**example method to open a file and extract the raw text
     *
     * @param file_name A String value of the file to search
     */
    public ExtractTextInRectangleAsTable(String file_name) {

//    get any user set dpi
        final String xmlFlag=System.getProperty("xml");
        if(xmlFlag!=null){
            this.isCSV=false;
        }

        if(file_name==null) {
            file_name = testFile;
        }

        //check output dir has separator
        if (!user_dir.endsWith(separator)) {
            user_dir += separator;
        }

        //create a directory if it doesn't exist
        final File output_path = new File(outputDir);
        if (!output_path.exists()) {
            output_path.mkdirs();
        }

        /**
         * if file name ends pdf, do the file otherwise
         * do every pdf file in the directory. We already know file or
         * directory exists so no need to check that, but we do need to
         * check its a directory
         */
        if (file_name.toLowerCase().endsWith(".pdf")) {
            decodeFile(file_name);
        } else {

            /**
             * get list of files and check directory
             */
            String[] files = null;
            final File inputFiles;

            /**make sure name ends with a deliminator for correct path later*/
            if (!file_name.endsWith(separator)) {
                file_name += separator;
            }

            try {
                inputFiles = new File(file_name);

                if (!inputFiles.isDirectory()) {
                    System.err.println(
                            file_name + " is not a directory. Exiting program");
                }
                files = inputFiles.list();
            } catch (final Exception ee) {
                LogWriter.writeLog(
                        "Exception trying to access file " + ee.getMessage());
            }

            /**now work through all pdf files*/
            final long fileCount = files.length;

            for (int i = 0; i < fileCount; i++) {
                if(showMessages) {
                    System.out.println(i + "/ " + fileCount + ' ' + files[i]);
                }

                if (files[i].toLowerCase().endsWith(".pdf")) {
                    if(showMessages) {
                        System.out.println(file_name + files[i]);
                    }

                    decodeFile(file_name + files[i]);
                }
            }
        }
    }

    /**
     * routine to decode a file
     */
    private void decodeFile(final String file_name) {

        /**get just the name of the file without
         * the path to use as a sub-directory or .pdf
         */
        String name = "demo"; //set a default just in case

        final int pointer = file_name.lastIndexOf(separator);

        if (pointer != -1) {
            name = file_name.substring(pointer + 1, file_name.length() - 4);
        }

        /**
         * create output dir for tables
         */
        outputDir = user_dir + "tables" + separator + name + separator;

        //ensure a directory for data
        final File page_path = new File(outputDir + separator);
        if (!page_path.exists()) {
            page_path.mkdirs();
        }

        /**debugging code to create a log
         LogWriter.setupLogFile(true,0,"","v",false);
         LogWriter.log_name =  "/mnt/shared/log.txt";
         */

        //PdfDecoder returns a PdfException if there is a problem
        try {
            decodePdf = new PdfDecoderServer(false);
            decodePdf.setExtractionMode(PdfDecoderServer.TEXT); //extract just text
            //make sure widths in data CRITICAL if we want to split lines correctly!!
            PdfDecoderServer.init(true);

            /**
             * open the file (and read metadata including pages in  file)
             */
            if(showMessages) {
                System.out.println("Opening file :" + file_name);
            }
            decodePdf.openPdfFile(file_name);

        } catch (final Exception e) {
            System.err.println("7.Exception " + e + " in pdf code");
        }

        /**
         * extract data from pdf (if allowed).
         */
        if(!decodePdf.isExtractionAllowed()){
            System.out.println("Text extraction not allowed");
        }else if (decodePdf.isEncrypted() && !decodePdf.isPasswordSupplied()) {
            System.out.println("Encrypted settings");
            System.out.println(
                    "Please look at Viewer for code sample to handle such files");
            System.out.println("Or get support/consultancy");
        } else {
            //page range
            final int start = 1;
            final int end = decodePdf.getPageCount();

            /**
             * extract data from pdf
             */
            try {
                for (int page = start; page < end + 1; page++) { //read pages

                    //decode the page
                    decodePdf.decodePage(page);

                    /** create a grouping object to apply grouping to data*/
                    final PdfGroupingAlgorithms currentGrouping =decodePdf.getGroupingObject();

                    /**use whole page size for  demo - get data from PageData object*/
                    final PdfPageData currentPageData = decodePdf.getPdfPageData();

                    final int x1;
                    final int y1;
                    final int x2;
                    final int y2;

                    if(defX1==-1){
                        x1 = currentPageData.getMediaBoxX(page);
                        x2 = currentPageData.getMediaBoxWidth(page)+x1;

                        y2 = currentPageData.getMediaBoxY(page);
                        y1 = currentPageData.getMediaBoxHeight(page)+y2;
                    }else{
                        x1=defX1;
                        y1=defY1;
                        x2=defX2;
                        y2=defY2;
                    }

                    //tell user
                    if(showMessages) {
                        System.out.println(
                                "Extracting text from rectangle as table("
                                        + x1
                                        + ','
                                        + y1
                                        + ' '
                                        + x2
                                        + ','
                                        + y2
                                        + ')');
                    }

                    //default for xml
                    String ending=".xml";

                    //tell user type of content
                    if(isCSV){
                        if(showMessages) {
                            System.out.println("Table will be in CSV format");
                        }
                        ending=".csv";
                    }else{
                        if(showMessages) {
                            System.out.println("Table will be in xml format");
                        }
                    }

                    /**Co-ordinates are x1,y1 (top left hand corner), x2,y2(bottom right) */

                    /**The call to extract the table*/
                    final Map tableContent;
                    String tableText=null;

                    try{
                        //the source code for this grouping is in the customer area
                        //in class pdfGroupingAlgorithms
                        //all these settings are defined in the Java
                        tableContent =currentGrouping.extractTextAsTable(
                                x1,
                                y1,
                                x2,
                                y2,
                                page,
                                isCSV,
                                false,
                                false,false,0);

                        //get the text from the Map object
                        tableText=(String)tableContent.get("content");

                    } catch (final PdfException e) {
                        decodePdf.closePdfFile();
                        System.err.println("Exception " + e.getMessage()+" with table extraction");
                    }

                    if (tableText == null) {
                        if(showMessages) {
                            System.out.println("No text found");
                        }
                    } else {

                        /**
                         * output the data - you may wish to alter the encoding to suit
                         */
                        final OutputStreamWriter output_stream =
                                new OutputStreamWriter(
                                        new FileOutputStream(outputDir + page + ending),
                                        "UTF-8");

                        if(showMessages) {
                            System.out.println(
                                    "Writing to " + outputDir + page + ending);
                        }

                        //xml header
                        if(!isCSV) {
                            output_stream.write(
                                    "<xml><BODY>\n\n");
                        }

                        //NOTE DATA IS TECHNICALLY UNICODE
                        output_stream.write(tableText); //write actual data

                        //xml footer
                        if(!isCSV) {
                            output_stream.write("\n</body></xml>");
                        }

                        output_stream.close();

                    }

                    //remove data once written out
                    decodePdf.flushObjectValues(false);
                }
            } catch (final Exception e) {
                decodePdf.closePdfFile();
                System.err.println("Exception " + e.getMessage());
                e.printStackTrace();
            }

            /**
             * flush data structures - not strictly required but included
             * as example
             */
            decodePdf.flushObjectValues(true); //flush any text data read

            /**tell user*/
            if(showMessages) {
                System.out.println("Text read as table");
            }

        }

        /**close the pdf file*/
        decodePdf.closePdfFile();

    }
    //////////////////////////////////////////////////////////////////////////
    /**
     * main routine which checks for any files passed and runs the demo
     * 
     * @param args Program arguments passed into the Viewer.
     */
    public static void main(final String[] args) {
        System.out.println("Simple demo to extract text objects as CSV or xml tables");

        //set to default
        String file_name = testFile;

        //check user has passed us a filename
        if(args.length==1){
            file_name = args[0];
            System.out.println("File :" + file_name);
        }else if(args.length==2){
            //kieran
        }else  if(args.length==5){

            file_name = args[0];
            System.out.println("File :" + file_name);

            System.out.println("User coordinates supplied");
            defX1=Integer.parseInt(args[1]);
            defY1=Integer.parseInt(args[2]);
            defX2=Integer.parseInt(args[3]);
            defY2=Integer.parseInt(args[4]);
        }else{
            System.out.println("Please call with either ");
            System.out.println("FileName");
            System.out.println("or");
            System.out.println("FileName x1 y1 x2 y2");
        }

        //check file exists
        final File pdf_file = new File(file_name);

        //if file exists, open and get number of pages
        if (!pdf_file.exists()) {
            System.out.println("File " + file_name + " not found");
        }
        new ExtractTextInRectangleAsTable(file_name);
    }
}
