package com.xebia.util.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.owasp.encoder.Encode;

import com.xebia.util.export.exception.ExportException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadExcelFileStrategy implements FileWriterStrategy {

    private ByteArrayOutputStream outStream;

    private ServletOutputStream servletOutputStream;

    private Workbook workbook;

    private SXSSFSheet sheet;

    private LongAdder counter;
    
    protected DownloadExcelFileStrategy() {
        
    }

    protected DownloadExcelFileStrategy(final String fileName, final String sheetName, final HttpServletResponse response) {
        response.setContentType(Encode.forJava(FileType.EXCEL.contentType()));
        response.setHeader(HEADER_CONTENT_DISPOSITION,
                HEADER_FILE_NAME + Encode.forJava(fileName + "." + FileType.EXCEL.extension()));

        this.outStream = new ByteArrayOutputStream();
        try {
            this.servletOutputStream = response.getOutputStream();
        } catch (IOException e) {
            throw ExportException.ioException(e);
        }
        this.workbook = new SXSSFWorkbook();
        this.sheet = (SXSSFSheet) workbook.createSheet(sheetName);
        this.counter = new LongAdder();
    }

    public static DownloadExcelFileStrategy of(@NonNull final String fileName, @NonNull final String sheetName,
            @NonNull final HttpServletResponse response) {
        return new DownloadExcelFileStrategy(fileName, sheetName, response);
    }

    @Override
    public void writeHeader(String[] columnHeaders) {
        Font headerFont = this.workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = this.workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = this.sheet.createRow(this.counter.intValue());

        for (int col = 0; col < columnHeaders.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columnHeaders[col] == null ? "" : columnHeaders[col]);
            cell.setCellStyle(headerCellStyle);
        }
        this.counter.increment();
    }

    @Override
    public void writeRow(List<String> rowData) {
        Row row = this.sheet.createRow(this.counter.intValue());
        int colIdx = 0;
        for (String columnValue : rowData) {
            row.createCell(colIdx++).setCellValue(columnValue);
        }
        this.counter.increment();
    }

    @Override
    public void cleanUp() {
        try {
            this.workbook.write(outStream);
            this.outStream.writeTo(servletOutputStream);
            this.servletOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.outStream.close();
        } catch (IOException e) {
            log.error("Error while closing csvWriter", e);
        }
        try {
            this.servletOutputStream.close();
        } catch (IOException e) {
            log.error("Error while closing csvWriter", e);
        }
        try {
            this.workbook.close();
        } catch (IOException e) {
            log.error("Error while closing csvWriter", e);
        }
    }
}
