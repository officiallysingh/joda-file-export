package com.xebia.util.export;

public enum FileType {

    // @formatter:off
    CSV("csv", "text/csv"),
    EXCEL("xlsx", "application/ms-excel");
    // @formatter:on

    private final String extension;

    private final String contentType;

    private FileType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String extension() {
        return this.extension;
    }

    public String contentType() {
        return this.contentType;
    }
    
    public boolean isCSV() {
        return this == CSV;
    }

    public boolean isExcel() {
        return this == EXCEL;
    }
}
