package com.wwr.apipost.handle.apipost.config.prefix.table;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventObject;

/**
 *
 * 前置服务Url配置表的操作
 *
 * @author linyuan
 **/
public class PrefixUrlTable extends JBTable{
    /**
     * 表头
     */
    private static final String[] tableHeaderName = new String[] {"Module Name", "Prefix Url"};

    public PrefixUrlTable() {

        setModel(defaultTableModel());
        setColumnModel(defaultTableColumnModel());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        Dimension tableSize = getPreferredSize();
        tableSize.width = Integer.MAX_VALUE;
        setPreferredScrollableViewportSize(tableSize);
        setFillsViewportHeight(Boolean.TRUE);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    repaint();
                }
            }
        });
    }

    public static DefaultTableModel defaultTableModel() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(tableHeaderName.length);
        return tableModel;
    }

    public static DefaultTableColumnModel defaultTableColumnModel() {
        CustomTableColumnModel model = new CustomTableColumnModel();
        TableColumn column1 = new TableColumn(0);
        column1.setHeaderValue(tableHeaderName[0]);
        column1.setPreferredWidth(100);
        column1.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject event) {
                return false;
            }
        });
        model.addColumn(column1);

        TableColumn column2 = new TableColumn(1);
        column2.setHeaderValue(tableHeaderName[1]);
        column2.setCellEditor(new DefaultCellEditor(new JTextField()));
        column2.setPreferredWidth(100);
        model.addColumn(column2);
        return model;
    }

}
