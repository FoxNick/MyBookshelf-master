package com.monke.monkeybook.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.monke.monkeybook.bean.SearchBookBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEARCH_BOOK_BEAN".
*/
public class SearchBookBeanDao extends AbstractDao<SearchBookBean, String> {

    public static final String TABLENAME = "SEARCH_BOOK_BEAN";

    /**
     * Properties of entity SearchBookBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property NoteUrl = new Property(0, String.class, "noteUrl", true, "NOTE_URL");
        public final static Property CoverUrl = new Property(1, String.class, "coverUrl", false, "COVER_URL");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Author = new Property(3, String.class, "author", false, "AUTHOR");
        public final static Property Tag = new Property(4, String.class, "tag", false, "TAG");
        public final static Property Kind = new Property(5, String.class, "kind", false, "KIND");
        public final static Property Origin = new Property(6, String.class, "origin", false, "ORIGIN");
        public final static Property LastChapter = new Property(7, String.class, "lastChapter", false, "LAST_CHAPTER");
        public final static Property Introduce = new Property(8, String.class, "introduce", false, "INTRODUCE");
        public final static Property BookType = new Property(9, String.class, "bookType", false, "BOOK_TYPE");
        public final static Property VariableString = new Property(10, String.class, "variableString", false, "VARIABLE_STRING");
        public final static Property AddTime = new Property(11, Long.class, "addTime", false, "ADD_TIME");
    }


    public SearchBookBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SearchBookBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEARCH_BOOK_BEAN\" (" + //
                "\"NOTE_URL\" TEXT PRIMARY KEY NOT NULL ," + // 0: noteUrl
                "\"COVER_URL\" TEXT," + // 1: coverUrl
                "\"NAME\" TEXT," + // 2: name
                "\"AUTHOR\" TEXT," + // 3: author
                "\"TAG\" TEXT," + // 4: tag
                "\"KIND\" TEXT," + // 5: kind
                "\"ORIGIN\" TEXT," + // 6: origin
                "\"LAST_CHAPTER\" TEXT," + // 7: lastChapter
                "\"INTRODUCE\" TEXT," + // 8: introduce
                "\"BOOK_TYPE\" TEXT," + // 9: bookType
                "\"VARIABLE_STRING\" TEXT," + // 10: variableString
                "\"ADD_TIME\" INTEGER);"); // 11: addTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEARCH_BOOK_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SearchBookBean entity) {
        stmt.clearBindings();
 
        String noteUrl = entity.getNoteUrl();
        if (noteUrl != null) {
            stmt.bindString(1, noteUrl);
        }
 
        String coverUrl = entity.getCoverUrl();
        if (coverUrl != null) {
            stmt.bindString(2, coverUrl);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(4, author);
        }
 
        String tag = entity.getTag();
        if (tag != null) {
            stmt.bindString(5, tag);
        }
 
        String kind = entity.getKind();
        if (kind != null) {
            stmt.bindString(6, kind);
        }
 
        String origin = entity.getOrigin();
        if (origin != null) {
            stmt.bindString(7, origin);
        }
 
        String lastChapter = entity.getLastChapter();
        if (lastChapter != null) {
            stmt.bindString(8, lastChapter);
        }
 
        String introduce = entity.getIntroduce();
        if (introduce != null) {
            stmt.bindString(9, introduce);
        }
 
        String bookType = entity.getBookType();
        if (bookType != null) {
            stmt.bindString(10, bookType);
        }
 
        String variableString = entity.getVariableString();
        if (variableString != null) {
            stmt.bindString(11, variableString);
        }
 
        Long addTime = entity.getAddTime();
        if (addTime != null) {
            stmt.bindLong(12, addTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SearchBookBean entity) {
        stmt.clearBindings();
 
        String noteUrl = entity.getNoteUrl();
        if (noteUrl != null) {
            stmt.bindString(1, noteUrl);
        }
 
        String coverUrl = entity.getCoverUrl();
        if (coverUrl != null) {
            stmt.bindString(2, coverUrl);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(4, author);
        }
 
        String tag = entity.getTag();
        if (tag != null) {
            stmt.bindString(5, tag);
        }
 
        String kind = entity.getKind();
        if (kind != null) {
            stmt.bindString(6, kind);
        }
 
        String origin = entity.getOrigin();
        if (origin != null) {
            stmt.bindString(7, origin);
        }
 
        String lastChapter = entity.getLastChapter();
        if (lastChapter != null) {
            stmt.bindString(8, lastChapter);
        }
 
        String introduce = entity.getIntroduce();
        if (introduce != null) {
            stmt.bindString(9, introduce);
        }
 
        String bookType = entity.getBookType();
        if (bookType != null) {
            stmt.bindString(10, bookType);
        }
 
        String variableString = entity.getVariableString();
        if (variableString != null) {
            stmt.bindString(11, variableString);
        }
 
        Long addTime = entity.getAddTime();
        if (addTime != null) {
            stmt.bindLong(12, addTime);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public SearchBookBean readEntity(Cursor cursor, int offset) {
        SearchBookBean entity = new SearchBookBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // noteUrl
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // coverUrl
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // author
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // tag
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // kind
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // origin
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // lastChapter
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // introduce
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // bookType
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // variableString
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11) // addTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SearchBookBean entity, int offset) {
        entity.setNoteUrl(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCoverUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAuthor(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTag(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setKind(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOrigin(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setLastChapter(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIntroduce(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBookType(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setVariableString(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setAddTime(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
     }
    
    @Override
    protected final String updateKeyAfterInsert(SearchBookBean entity, long rowId) {
        return entity.getNoteUrl();
    }
    
    @Override
    public String getKey(SearchBookBean entity) {
        if(entity != null) {
            return entity.getNoteUrl();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SearchBookBean entity) {
        return entity.getNoteUrl() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
