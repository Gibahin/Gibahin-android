package org.thoughtcrime.securesms.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.thoughtcrime.securesms.attachments.Attachment;
import org.thoughtcrime.securesms.attachments.AttachmentId;
import org.thoughtcrime.securesms.attachments.DatabaseAttachment;

public class MediaDatabase extends Database {

    private final static String MEDIA_QUERY = "SELECT " + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.ROW_ID + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.CONTENT_TYPE + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.FILE_NAME + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.THUMBNAIL_ASPECT_RATIO + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.UNIQUE_ID + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.MMS_ID + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.TRANSFER_STATE + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.SIZE + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.DATA + ", "
        + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.THUMBNAIL + ", "
        + MmsDatabase.TABLE_NAME + "." + MmsDatabase.MESSAGE_BOX + ", "
        + MmsDatabase.TABLE_NAME + "." + MmsDatabase.DATE_SENT + ", "
        + MmsDatabase.TABLE_NAME + "." + MmsDatabase.DATE_RECEIVED + ", "
        + MmsDatabase.TABLE_NAME + "." + MmsDatabase.ADDRESS + " "
        + "FROM " + AttachmentDatabase.TABLE_NAME + " LEFT JOIN " + MmsDatabase.TABLE_NAME
        + " ON " + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.MMS_ID + " = " + MmsDatabase.TABLE_NAME + "." + MmsDatabase.ID + " "
        + "WHERE " + AttachmentDatabase.MMS_ID + " IN (SELECT " + MmsSmsColumns.ID
        + " FROM " + MmsDatabase.TABLE_NAME
        + " WHERE " + MmsDatabase.THREAD_ID + " = ?) AND ("
        + AttachmentDatabase.CONTENT_TYPE + " LIKE 'image/%' OR "
        + AttachmentDatabase.CONTENT_TYPE + " LIKE 'video/%') AND "
        + AttachmentDatabase.DATA + " IS NOT NULL "
        + "ORDER BY " + AttachmentDatabase.TABLE_NAME + "." + AttachmentDatabase.ROW_ID + " DESC";

  public MediaDatabase(Context context, SQLiteOpenHelper databaseHelper) {
    super(context, databaseHelper);
  }

  public Cursor getMediaForThread(long threadId) {
    SQLiteDatabase database = databaseHelper.getReadableDatabase();
    Cursor cursor = database.rawQuery(MEDIA_QUERY, new String[]{threadId+""});
    setNotifyConverationListeners(cursor, threadId);
    return cursor;
  }

  public static class MediaRecord {
    private final AttachmentId attachmentId;
    private final long         mmsId;
    private final boolean      hasData;
    private final boolean      hasThumbnail;
    private final String       contentType;
    private final String       filename;
    private final String       address;
    private final long         date;
    private final int          transferState;
    private final long         size;

    private MediaRecord(AttachmentId attachmentId, long mmsId,
                        boolean hasData, boolean hasThumbnail,
                        String contentType, String filename, String address, long date,
                        int transferState, long size)
    {
      this.attachmentId  = attachmentId;
      this.mmsId         = mmsId;
      this.hasData       = hasData;
      this.hasThumbnail  = hasThumbnail;
      this.contentType   = contentType;
      this.filename      = filename;
      this.address       = address;
      this.date          = date;
      this.transferState = transferState;
      this.size          = size;
    }

    public static MediaRecord from(Cursor cursor) {
      AttachmentId attachmentId = new AttachmentId(cursor.getLong(cursor.getColumnIndexOrThrow(AttachmentDatabase.ROW_ID)),
                                                   cursor.getLong(cursor.getColumnIndexOrThrow(AttachmentDatabase.UNIQUE_ID)));

      long date;

      if (MmsDatabase.Types.isPushType(cursor.getLong(cursor.getColumnIndexOrThrow(MmsDatabase.MESSAGE_BOX)))) {
        date = cursor.getLong(cursor.getColumnIndexOrThrow(MmsDatabase.DATE_SENT));
      } else {
        date = cursor.getLong(cursor.getColumnIndexOrThrow(MmsDatabase.DATE_RECEIVED));
      }

      return new MediaRecord(attachmentId,
                             cursor.getLong(cursor.getColumnIndexOrThrow(AttachmentDatabase.MMS_ID)),
                             !cursor.isNull(cursor.getColumnIndexOrThrow(AttachmentDatabase.DATA)),
                             !cursor.isNull(cursor.getColumnIndexOrThrow(AttachmentDatabase.THUMBNAIL)),
                             cursor.getString(cursor.getColumnIndexOrThrow(AttachmentDatabase.CONTENT_TYPE)),
                             cursor.getString(cursor.getColumnIndexOrThrow(AttachmentDatabase.FILE_NAME)),
                             cursor.getString(cursor.getColumnIndexOrThrow(MmsDatabase.ADDRESS)),
                             date,
                             cursor.getInt(cursor.getColumnIndexOrThrow(AttachmentDatabase.TRANSFER_STATE)),
                             cursor.getLong(cursor.getColumnIndexOrThrow(AttachmentDatabase.SIZE)));
    }

    public Attachment getAttachment() {
      return new DatabaseAttachment(attachmentId, mmsId, hasData, hasThumbnail, contentType, filename, transferState, size, null, null, null);
    }

    public String getContentType() {
      return contentType;
    }

    public String getAddress() {
      return address;
    }

    public long getDate() {
      return date;
    }

  }


}
