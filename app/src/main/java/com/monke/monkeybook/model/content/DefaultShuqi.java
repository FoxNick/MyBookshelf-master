package com.monke.monkeybook.model.content;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monke.basemvplib.BaseModelImpl;
import com.monke.basemvplib.OkHttpHelper;
import com.monke.monkeybook.bean.BookContentBean;
import com.monke.monkeybook.bean.BookInfoBean;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.bean.BookSourceBean;
import com.monke.monkeybook.bean.ChapterBean;
import com.monke.monkeybook.bean.SearchBookBean;
import com.monke.monkeybook.help.CookieHelper;
import com.monke.monkeybook.help.Logger;

import com.monke.monkeybook.model.BookSourceManager;
import com.monke.monkeybook.model.SimpleModel;
import com.monke.monkeybook.model.analyzeRule.AnalyzeHeaders;
import com.monke.monkeybook.model.analyzeRule.AnalyzeUrl;
import com.monke.monkeybook.model.annotation.BookType;

import com.monke.monkeybook.model.content.exception.BookSourceException;
import com.monke.monkeybook.model.impl.IHttpGetApi;
import com.monke.monkeybook.model.impl.IShuqiApi;
import com.monke.monkeybook.model.impl.IStationBookModel;
import com.monke.monkeybook.utils.MD5Utils;
import com.monke.monkeybook.utils.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Response;


public class DefaultShuqi extends BaseModelImpl implements IStationBookModel {
    private static final String TAG = DefaultShuqi.class.getSimpleName();
    public String tag;
    private String name;
    private BookSourceBean bookSourceBean;
    private Map<String, String> headerMap;

    private DefaultShuqi(String tag) throws BookSourceException {
        this.tag = tag;
        bookSourceBean = BookSourceManager.getByUrl(tag);
        if (bookSourceBean != null) {
            name = bookSourceBean.getBookSourceName();
            headerMap = AnalyzeHeaders.getMap(bookSourceBean);
        }
        if (bookSourceBean == null) {
            throw new BookSourceException("没有找到当前书源");
        }
    }

    public static DefaultShuqi getInstance(String tag) throws BookSourceException {
        return new DefaultShuqi(tag);
    }

    private Map<String, String> headerMap(boolean withCookie) {
        if (headerMap == null) {
            return null;
        }

        final Map<String, String> map = new HashMap<>(headerMap);
        if (!withCookie) {
            map.remove("Cookie");
        }
        return map;
    }
    /**
     * 发现书籍
     */
    @Override
    public Observable<List<SearchBookBean>> findBook(String url, int page) {
        try {
            final AnalyzeUrl analyzeUrl = new AnalyzeUrl(tag, url, page, headerMap(false));

            return toObservable(analyzeUrl)
                    .flatMap(response -> fanalyzeSearchBook(response))
                    .onErrorResumeNext(throwable -> {
                        if (throwable instanceof IOException) {
                            return Observable.error(throwable);
                        }
                        return Observable.just(new ArrayList<>());
                    });
        } catch (Exception e) {
            Logger.e(TAG, "findBook", e);
            return Observable.just(new ArrayList<>());
        }
    }
    private Observable<List<SearchBookBean>> fanalyzeSearchBook(final String response) {
        return Observable.create(e -> {
            List<SearchBookBean> searchBooks = new ArrayList<>();
            SearchBookBean item;
            String zhuangtai;
            JsonObject root = new JsonParser().parse(response).getAsJsonObject();

                JsonArray booksArray = root.getAsJsonArray("data");
                for (JsonElement element : booksArray) {
                    JsonObject book = element.getAsJsonObject();
                    String bookId = book.get("bid").getAsString();
                    if(book.get("status").getAsInt() == 1)
                    {
                        zhuangtai = "完结";
                    }else{
                        zhuangtai = "连载";
                    }
                    item = new SearchBookBean();
                    item.setTag(tag);
                    item.setOrigin(name);
                    item.setBookType(BookType.TEXT);
                    item.setWeight(Integer.MAX_VALUE);
                    item.setAuthor(book.get("author").getAsString());
                    item.setKind(book.get("category").getAsString()+","+zhuangtai+","+book.get("words").getAsInt());
                    item.setLastChapter(book.get("last_chapter_name").getAsString());
                    item.setName(book.get("title").getAsString());
                    item.setNoteUrl("http://c1.shuqireader.com/httpserver/filecache/get_book_content_" + bookId);
                    item.setCoverUrl(book.get("cover").getAsString().replace("\\/", "/"));
                    item.setIntroduce(book.get("desc").getAsString());
                    item.putVariable("bookId", bookId);
                    searchBooks.add(item);
                }

            e.onNext(searchBooks);
            e.onComplete();
        });
    }
    /**
     * 搜索书籍
     */
    @Override
    public Observable<List<SearchBookBean>> searchBook(String content, int page) {
        return OkHttpHelper.getInstance().createService("http://read.xiaoshuo1-sm.com", IShuqiApi.class)
                .getSearchBook("is_serchpay", "3", content, page, "30")
                .flatMap(response -> analyzeSearchBook(response.body()));
    }

    private Observable<List<SearchBookBean>> analyzeSearchBook(final String response) {
        return Observable.create(e -> {
            List<SearchBookBean> searchBooks = new ArrayList<>();
            SearchBookBean item;
            String zhuangtai;
            JsonObject root = new JsonParser().parse(response).getAsJsonObject();
            JsonObject info = root.getAsJsonObject("info");
                int pageI = info.get("page").getAsInt();
                if (pageI == 1) {
                    if (root.has("aladdin")) {
                        JsonObject aladdin = root.getAsJsonObject("aladdin");
                        String bookId = aladdin.get("bid").getAsString();
                        if(aladdin.get("status").getAsInt() == 1)
                        {
                            zhuangtai = "完结";
                        }else{
                            zhuangtai = "连载";
                        }
                        item = new SearchBookBean();
                        item.setTag(tag);
                        item.setOrigin(name);
                        item.setBookType(BookType.TEXT);
                        item.setWeight(Integer.MAX_VALUE);
                        item.setAuthor(aladdin.get("author").getAsString());
                        item.setKind(aladdin.get("category").getAsString()+","+zhuangtai+","+aladdin.get("words").getAsInt());
                        item.setLastChapter(aladdin.get("latest_chapter").getAsJsonObject().get("cname").getAsString());
                        item.setName(aladdin.get("title").getAsString());
                        item.setNoteUrl("http://c1.shuqireader.com/httpserver/filecache/get_book_content_" + bookId);
                        item.setCoverUrl(aladdin.get("cover").getAsString().replace("\\/", "/"));
                        item.setIntroduce(aladdin.get("desc").getAsString());
                        item.putVariable("bookId", bookId);
                        searchBooks.add(item);
                    }
                }

            if (root.has("data")) {
                JsonArray booksArray = root.getAsJsonArray("data");
                for (JsonElement element : booksArray) {
                    JsonObject book = element.getAsJsonObject();
                    String bookId = book.get("bid").getAsString();
                    if(book.get("status").getAsInt() == 1)
                    {
                        zhuangtai = "完结";
                    }else{
                        zhuangtai = "连载";
                    }
                    item = new SearchBookBean();
                    item.setTag(tag);
                    item.setOrigin(name);
                    item.setBookType(BookType.TEXT);
                    item.setWeight(Integer.MAX_VALUE);
                    item.setAuthor(book.get("author").getAsString());
                    item.setKind(book.get("category").getAsString()+","+zhuangtai+","+book.get("words").getAsInt());
                    item.setLastChapter(book.get("first_chapter").getAsString());
                    item.setName(book.get("title").getAsString());
                    item.setNoteUrl("http://c1.shuqireader.com/httpserver/filecache/get_book_content_" + bookId);
                    item.setCoverUrl(book.get("cover").getAsString().replace("\\/", "/"));
                    item.setIntroduce(book.get("desc").getAsString());
                    item.putVariable("bookId", bookId);
                    searchBooks.add(item);
                }
            }
            e.onNext(searchBooks);
            e.onComplete();
        });
    }

    /**
     * 网络请求并解析书籍信息
     */
    @Override
    public Observable<BookShelfBean> getBookInfo(BookShelfBean bookShelfBean) {
        String bid = bookShelfBean.getVariable("bookId");
        String Data = bid + "1514984538213800000037e81a9d8f02596e1b895d07c171d5c9";
        String Sign = MD5Utils.strToMd5By32(Data);
        HashMap<String, String> fieldMap = new HashMap<>();
        fieldMap.put("timestamp", "1514984538213");
        fieldMap.put("user_id", "8000000");
        fieldMap.put("bookId", bid);
        fieldMap.put("sign", Sign);
        return OkHttpHelper.getInstance().createService("http://walden1.shuqireader.com", IShuqiApi.class)
                .getBookDetail(fieldMap)
                .flatMap(response -> analyzeBookInfo(response.body(), bookShelfBean));
    }

    private Observable<BookShelfBean> analyzeBookInfo(String s, final BookShelfBean bookShelfBean) {
        return Observable.create(e -> {
            if (TextUtils.isEmpty(s)) {
                e.onError(new Throwable("书籍信息获取失败"));
                e.onComplete();
                return;
            }
            JsonObject root = new JsonParser().parse(s).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");
            JsonObject jsonx = data.getAsJsonObject("lastChapter");
            String chapterName = jsonx.get("chapterName").getAsString();
            BookInfoBean bookInfoBean = bookShelfBean.getBookInfoBean();
            bookShelfBean.setLastChapterName(chapterName);
            bookInfoBean.setBookType(BookType.TEXT);
            bookInfoBean.setTag(tag);
            bookInfoBean.setOrigin(name);
            bookInfoBean.setCoverUrl(data.get("imgUrl").getAsString());
            bookInfoBean.setName(data.get("bookName").getAsString());
            bookInfoBean.setAuthor(data.get("authorName").getAsString());
            bookInfoBean.setIntroduce(data.get("desc").getAsString());
            bookInfoBean.setNoteUrl(bookShelfBean.getNoteUrl());   //id
            bookInfoBean.setChapterListUrl(bookShelfBean.getNoteUrl());
            e.onNext(bookShelfBean);
            e.onComplete();
        });
    }

    /**
     * 网络解析图书目录
     */
    @Override
    public Observable<List<ChapterBean>> getChapterList(BookShelfBean bookShelfBean) {
        String bid = bookShelfBean.getVariable("bookId");
        String Data = bid + "1514984538213800000037e81a9d8f02596e1b895d07c171d5c9";
        String Sign = MD5Utils.strToMd5By32(Data);
        HashMap<String, String> fieldMap = new HashMap<>();
        fieldMap.put("timestamp", "1514984538213");
        fieldMap.put("user_id", "8000000");
        fieldMap.put("bookId", bid);
        fieldMap.put("sign", Sign);
        return OkHttpHelper.getInstance().createService("http://walden1.shuqireader.com", IShuqiApi.class)
                .getChapterList(fieldMap)
                .flatMap(response -> analyzeChapterList(response.body(), bookShelfBean.getNoteUrl()));
    }

    private Observable<List<ChapterBean>> analyzeChapterList(String s, String noteUrl) {
        return Observable.create(e -> {
            List<ChapterBean> chapterBeans = new ArrayList<>();
            JsonObject root = new JsonParser().parse(s).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");
            JsonArray chapterListArray = data.getAsJsonArray("chapterList");
            for (JsonElement element : chapterListArray) {
                JsonArray volumeListsArray = element.getAsJsonObject().getAsJsonArray("volumeList");
                for (JsonElement ele : volumeListsArray) {
                    String chapterId = ele.getAsJsonObject().get("chapterId").getAsString();
                    String chapterName = ele.getAsJsonObject().get("chapterName").getAsString();
                    ChapterBean temp = new ChapterBean();
                    temp.setDurChapterUrl(noteUrl + "_" + chapterId + ".xml");   //id
                    temp.setDurChapterName(chapterName);
                    temp.setNoteUrl(noteUrl);
                    chapterBeans.add(temp);
                }
            }
            e.onNext(chapterBeans);
            e.onComplete();
        });
    }

    /**
     * 章节缓存
     */
    @Override
    public Observable<BookContentBean> getBookContent(String chapterUrl, ChapterBean chapterBean) {
        if (StringUtils.isBlank(chapterBean.getDurChapterUrl())) {
            return Observable.error(new NullPointerException("chapter url is invalid"));
        }
        return OkHttpHelper.getInstance().createService(StringUtils.getBaseUrl(chapterBean.getDurChapterUrl()), IHttpGetApi.class)
                .getWebContent(chapterBean.getDurChapterUrl(), AnalyzeHeaders.getMap(null))
                .flatMap(response -> analyzeBookContent(response.body(), chapterBean));
    }

    private Observable<BookContentBean> analyzeBookContent(String response, ChapterBean chapterBean) {
        return Observable.create(e -> {
            BookContentBean bookContentBean = new BookContentBean();
            bookContentBean.setDurChapterUrl(chapterBean.getDurChapterUrl());
            bookContentBean.setDurChapterIndex(chapterBean.getDurChapterIndex());
            bookContentBean.setDurChapterName(chapterBean.getDurChapterName());
            bookContentBean.setNoteUrl(chapterBean.getNoteUrl());
            bookContentBean.appendDurChapterContent(decodeChapterContent(getContent(response)));
            e.onNext(bookContentBean);
            e.onComplete();
        });
    }

    private static String getContent(String text) {
        Pattern pattern = Pattern.compile("(?<=\\[CDATA\\[)(\\S+)(?=\\]\\]\\>)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public String decodeChapterContent(String string) {
        if (string == null) {
            return "";
        }
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            int charAt = bytes[i];
            if (65 <= charAt && charAt <= 90) {
                charAt = charAt + 13;
                if (charAt > 90) {
                    charAt = ((charAt % 90) + 65) - 1;
                }
            } else if (97 <= charAt && charAt <= 122) {
                charAt = charAt + 13;
                if (charAt > 122) {
                    charAt = ((charAt % 122) + 97) - 1;
                }
            }
            bytes[i] = (byte) charAt;
        }
        String content = new String(bytes, StandardCharsets.UTF_8);
        return StringUtils.base64Decode(content);
    }

    private Observable<String> toObservable(AnalyzeUrl analyzeUrl) {
        return SimpleModel.getResponse(analyzeUrl)
                .flatMap(response -> setCookie(response, tag))
                .doOnNext(response -> {
                    final String requestUrl;
                    okhttp3.Response networkResponse = response.raw().networkResponse();
                    if (networkResponse != null) {
                        requestUrl = networkResponse.request().url().toString();
                    } else {
                        requestUrl = response.raw().request().url().toString();
                    }
                    analyzeUrl.setRequestUrl(requestUrl);
                })
                .map(Response::body);
    }


    private Observable<Response<String>> setCookie(Response<String> response, String tag) {
        return Observable.create((ObservableOnSubscribe<Response<String>>) e -> {
            if (!response.raw().headers("Set-Cookie").isEmpty()) {
                final StringBuilder cookieBuilder = new StringBuilder();
                for (String s : response.raw().headers("Set-Cookie")) {
                    String[] x = s.split(";");
                    for (String y : x) {
                        if (!TextUtils.isEmpty(y)) {
                            cookieBuilder.append(y).append(";");
                        }
                    }
                }
                String cookie = cookieBuilder.toString();
                if (!TextUtils.isEmpty(cookie)) {
                    CookieHelper.getInstance().replaceCookie(tag, cookie);
                }
            }
            e.onNext(response);
            e.onComplete();
        }).onErrorReturnItem(response);
    }
}