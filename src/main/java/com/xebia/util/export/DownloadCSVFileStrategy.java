package com.xebia.util.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.owasp.encoder.Encode;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.xebia.util.export.exception.ExportException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadCSVFileStrategy implements FileWriterStrategy {

    private ICsvListWriter csvWriter;
    
    protected DownloadCSVFileStrategy() {
        
    }

    protected DownloadCSVFileStrategy(final String fileName, final HttpServletResponse response) {
        response.setContentType(Encode.forJava(FileType.CSV.contentType()));
        response.setHeader(HEADER_CONTENT_DISPOSITION,
                HEADER_FILE_NAME + Encode.forJava(fileName + "." + FileType.CSV.extension()));
        
        try {
            this.csvWriter = new CsvListWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        } catch (IOException e) {
            throw ExportException.ioException(e);
        }
    }

    public static DownloadCSVFileStrategy of(@NonNull final String fileName, @NonNull final HttpServletResponse response) {
        return new DownloadCSVFileStrategy(fileName, response);
    }

    @Override
    public void writeHeader(final String[] columnHeaders) {
        try {
            this.csvWriter.writeHeader(columnHeaders);
        } catch (final IOException e) {
            throw ExportException.ioException(e);
        }
    }

    @Override
    public void writeRow(final List<String> rowData) {
        try {
            this.csvWriter.write(rowData);
        } catch (final IOException e) {
            throw ExportException.ioException(e);
        }
    }

    @Override
    public void cleanUp() {
        try {
            this.csvWriter.close();
        } catch (final IOException e) {
            log.error("Error while closing csvWriter", e);
        }
    }
}
