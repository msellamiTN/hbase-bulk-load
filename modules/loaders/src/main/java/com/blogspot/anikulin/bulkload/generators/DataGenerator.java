package com.blogspot.anikulin.bulkload.generators;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;

/**
 * Generates Tab Separated files included test data.
 *
 * @author Anatoliy Nikulin
 * email 2anikulin@gmail.com
 */
public class DataGenerator {

    private static final String ROW_DATA_FILE = "row_data.txt";
    private static final int OUTPUT_FILE_ARG = 0;
    private static final int ROWS_COUNT_ARG = 1;
    private static final int SPLITS_ARG = 2;

    private static volatile String rowData;

    /**
     * Entry point.
     *
     * [file name]    - output file name
     * [rows count]   - common lines count
     *
     * optional:
     * [splits by number] - splits all data by several files.
     * @param args input arguments
     */
    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println(
                    "Wrong input parameters. Usage: generator [file name] [rows count] (optional: [splits by number])"
            );
            return;
        }

        String outputFile = args[OUTPUT_FILE_ARG];
        long rowsCount = Long.parseLong(args[ROWS_COUNT_ARG]);
        long splitRowsCount = args.length == 3 ? Long.parseLong(args[SPLITS_ARG]) : rowsCount;

        BufferedWriter fileWriter = null;

        try {
            System.out.println("Start");
            String rowString = getRowData();

            int counter = 0;
            for (long i = 0; i < rowsCount; i++) {

                if (i % splitRowsCount == 0) {
                    IOUtils.closeQuietly(fileWriter);
                    fileWriter = createFile(outputFile, counter++);
                }

                if (fileWriter != null) {
                    fileWriter.write(
                            String.format("%d\t%s%n", i, rowString)
                    );
                } else {
                    throw new NullPointerException("Error while writer creating");
                }
            }
            System.out.println("Successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

    /**
     * Loads data from resources.
     *
     * @return Text
     * @throws IOException .
     */
    public static String getRowData() throws IOException {
        if (StringUtils.isNotBlank(rowData)) {
            return  rowData;
        }

        InputStream inputStream = null;
        StringWriter resourceWriter = null;

        try {
            inputStream = DataGenerator.class.getClassLoader().getResourceAsStream(ROW_DATA_FILE);
            resourceWriter = new StringWriter();
            IOUtils.copy(inputStream, resourceWriter);
            rowData = resourceWriter.toString();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(resourceWriter);
        }

        return rowData;
    }


    private static BufferedWriter createFile(final String filePath, final int counter) throws IOException {
        String newFileName = String.format(
                "%s%d.%s",
                FilenameUtils.removeExtension(filePath),
                counter,
                FilenameUtils.getExtension(filePath)
        );

        return new BufferedWriter(new FileWriter(newFileName));
    }
}
