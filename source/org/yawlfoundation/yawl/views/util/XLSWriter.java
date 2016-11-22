package org.yawlfoundation.yawl.views.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yawlfoundation.yawl.views.ontology.Triple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 22/11/16
 */
public class XLSWriter {

    Workbook _workbook;

    public XLSWriter() {
        _workbook = new XSSFWorkbook();
        _workbook.createSheet("Ontology");
    }


    public Row writeRow(String... values) {
        Row row = createRow();
        for (int i=0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
         }
         return row;
    }


    public void writeRow(Triple triple) {
        writeRow(triple.getSubject(), triple.getPredicate(), triple.getObject());
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
}
