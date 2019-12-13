package com.zcompany.example.jodaExport.ext;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.xebia.util.export.FileType;
import com.xebia.util.export.FileWriterStrategy;
import com.xebia.util.export.exception.ExportException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExternalizedHeaderLabelsDumpCSVStrategy implements FileWriterStrategy {

    private ICsvListWriter csvWriter;

    private FileWriter fileWriter;

    private MessageSource messageSource;

    private ExternalizedHeaderLabelsDumpCSVStrategy(final String fileName, final String location,
            final MessageSource messageSource) {
        try {
            this.fileWriter = new FileWriter(
                    location + System.getProperty("file.separator") + fileName + "." + FileType.CSV.extension());
            this.csvWriter = new CsvListWriter(fileWriter, CsvPreference.STANDARD_PREFERENCE);
        } catch (IOException e) {
            throw ExportException.ioException(e);
        }
        this.messageSource = messageSource;
    }

    public static ExternalizedHeaderLabelsDumpCSVStrategy of(final String fileName, final String location,
            final MessageSource messageSource) {
        return new ExternalizedHeaderLabelsDumpCSVStrategy(fileName, location, messageSource);
    }

    @Override
    public void writeHeader(final String[] columnHeaders) {
        String[] extLabelColumnHeaders = Arrays.stream(columnHeaders)
                .map(messageKey -> this.messageSource.getMessage(messageKey, null, Locale.ENGLISH))
                .toArray(String[]::new);
        try {
            this.csvWriter.writeHeader(extLabelColumnHeaders);
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
            this.fileWriter.close();
        } catch (final IOException e) {
            log.error("Error while closing writers", e);
        }
    }
}
