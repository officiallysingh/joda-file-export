package com.xebia.util.export.jodaBeans;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true, fluent = true)
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(of = "qualifiedName")
@ToString
public class BeanProperty {

    private final String name;

    private final String qualifiedName;

    private String columnName;

    @Getter(value = AccessLevel.NONE)
    private final boolean downloadable;
    
    public boolean isDownloadable() {
        return this.downloadable;
    }
    
    public BeanProperty addPrefixToColumnName(final String prefix) {
        this.columnName = prefix + " " + this.columnName;
        return this;
    }
}
