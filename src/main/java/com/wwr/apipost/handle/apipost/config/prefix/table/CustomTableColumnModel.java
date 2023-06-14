package com.wwr.apipost.handle.apipost.config.prefix.table;

import javax.swing.table.DefaultTableColumnModel;

/**
 * 防止用户交换列表
 *
 * @author linyuan
 */
public class CustomTableColumnModel extends DefaultTableColumnModel {
    private static final int COLUMN1_INDEX = 0;
    private static final int COLUMN2_INDEX = 1;

    @Override
    public void moveColumn(int columnIndex, int newIndex) {
        if (columnIndex == COLUMN1_INDEX || newIndex == COLUMN1_INDEX ||
            columnIndex == COLUMN2_INDEX || newIndex == COLUMN2_INDEX) {
            return;
        }
        super.moveColumn(columnIndex, newIndex);
    }
}