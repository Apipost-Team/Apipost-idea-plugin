package com.wwr.apipost.handle.apipost.config;

import lombok.Getter;

import javax.swing.*;

@Getter
public class ApiPostWorkDirForm {
    private JPanel contentPane;
    private JComboBox workDir;


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

    public String get(){
        return workDir.getSelectedItem().toString();
    }


}
