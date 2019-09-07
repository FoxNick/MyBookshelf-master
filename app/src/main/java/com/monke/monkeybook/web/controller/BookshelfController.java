package com.monke.monkeybook.web.controller;

import android.text.TextUtils;

import com.monke.monkeybook.bean.BookContentBean;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.bean.ChapterBean;
import com.monke.monkeybook.dao.DbHelper;
import com.monke.monkeybook.help.BookshelfHelp;
import com.monke.monkeybook.help.ChapterContentHelp;
import com.monke.monkeybook.model.WebBookModel;
import com.monke.monkeybook.utils.GsonUtils;
import com.monke.monkeybook.web.utils.ReturnData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookshelfController {

    public ReturnData getBookshelf() {
        List<BookShelfBean> shelfBeans = BookshelfHelp.queryAllBook();
        ReturnData returnData = new ReturnData();
        if (shelfBeans.isEmpty()) {
            return returnData.setErrorMsg("还没有添加小说");
        }
        return returnData.setData(shelfBeans);
    }

    public ReturnData getChapterList(Map<String, List<String>> parameters) {
        List<String> strings = parameters.get("url");
        ReturnData returnData = new ReturnData();
        if (strings == null) {
            return returnData.setErrorMsg("参数url不能为空，请指定书籍地址");
        }
        List<ChapterBean> chapterList = BookshelfHelp.queryChapterList(strings.get(0));
        return returnData.setData(chapterList);
    }
    private String getParam(Map<String, List<String>> parameters,String key){
        try{
            return Objects.requireNonNull(parameters.get(key)).get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public ReturnData getBookContent(Map<String, List<String>> parameters) {
        String strings = getParam(parameters,"url");
        String noteurl =  getParam(parameters,"noteurl");
        String index =  getParam(parameters,"index");
        String name =  getParam(parameters,"name");
        ReturnData returnData = new ReturnData();
        if (strings == null) {
            return returnData.setErrorMsg("参数url不能为空，请指定内容地址");
        }
        if (noteurl == null) {
            return returnData.setErrorMsg("参数noteurl不能为空，请指定书籍地址");
        }
        ChapterBean chapter = new ChapterBean();
        chapter.setDurChapterUrl(getParam(parameters,"url"));
        chapter.setNoteUrl(noteurl);
        chapter.setDurChapterIndex(Integer.valueOf(index));
        chapter.setDurChapterName(name);

        BookShelfBean bookShelfBean = BookshelfHelp.queryBookByUrl(chapter.getNoteUrl());
        if (bookShelfBean == null) {
            return returnData.setErrorMsg("书架没有此书");
        }
        String content = ChapterContentHelp.getChapterCache(bookShelfBean, chapter);
        if (!TextUtils.isEmpty(content)) {
            return returnData.setData(content);
        }
        try {
            BookContentBean bookContentBean = WebBookModel.getInstance().getBookContent(bookShelfBean.getBookInfoBean(), chapter).blockingFirst();
            return returnData.setData(bookContentBean.getDurChapterContent());
        } catch (Exception e) {
            return returnData.setErrorMsg(e.getMessage());
        }
    }

    public ReturnData saveBook(String postData) {
        BookShelfBean bookShelfBean = GsonUtils.parseJObject(postData, BookShelfBean.class);
        ReturnData returnData = new ReturnData();
        if (bookShelfBean != null) {
            DbHelper.getInstance().getDaoSession().getBookShelfBeanDao().insertOrReplace(bookShelfBean);
            return returnData.setData("");
        }
        return returnData.setErrorMsg("格式不对");
    }

}
