package com.xebia.util.export.jodaBeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Beans {

    private List<Bean> beans = new ArrayList<>();

    private Set<String> beanClasses = new HashSet<>();

    private Map<String, Integer> index = new HashMap<>();

    public static Beans of(final Bean rootBean) {
        Beans beans = new Beans();
        JodaBeanUtils.beanIterator(rootBean).forEachRemaining(b -> {
            beans.beanClasses.add(b.metaBean().beanType().getName());
            beans.beans.add(b);
        });
        return beans;
    }

    public Optional<Bean> bean(final String name) {
        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).metaBean().beanName().equals(name)) {
                return Optional.of(beans.get(i));
            }
        }
        return Optional.empty();
    }

    public Optional<Bean> nextBean(final String name) {
        Integer idx = index.get(name);

        for (int i = idx == null ? 0 : idx; i < beans.size(); i++) {
            if (beans.get(i).metaBean().beanName().equals(name)) {
                this.index.put(name, i + 1);
                return Optional.of(beans.get(i));
            }
        }
        return Optional.empty();
    }

    public boolean isBean(final MetaProperty<?> prop) {
        return this.beanClasses.contains(prop.propertyType().getName());
    }

}
