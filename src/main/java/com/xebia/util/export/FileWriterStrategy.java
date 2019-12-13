package com.xebia.util.export;

import java.util.List;

public interface FileWriterStrategy {
    
    String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    String HEADER_FILE_NAME = "attachment; fileName=";

    public void writeHeader(final String[] columnHeaders);
    
    public void writeRow(final List<String> rowData);
    
    public void cleanUp();
}
