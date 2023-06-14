package com.wwr.apipost.handle.apipost.config;

import lombok.Getter;

import javax.swing.*;

@Getter
public class ApiPostEditWorkDirForm {
    private JPanel contentPane;
    private JTextField workDir;

    public void set(String workDirs){
        workDir.setText(workDirs);
    }

    public String get(){
        return workDir.getText();
    }

}
