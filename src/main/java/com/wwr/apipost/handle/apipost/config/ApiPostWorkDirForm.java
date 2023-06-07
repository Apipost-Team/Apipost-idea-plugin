package com.wwr.apipost.handle.apipost.config;

import com.intellij.openapi.project.Project;
import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Getter
public class ApiPostWorkDirForm {
    private JPanel contentPane;
    private JComboBox workDir;
    private JButton editWorkDir;


    public void set(String workDirs){
      if(null!=workDirs&&!"".equals(workDirs)){
          if(workDirs.contains(",")){
              for (String s : workDirs.split(",")) {
                  workDir.addItem(s);
              }
          }else{
              workDir.addItem(workDirs);
          }
      }
    }

    public void reSet(String workDirs){
        workDir.removeAllItems();
        set(workDirs);
    }

    public String get(){
        return workDir.getSelectedItem().toString();
    }

    public void buttonAction(Project project, String title){
        for (ActionListener actionListener : editWorkDir.getActionListeners()) {
            editWorkDir.removeActionListener(actionListener);
        }
        editWorkDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiPostEditWorkDirDialog.show(project,title);
            }
        });
    }

}
