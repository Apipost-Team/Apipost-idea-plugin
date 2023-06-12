package com.wwr.apipost.handle.apipost.config.prefix.button;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;

/**
 * 移除服务的前置URL
 *
 * @author linyuan
 */
public class RemoveAction extends AbstractAction {

    JTable prefixUrl;

    public RemoveAction(JTable prefixUrl) {
        super("Remove");
        this.prefixUrl = prefixUrl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = prefixUrl.getSelectedRow();
        if (selectedRow > -1) {
            DefaultTableModel model = (DefaultTableModel) prefixUrl.getModel();
            TableCellEditor cellEditor = prefixUrl.getCellEditor();
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
            model.removeRow(selectedRow);
            prefixUrl.repaint();

            int rowCount = model.getRowCount();
            if (rowCount > 0) {
                int previousRow = Math.max(selectedRow - 1, 0);
                prefixUrl.setRowSelectionInterval(previousRow, previousRow);
            }

            if (rowCount < 0 && e.getSource() instanceof JButton) {
                ((JButton) e.getSource()).setEnabled(false);
            }
        }
    }
}