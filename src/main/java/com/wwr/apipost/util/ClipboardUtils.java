package com.wwr.apipost.util;

import lombok.experimental.UtilityClass;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@UtilityClass
public class ClipboardUtils {

    /**
     * 设置剪贴版内容
     */
    public static void setClipboard(String content) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(content);
        clipboard.setContents(selection, null);
    }

}
