package com.example.success.generatedDao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.example.success.entity.KnowledgeLabel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "KNOWLEDGE_LABEL".
*/
public class KnowledgeLabelDao extends AbstractDao<KnowledgeLabel, Long> {

    public static final String TABLENAME = "KNOWLEDGE_LABEL";

    /**
     * Properties of entity KnowledgeLabel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property KnowledgeId = new Property(1, Long.class, "knowledgeId", false, "KNOWLEDGE_ID");
        public final static Property LabelId = new Property(2, Long.class, "labelId", false, "LABEL_ID");
    }

    private Query<KnowledgeLabel> knowledge_KnowledgeLabelListQuery;

    public KnowledgeLabelDao(DaoConfig config) {
        super(config);
    }
    
    public KnowledgeLabelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"KNOWLEDGE_LABEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"KNOWLEDGE_ID\" INTEGER," + // 1: knowledgeId
                "\"LABEL_ID\" INTEGER);"); // 2: labelId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"KNOWLEDGE_LABEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, KnowledgeLabel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long knowledgeId = entity.getKnowledgeId();
        if (knowledgeId != null) {
            stmt.bindLong(2, knowledgeId);
        }
 
        Long labelId = entity.getLabelId();
        if (labelId != null) {
            stmt.bindLong(3, labelId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, KnowledgeLabel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long knowledgeId = entity.getKnowledgeId();
        if (knowledgeId != null) {
            stmt.bindLong(2, knowledgeId);
        }
 
        Long labelId = entity.getLabelId();
        if (labelId != null) {
            stmt.bindLong(3, labelId);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public KnowledgeLabel readEntity(Cursor cursor, int offset) {
        KnowledgeLabel entity = new KnowledgeLabel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // knowledgeId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // labelId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, KnowledgeLabel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setKnowledgeId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setLabelId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(KnowledgeLabel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(KnowledgeLabel entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(KnowledgeLabel entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "knowledgeLabelList" to-many relationship of Knowledge. */
    public List<KnowledgeLabel> _queryKnowledge_KnowledgeLabelList(Long knowledgeId) {
        synchronized (this) {
            if (knowledge_KnowledgeLabelListQuery == null) {
                QueryBuilder<KnowledgeLabel> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.KnowledgeId.eq(null));
                knowledge_KnowledgeLabelListQuery = queryBuilder.build();
            }
        }
        Query<KnowledgeLabel> query = knowledge_KnowledgeLabelListQuery.forCurrentThread();
        query.setParameter(0, knowledgeId);
        return query.list();
    }

}