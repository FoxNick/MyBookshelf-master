package com.monke.monkeybook.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.monke.monkeybook.base.MBaseActivity;

import com.monke.monkeybook.help.AppConfigHelper;
import com.monke.monkeybook.utils.StringUtils;

public class ReceivingSharedActivity extends MBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();
        String type = getIntent().getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if (openUrl(text)) {
                    SearchBookActivity.startByKey(this, text);
                }
                finish();
                return;
            }
        }
        if (Intent.ACTION_PROCESS_TEXT.equals(action) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && type != null) {
            if ("text/plain".equals(type)) {
                String text = getIntent().getStringExtra(Intent.EXTRA_PROCESS_TEXT);
                if (openUrl(text)) {
                    SearchBookActivity.startByKey(this, text);
                }
                finish();
                return;
            }
        }
        finish();
    }

    private boolean openUrl(String text) {
        if (StringUtils.isTrimEmpty(text)) {
            return false;
        }
        String[] urls = text.split("\\s");
        StringBuilder result = new StringBuilder();
        for (String url : urls) {
            if (url.matches("http.+"))
                result.append("\n").append(url.trim());
        }
        if (result.length() > 1) {
            AppConfigHelper.get().getPreferences().edit()
                    .putString("shared_url", result.toString())
                    .apply();

            Intent intent = new Intent();
            intent.setClass(ReceivingSharedActivity.this, MainActivity.class);
            this.startActivity(intent);
            return false;
        } else {
            return true;
        }
    }


}
