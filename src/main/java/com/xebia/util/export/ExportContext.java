package com.xebia.util.export;

import org.joda.beans.Bean;

public interface ExportContext<T extends Bean> {

    public void export();
}
