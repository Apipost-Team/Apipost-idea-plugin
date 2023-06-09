package com.wwr.apipost.handle.apipost.config.prefix;

import com.wwr.apipost.config.domain.PrefixUrl;
import com.wwr.apipost.handle.apipost.config.ApiPostSettings;
import com.wwr.apipost.handle.apipost.config.prefix.button.AddAction;
import com.wwr.apipost.handle.apipost.config.prefix.button.RemoveAction;
import com.wwr.apipost.handle.apipost.config.prefix.table.PrefixUrlTable;
import lombok.Getter;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;

/**
 * @author linyuan
 **/
@Getter
public class ApiPostPrefixUrlSettingsForm {
    private JPanel contentPane;
    private JTable prefixUrlTable;
    private JButton addButton;
    private JButton removeButton;
    private JLabel profiles;
    private JTextField profile;

    public ApiPostPrefixUrlSettingsForm() {
        addButton.addActionListener(new AddAction(prefixUrlTable));
        addButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (!Objects.equals(removeButton, e.getOppositeComponent())) {
                    prefixUrlTable.clearSelection();
                }
            }
        });
        removeButton.addActionListener(new RemoveAction(prefixUrlTable));

        ListSelectionModel selectionModel = prefixUrlTable.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            boolean selected = !selectionModel.isSelectionEmpty();
            removeButton.setEnabled(selected);
        });

        prefixUrlTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (!Objects.equals(removeButton, e.getOppositeComponent())) {
                    prefixUrlTable.clearSelection();
                }
            }
        });

        profile.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                ApiPostSettings instance = ApiPostSettings.getInstance();
                instance.setProfile(profile.getText().trim());
            }
        });
    }

    private void createUIComponents() {
        prefixUrlTable = new PrefixUrlTable();
    }

    public void get(ApiPostSettings settings) {
        List<PrefixUrl> prefixUrlList = new ArrayList<>();
        int rowCount = prefixUrlTable.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object name = prefixUrlTable.getModel().getValueAt(i, 0);
            if (name != null && !"".equals(name)) {
                Object url = prefixUrlTable.getModel().getValueAt(i, 1);
                PrefixUrl prefixUrl = new PrefixUrl();
                prefixUrl.setModuleName(name.toString());
                prefixUrl.setPrefixUrl(Optional.ofNullable(url).map(Object::toString).orElse(""));
                prefixUrl.setRowIndex(i);
                prefixUrlList.add(prefixUrl);
            }
        }
        settings.setProfile(profile.getText().trim());
        settings.setPrefixUrlList(prefixUrlList);
    }

    public void set(ApiPostSettings settings) {
        List<PrefixUrl> prefixUrlList = settings.getPrefixUrlList();
        if (prefixUrlList != null && !prefixUrlList.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) prefixUrlTable.getModel();
            model.setRowCount(0);
            prefixUrlList.sort(Comparator.comparing(PrefixUrl::getRowIndex));
            for (PrefixUrl prefixUrl : prefixUrlList) {
                Object[] rowData = {prefixUrl.getModuleName(), prefixUrl.getPrefixUrl()};
                model.addRow(rowData);
            }
            prefixUrlTable.repaint();
        }
        if(StringUtils.isNotBlank(settings.getProfile())){
            profile.setText(settings.getProfile().trim());
        }
    }
}
