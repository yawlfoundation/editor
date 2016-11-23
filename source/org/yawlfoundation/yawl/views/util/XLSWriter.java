package org.yawlfoundation.yawl.views.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yawlfoundation.yawl.views.ontology.Triple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Michael Adams
 * @date 22/11/16
 */
public class XLSWriter {

    private Workbook _workbook;

    private int[] maxChars = new int[3];


    public XLSWriter() {
        _workbook = new XSSFWorkbook();
        _workbook.createSheet("Ontology");
    }


    public Row writeRow(String... values) {
        Row row = createRow();
        for (int i=0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            storeMaxLen(values[i], i);
         }
         return row;
    }


    public void writeHeader(String... values) {
        Row row = writeRow(values);
        CellStyle style = getHeaderCellStyle();
        for (int i=0; i < row.getPhysicalNumberOfCells(); i++) {
            row.getCell(i).setCellStyle(style);
        }
    }


    public void writeRow(Triple triple) {
        writeRow(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public void fixColumnWidths() {
        for (int i = 0; i< maxChars.length; i++) {
            int width = (int)(maxChars[i] * 1.14388) * 256;
            getSheet().setColumnWidth(i, width);
        }
    }


    public boolean output(String fileName) {
        File file = new File(fileName);
        return file.canWrite() && output(file);
    }


    public boolean output(File file) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            _workbook.write(fileOut);
            fileOut.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Sheet getSheet() {
        return _workbook.getSheetAt(0);
    }


    private Row createRow() {
        Sheet sheet = getSheet();
        int rowCount = sheet.getPhysicalNumberOfRows();
        return sheet.createRow(rowCount);
    }


    private CellStyle getHeaderCellStyle() {
        Font font = _workbook.createFont();
        font.setFontHeightInPoints((short)13);
        font.setFontName("Calibri");
        font.setBold(true);
        CellStyle style = _workbook.createCellStyle();
        style.setFont(font);
        return style;
    }


    private void storeMaxLen(String s, int i) {
        if (i >= maxChars.length) {
            maxChars = Arrays.copyOf(maxChars, i + 1);
        }
        if (s != null && s.length() > maxChars[i]) {
            maxChars[i] = s.length();
        }
    }

}
