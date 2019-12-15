package com.xebia.util.export;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import org.joda.beans.Bean;
import org.joda.beans.MetaProperty;

import com.xebia.util.export.annotation.Export;
import com.xebia.util.export.annotation.ExportOverride;
import com.xebia.util.export.annotation.ExportOverrides;
import com.xebia.util.export.exception.ExportException;
import com.xebia.util.export.jodaBeans.BeanProperty;
import com.xebia.util.export.jodaBeans.Beans;

public class BeanMetaData {

    private final Map<Integer, BeanProperty> metaData;

    private final Map<String, BeanProperty> overrideMetaData;

    private final String[] header;

    private final String contextName;

    private final int columnCount;

    private final Beans beans;

    public static BeanMetaData of(final Bean rootBean, final String contextName) {
        return new BeanMetaData(rootBean, contextName);
    }

    private BeanMetaData(final Bean rootBean, final String contextName) {
        this.contextName = contextName;
        this.metaData = new LinkedHashMap<>();
        this.overrideMetaData = new LinkedHashMap<>();
        this.beans = Beans.of(rootBean);

        prepareBeanMetaData(rootBean, new LongAdder(), Optional.empty(), new StringBuilder(200));

        Set<String> validPropPathSet = this.metaData.values().stream().map(BeanProperty::qualifiedName)
                .collect(Collectors.toSet());
        this.overrideMetaData.keySet().forEach(propPath -> {
            if (!validPropPathSet.contains(propPath)) {
                throw ExportException.invalidMetaDataException(
                        "Illegal field name: " + propPath + "in @DownloadOverrides or @DownloadOverride");
            }
        });

        List<String> headerValues = this.metaData.values().stream().filter(BeanProperty::isDownloadable)
                .map(BeanProperty::columnName).collect(Collectors.toList());

        this.columnCount = headerValues.size();
        this.header = headerValues.toArray(new String[this.columnCount]);
    }

    private void prepareBeanMetaData(final Bean bean, final LongAdder counter, final Optional<String> columnNamePrefix,
            final StringBuilder propertyPath) {
        Iterator<MetaProperty<?>> itr = bean.metaBean().metaPropertyIterable().iterator();
        while (itr.hasNext()) {
            MetaProperty<?> prop = itr.next();
            if (this.beans.isBean(prop)) {
                try {
                    ExportOverrides downloadOverridesAnnotation = prop.annotation(ExportOverrides.class);
                    try {
                        prop.annotation(ExportOverride.class);
                        throw ExportException.invalidMetaDataException("The field " + prop.name() + " in class: "
                                + prop.declaringType().getName()
                                + " is annotated with both @DownloadOverrides and @DownloadOverride, can use either but not both on a particular field");
                    } catch (NoSuchElementException ex) {
                        // Ignore
                    }
                    try {
                        prop.annotation(Export.class);
                        throw ExportException.invalidMetaDataException(
                                "Field: " + prop.name() + " in class: " + prop.declaringType().getName()
                                        + " is of bean type, so @Download is not allowed here");
                    } catch (NoSuchElementException ex) {
                        // Ignore
                    }
                    for (ExportOverride downloadOverrideAnnotation : downloadOverridesAnnotation.value()) {
                        addOverrideBeanProperty(propertyPath.toString() + prop.name(), downloadOverrideAnnotation);
                    }
                } catch (NoSuchElementException e) {
                    try {
                        ExportOverride downloadOverrideAnnotation = prop.annotation(ExportOverride.class);
                        addOverrideBeanProperty(propertyPath.toString() + prop.name(), downloadOverrideAnnotation);
                    } catch (NoSuchElementException ex) {
                        // Ignore
                    }
                }
                prepareBeanMetaData(this.beans.nextBean(prop.propertyType().getName()).get(), counter, columnNamePrefix,
                        propertyPath.append(prop.name()).append("."));
                propertyPath.setLength(propertyPath.length() - (prop.name().length() + 1));
            } else if (prop.propertyType().isAssignableFrom(Map.class)) {
                Map<?, ?> mapAttribute = (Map<?, ?>) prop.get(this.beans.bean(prop.declaringType().getName()).get());
                mapAttribute.entrySet().forEach(e -> {
                    if (e.getKey().getClass().isAssignableFrom(Distinguishable.class)) {
                        Distinguishable describe = (Distinguishable) e.getKey();

                        Optional<Bean> nextBean = this.beans.nextBean(e.getValue().getClass().getName());
                        if (nextBean.isPresent()) {
                            prepareBeanMetaData(this.beans.nextBean(e.getValue().getClass().getName()).get(), counter,
                                    Optional.ofNullable(describe.label()), propertyPath.append(prop.name()).append(".")
                                            .append(describe.descriminator()).append("."));
                            propertyPath.setLength(
                                    propertyPath.length() - (prop.name().length() + describe.label().length() + 2));
                        } else {
                            prepareBeanLiteralPropertyMetaData(prop, counter, columnNamePrefix, propertyPath);
                        }
                    } else {
                        Optional<Bean> nextBean = this.beans.nextBean(e.getValue().getClass().getName());
                        if (nextBean.isPresent()) {
                            prepareBeanMetaData(nextBean.get(), counter, Optional.ofNullable(e.getKey().toString()),
                                    propertyPath.append(prop.name()).append(".").append(e.getKey().toString())
                                            .append("."));
                            propertyPath.setLength(propertyPath.length()
                                    - (prop.name().length() + e.getKey().toString().length() + 2));
                        } else {
                            prepareBeanLiteralPropertyMetaData(prop, counter, columnNamePrefix, propertyPath);
                        }
                    }
                });
            } else {
                prepareBeanLiteralPropertyMetaData(prop, counter, columnNamePrefix, propertyPath);
            }
        }
    }

    private void prepareBeanLiteralPropertyMetaData(final MetaProperty<?> prop, final LongAdder counter,
            final Optional<String> columnNamePrefix, final StringBuilder propertyPath) {
        propertyPath.append(prop.name());
        String propQualifiedName = propertyPath.toString();
        if (this.overrideMetaData.containsKey(propQualifiedName)) {
            if (columnNamePrefix.isPresent()) {
                this.metaData.put(counter.intValue(),
                        this.overrideMetaData.get(propQualifiedName).addPrefixToColumnName(columnNamePrefix.get()));
            } else {
                this.metaData.put(counter.intValue(), this.overrideMetaData.get(propQualifiedName));
            }
        } else {
            try {
                Export downloadAnnotation = prop.annotation(Export.class);
                this.metaData.put(counter.intValue(), BeanProperty.of(prop.name(), propQualifiedName,
                        columnNamePrefix.isPresent() ? columnNamePrefix.get() + " " + downloadAnnotation.columnName()
                                : downloadAnnotation.columnName(),
                        downloadAnnotation.contexts().length == 0 ? true
                                : this.contextName.equalsIgnoreCase(FileExportContext.DEFAULT_CONTEXT) ? true
                                        : Arrays.asList(downloadAnnotation.contexts()).contains(this.contextName)));
            } catch (NoSuchElementException ex) {
                throw ExportException.invalidMetaDataException("@Download annotation is required on the field "
                        + prop.name() + " in class: " + prop.declaringType().getName());
            }
        }
        propertyPath.setLength(propertyPath.length() - prop.name().length());
        counter.increment();
    }

    private void addOverrideBeanProperty(final String propertyPath, ExportOverride downloadOverrideAnnotation) {
        String propQualifiedName = propertyPath + "." + downloadOverrideAnnotation.fieldName();
        BeanProperty overrideProperty = BeanProperty.of(
                downloadOverrideAnnotation.fieldName().substring(
                        downloadOverrideAnnotation.fieldName().lastIndexOf(".") + 1,
                        downloadOverrideAnnotation.fieldName().length()),
                propQualifiedName, downloadOverrideAnnotation.download().columnName(),
                downloadOverrideAnnotation.download().contexts().length == 0 ? true
                        : this.contextName.equalsIgnoreCase(FileExportContext.DEFAULT_CONTEXT) ? true
                                : Arrays.asList(downloadOverrideAnnotation.download().contexts())
                                        .contains(this.contextName));
        this.overrideMetaData.put(propQualifiedName, overrideProperty);
    }

    public String[] header() {
        return this.header;
    }

    public int columnCount() {
        return this.columnCount;
    }

    public String contextName() {
        return this.contextName;
    }

    public boolean isDownloadable(final int index) {
        return this.metaData.get(index).isDownloadable();
    }
}
