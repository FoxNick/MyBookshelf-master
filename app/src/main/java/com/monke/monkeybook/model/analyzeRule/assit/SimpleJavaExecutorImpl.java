package com.monke.monkeybook.model.analyzeRule.assit;

import com.monke.monkeybook.help.TextProcessor;
import com.monke.monkeybook.model.SimpleModel;
import com.monke.monkeybook.model.analyzeRule.AnalyzeUrl;
import com.monke.monkeybook.model.content.DefaultShuqi;
import com.monke.monkeybook.utils.MD5Utils;
import com.monke.monkeybook.utils.StringUtils;

import java.nio.charset.StandardCharsets;

public class SimpleJavaExecutorImpl implements SimpleJavaExecutor {

    public SimpleJavaExecutorImpl() {
    }

    @Override
    public final String ajax(String urlStr) {
        try {
            AnalyzeUrl analyzeUrl = new AnalyzeUrl(StringUtils.getBaseUrl(urlStr), urlStr);
            return SimpleModel.getResponse(analyzeUrl).blockingFirst().body();
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    @Override
    public final String base64Decode(String base64) {
        return StringUtils.base64Decode(base64);
    }


    @Override
    public String base64Encode(String string) {
        return StringUtils.base64Encode(string);
    }

    @Override
    public final String formatHtml(String string) {
        return TextProcessor.formatHtml(string);
    }

    @Override
    public  String decodeChapterContent(String string) {
        return DefaultShuqi.getInstance().decodeChapterContent(string);
    }

    @Override
    public  String Md532(String string) {
        return MD5Utils.strToMd5By32(string);
    }
}
