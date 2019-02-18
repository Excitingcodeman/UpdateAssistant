package com.gs.updateassistant;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


/**
 * @author husky
 * create on 2018/12/3-15:24
 * 带叉号的对话框
 */
public class CommonWithCloseDialog extends Dialog {

    private TextView title;
    private TextView content;
    private ImageView cancelBtn;
    private Button sureBtn;
    private ImageView resultIv;

    private  CommonWithCloseDialog instance;


    private CommonWithCloseDialog(@NonNull final Builder builder) {
        super(builder.mContext, builder.themeResId);
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(attributes);
        setContentView(R.layout.dialog_common_with_x);
        instance=this;
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        cancelBtn = findViewById(R.id.cancelBtn);
        sureBtn = findViewById(R.id.sureBtn);
        resultIv = findViewById(R.id.resultIv);
        if (TextUtils.isEmpty(builder.title)) {
            title.setVisibility(View.INVISIBLE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText(builder.title);
        }
        if (TextUtils.isEmpty(builder.content)) {
            content.setVisibility(View.INVISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            content.setText(builder.content);
        }

        if (!TextUtils.isEmpty(builder.sureString)) {
            sureBtn.setText(builder.sureString);
        }

        if (!TextUtils.isEmpty(builder.ivPath)) {
            Glide.with(getContext())
                    .load(builder.ivPath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(resultIv);
            resultIv.setVisibility(View.VISIBLE);
        } else if (builder.ivSource > 0) {
            Glide.with(getContext())
                    .load(builder.ivSource)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(resultIv);
            resultIv.setVisibility(View.VISIBLE);
        } else {
            resultIv.setVisibility(View.GONE);
        }

        if (builder.isShowClose) {
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            cancelBtn.setVisibility(View.GONE);
        }

        if (null != builder.mOnClick) {
            cancelBtn.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    builder.mOnClick.cancel(instance);
                }
            });
            sureBtn.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    builder.mOnClick.sure(instance);
                }
            });
        }
        setCanceledOnTouchOutside(false);
    }


    public static class Builder {
        private Context mContext;
        /**
         * 标题
         */
        private String title;
        /**
         * 内容
         */
        private String content;

        /**
         * 右边的按钮文字
         */
        private String sureString;
        /**
         * 点击事件
         */
        private DoubleClick mOnClick;
        /**
         * dialog的样式
         */
        private int themeResId = R.style.commonDialog;

        /**
         * 图片的地址
         */
        private String ivPath;
        /**
         * 图片的资源id
         */
        private int ivSource;

        private boolean isShowClose;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public Builder setIvPath(@NonNull String ivPath) {
            this.ivPath = ivPath;
            return this;
        }

        public Builder setShowClose(boolean showClose) {
            isShowClose = showClose;
            return this;
        }

        public Builder setIvSource(@DrawableRes int ivSource) {
            this.ivSource = ivSource;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }


        public Builder setSureString(String sureString) {
            this.sureString = sureString;
            return this;
        }

        public Builder setmOnClick(DoubleClick mOnClick) {
            this.mOnClick = mOnClick;
            return this;
        }

        public Builder setThemeResId(int themeResId) {
            this.themeResId = themeResId;
            return this;
        }

        public CommonWithCloseDialog create() {
            return new CommonWithCloseDialog(this);
        }
    }
}
