package com.gs.updateassistant;

import android.app.Dialog;

/**
 * @author husky
 * create on 2018/12/3-15:34
 */
public interface DoubleClick {
    /**
     * 左边的按钮的事件
     */
    void cancel(Dialog dialog);

    /**
     * 右边确定的按钮的事件
     */
    void sure(Dialog dialog);

}
