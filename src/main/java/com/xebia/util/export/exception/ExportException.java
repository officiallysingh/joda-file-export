package com.xebia.util.export.exception;

import java.io.IOException;

public class ExportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorCodeType exceptionType;

    public enum ExportExceptionType implements ErrorCodeType {

        //@formatter:off
        IO_EXCEPTION("IO exception"),
        EMPTY_DATA_SET_EXCEPTION("Empty data set"),
        INVALID_META_DATA_EXCEPTION("Invalid meta data");
        //@formatter:on

        private final String message;

        private ExportExceptionType(final String message) {
            this.message = message;
        }

        @Override
        public String message() {
            return this.message;
        }
    }

    public ExportException(ExportExceptionType exceptionType) {
        super(exceptionType.message);
        setExceptionType(exceptionType);
    }

    public ExportException(ExportExceptionType exceptionType, final Exception cause) {
        super(exceptionType.message, cause);
        setExceptionType(exceptionType);
    }

    public ExportException(ExportExceptionType exceptionType, final String additionalInfo) {
        super(exceptionType.message + " >>> " + additionalInfo);
        setExceptionType(exceptionType);
    }
    
    private void setExceptionType(ExportExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ErrorCodeType exceptionType() {
        return this.exceptionType;
    }

    public static ExportException emptyDatasetException() {
        return new ExportException(ExportExceptionType.EMPTY_DATA_SET_EXCEPTION);
    }

    public static ExportException ioException(final IOException cause) {
        return new ExportException(ExportExceptionType.IO_EXCEPTION, cause);
    }

    public static ExportException invalidMetaDataException(final String additionalInfo) {
        return new ExportException(ExportExceptionType.IO_EXCEPTION, additionalInfo);
    }
}
