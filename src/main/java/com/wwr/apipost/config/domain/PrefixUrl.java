package com.wwr.apipost.config.domain;

import lombok.Data;

import java.util.Objects;

/**
 *
 * @author linyuan
 **/
@Data
public class PrefixUrl {

    /**
     * server module name
     */
    private String moduleName;

    /**
     * prefix url
     */
    private String prefixUrl;

    /**
     * table row number
     */
    private Integer rowIndex;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrefixUrl other = (PrefixUrl) obj;
        return Objects.equals(moduleName, other.moduleName) && Objects.equals(prefixUrl, other.prefixUrl) && Objects.equals(rowIndex, other.rowIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, prefixUrl, rowIndex);
    }
}
