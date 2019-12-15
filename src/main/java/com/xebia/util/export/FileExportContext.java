package com.xebia.util.export;

import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.joda.beans.Bean;
import org.joda.convert.StringConvert;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileExportContext<T extends Bean> implements ExportContext<T> {

    public static final String DEFAULT_CONTEXT = "default";

    private String contextName;

    private boolean nullValueAsBlank;

    private StringConvert jodaConverter;

    private FileWriterStrategy fileWriterStrategy;

    private Iterable<T> data;

    private Flux<T> dataStream;

    @Override
    public void export() {
        if (this.data != null) {
            Iterator<? extends Bean> itr = this.data.iterator();
            Flux<? extends Bean> dataStream = Flux.generate(() -> itr, (state, sink) -> {
                if (state.hasNext()) {
                    sink.next(state.next());
                } else {
                    sink.complete();
                }
                return state;
            });
            DataToFileProcessor.process(dataStream, this.contextName, this.fileWriterStrategy, this.jodaConverter,
                    this.nullValueAsBlank);
        } else {
            DataToFileProcessor.process(this.dataStream, this.contextName, this.fileWriterStrategy, this.jodaConverter,
                    this.nullValueAsBlank);
        }
    }

    public static <T extends Bean> JodaConverterBuilder<T> of() {
        return new ExportContextBuilder<T>();
    }

    public static <T extends Bean> JodaConverterBuilder<T> of(final boolean nullValueAsBlank) {
        return new ExportContextBuilder<T>(nullValueAsBlank);
    }

    public static <T extends Bean> JodaConverterBuilder<T> of(final String contextName,
            final boolean nullValueAsBlank) {
        return new ExportContextBuilder<T>(contextName, nullValueAsBlank);
    }

    public interface JodaConverterBuilder<T extends Bean> {

        public ExportBuilder<T> withJodaConverter(final StringConvert jodaConverter);
    }

    public interface ExportBuilder<T extends Bean> {

        public DataBuilder<T> export(final FileWriterStrategy fileWriterStrategy);

        public DataBuilder<T> exportAsCSV(final String fileName, final HttpServletResponse response);

        public DataBuilder<T> exportAsExcel(final String fileName, final String sheetName,
                final HttpServletResponse response);

//        public DataBuilder dump(final FileWriterStrategy fileWriterStrategy);
//
//        public DataBuilder dumpAsCSV(final String fileName, final String directory);
//
//        public DataBuilder dumpAsExcel(final String fileName, final String sheetName, final String directory);
    }

    public interface DataBuilder<T extends Bean> {

        public ExportContext<T> from(final Iterable<T> data);

        public ExportContext<T> from(final Flux<T> dataStream);
    }

    public static class ExportContextBuilder<T extends Bean>
            implements ExportBuilder<T>, DataBuilder<T>, JodaConverterBuilder<T> {

        private String contextName;

        private boolean nullValueAsBlank;

        private StringConvert jodaConverter;

        private FileWriterStrategy fileWriterStrategy;

        ExportContextBuilder() {
            this.contextName = DEFAULT_CONTEXT;
            this.nullValueAsBlank = true;
        }

        ExportContextBuilder(final boolean nullValueAsBlank) {
            this.contextName = DEFAULT_CONTEXT;
            this.nullValueAsBlank = nullValueAsBlank;
        }

        ExportContextBuilder(final String contextName, final boolean nullValueAsBlank) {
            this.contextName = contextName;
            this.nullValueAsBlank = nullValueAsBlank;
        }

        @Override
        public ExportBuilder<T> withJodaConverter(final StringConvert jodaConverter) {
            this.jodaConverter = jodaConverter;
            return this;
        }

        @Override
        public DataBuilder<T> export(final FileWriterStrategy fileWriterStrategy) {
            this.fileWriterStrategy = fileWriterStrategy;
            return this;
        }

        @Override
        public DataBuilder<T> exportAsCSV(final String fileName, final HttpServletResponse response) {
            this.fileWriterStrategy = DownloadCSVFileStrategy.of(fileName, response);
            return this;
        }

        @Override
        public DataBuilder<T> exportAsExcel(final String fileName, final String sheetName,
                final HttpServletResponse response) {
            this.fileWriterStrategy = DownloadExcelFileStrategy.of(fileName, sheetName, response);
            return this;
        }

        @Override
        public ExportContext<T> from(final Iterable<T> data) {
            FileExportContext<T> collectionExportContext = new FileExportContext<>();
            collectionExportContext.contextName = this.contextName;
            collectionExportContext.nullValueAsBlank = this.nullValueAsBlank;
            collectionExportContext.jodaConverter = this.jodaConverter;
            collectionExportContext.fileWriterStrategy = this.fileWriterStrategy;
            collectionExportContext.data = data;
            return collectionExportContext;
        }

        @Override
        public ExportContext<T> from(final Flux<T> dataStream) {
            FileExportContext<T> reactiveExportContext = new FileExportContext<>();
            reactiveExportContext.contextName = this.contextName;
            reactiveExportContext.nullValueAsBlank = this.nullValueAsBlank;
            reactiveExportContext.jodaConverter = this.jodaConverter;
            reactiveExportContext.fileWriterStrategy = this.fileWriterStrategy;
            reactiveExportContext.dataStream = dataStream;
            return reactiveExportContext;
        }
    }
}
