package com.pdfchat.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pdfchat.data.db.entities.TextChunkEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TextChunkDao_Impl implements TextChunkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TextChunkEntity> __insertionAdapterOfTextChunkEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPdfId;

  public TextChunkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTextChunkEntity = new EntityInsertionAdapter<TextChunkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `text_chunks` (`id`,`pdfId`,`content`,`pageNumber`,`chunkIndex`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TextChunkEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPdfId());
        statement.bindString(3, entity.getContent());
        statement.bindLong(4, entity.getPageNumber());
        statement.bindLong(5, entity.getChunkIndex());
      }
    };
    this.__preparedStmtOfDeleteByPdfId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM text_chunks WHERE pdfId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<TextChunkEntity> chunks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTextChunkEntity.insert(chunks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByPdfId(final long pdfId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPdfId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, pdfId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByPdfId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllChunks(final Continuation<? super List<TextChunkEntity>> $completion) {
    final String _sql = "SELECT * FROM text_chunks";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TextChunkEntity>>() {
      @Override
      @NonNull
      public List<TextChunkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPdfId = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfChunkIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "chunkIndex");
          final List<TextChunkEntity> _result = new ArrayList<TextChunkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TextChunkEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPdfId;
            _tmpPdfId = _cursor.getLong(_cursorIndexOfPdfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final int _tmpChunkIndex;
            _tmpChunkIndex = _cursor.getInt(_cursorIndexOfChunkIndex);
            _item = new TextChunkEntity(_tmpId,_tmpPdfId,_tmpContent,_tmpPageNumber,_tmpChunkIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
