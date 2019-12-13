package com.xebia.util.export;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;

import org.joda.beans.Bean;
import org.joda.beans.MetaProperty;
import org.joda.convert.StringConvert;

import com.xebia.util.export.exception.ExportException;
import com.xebia.util.export.jodaBeans.Beans;

import reactor.core.publisher.Flux;

final class DataToFileProcessor {

    public final static <T extends Bean> void process(final Flux<T> dataStream, final String contextName,
            final FileWriterStrategy fileWriterStrategy, final StringConvert jodaConverter,
            final boolean nullValueAsBlank) {

        T headerBean = Optional.ofNullable(dataStream.blockFirst(Duration.ofSeconds(10)))
                .orElseThrow(ExportException::emptyDatasetException);
        BeanMetaData downloadMetaData = BeanMetaData.of(headerBean, contextName);

        fileWriterStrategy.writeHeader(downloadMetaData.header());
        fileWriterStrategy.writeRow(rowData(headerBean, downloadMetaData, jodaConverter, nullValueAsBlank));


        dataStream.doOnComplete(fileWriterStrategy::cleanUp).subscribe(record -> fileWriterStrategy
                .writeRow(rowData(record, downloadMetaData, jodaConverter, nullValueAsBlank)));
    }

    private final static List<String> rowData(final Bean rootBean, final BeanMetaData downloadMetaData,
            final StringConvert jodaConverter, final boolean nullValueAsBlank) {
        Beans beans = Beans.of(rootBean);
        List<String> rowData = new ArrayList<>(downloadMetaData.columnCount());
        columnData(beans, rootBean, new LongAdder(), downloadMetaData, rowData, jodaConverter, nullValueAsBlank);
        return rowData;
    }

    private final static void columnData(final Beans beans, final Bean bean, final LongAdder counter,
            final BeanMetaData downloadMetaData, final List<String> rowData, final StringConvert jodaConverter,
            final boolean nullValueAsBlank) {
        Iterator<MetaProperty<?>> itr = bean.metaBean().metaPropertyIterable().iterator();
        while (itr.hasNext()) {
            MetaProperty<?> prop = itr.next();
            if (beans.isBean(prop)) {
                columnData(beans, beans.nextBean(prop.propertyType().getName()).get(), counter, downloadMetaData,
                        rowData, jodaConverter, nullValueAsBlank);
            } else if (prop.propertyType().isAssignableFrom(Map.class)) {
                Map<?, ?> mapAttribute = (Map<?, ?>) prop.get(beans.bean(prop.declaringType().getName()).get());
                mapAttribute.entrySet().forEach(e -> {
                    columnData(beans, beans.nextBean(e.getValue().getClass().getName()).get(), counter,
                            downloadMetaData, rowData, jodaConverter, nullValueAsBlank);
                });
            } else if (downloadMetaData.isDownloadable(counter.intValue())) {
                String fieldValue = prop.getString(bean, jodaConverter);
                if (nullValueAsBlank && fieldValue == null) {
                    rowData.add("");
                } else if (!nullValueAsBlank && fieldValue == null) {
                    rowData.add("null");
                } else {
                    rowData.add(nullValueAsBlank && fieldValue == null ? "" : fieldValue);
                }
                counter.increment();
            } else {
                counter.increment();
            }
        }
    }
}
