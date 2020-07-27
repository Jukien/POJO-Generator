/**
 * BSD 3-Clause License
 * <p>
 * Copyright (c) 2019, yseasony
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * * Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fr.jukien.intellij.plugins.util;

import com.google.common.collect.Lists;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 24/04/2019
 *
 * @version 2.2.1
 * @since 1.0.0
 */
public class TableInfo {
    private final DbTable tableElement;
    private final String schemaName;
    private final List<DasColumn> columns;
    private final List<String> primaryKeys = new ArrayList<String>();

    public TableInfo(DbTable tableElement) {
        this.tableElement = tableElement;
        this.schemaName = DasUtil.getSchema(tableElement);

        List<DasColumn> columns = new ArrayList<DasColumn>();

        JBIterable<? extends DasColumn> columnsIter = DasUtil.getColumns(tableElement);
        List<? extends DasColumn> dasColumns = columnsIter.toList();
        for (DasColumn dasColumn : dasColumns) {
            columns.add(dasColumn);

            if (DasUtil.isPrimary(dasColumn)) {
                primaryKeys.add(dasColumn.getName());
            }
        }
        this.columns = columns;
    }

    public String getTableName() {
        return tableElement.getName();
    }

    public String getSchemaName() {
        return schemaName;
    }

    public List<DasColumn> getColumns() {
        return columns;
    }

    public List<String> getColumnsName() {
        List<String> columnsName = Lists.newArrayList();
        for (DasColumn column : columns) {
            columnsName.add(column.getName());
        }
        return columnsName;
    }

    public List<String> getPrimaryKeys() {
        return this.primaryKeys;
    }

    public List<DasColumn> getNonPrimaryColumns() {
        Set<String> pKNameSet = new HashSet<String>();
        for (String pkName : getPrimaryKeys()) {
            pKNameSet.add(pkName);
        }

        List<DasColumn> ret = new ArrayList<DasColumn>();
        for (DasColumn column : columns) {
            if (!pKNameSet.contains(column.getName())) {
                ret.add(column);
            }
        }

        return ret;
    }
}
