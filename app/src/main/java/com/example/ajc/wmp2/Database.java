package com.example.ajc.wmp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class Database extends SQLiteOpenHelper
{
    private SQLiteDatabase db;
    private static final String DATABASE_NAME      = "uksw_database";
    private static final String TABLE_FIRST_NAME   = "categories";
    private static final String ID_NAME            = "id";
    private static final String NAME_NAME          = "name";
    private static final String SYNC_NAME          = "synchronization";
    private static final String TABLE_SECOND_NAME  = "announcement";
    private static final String DATE_NAME          = "date";
    private static final String TIME_NAME          = "time";
    private static final String AUTHOR_NAME        = "author";
    private static final String CATEGOTY1          = "category1";
    private static final String CATEGOTY2          = "category2";
    private static final String CATEGOTY3          = "category3";
    private static final String CATEGOTY4          = "category4";
    private static final String CATEGOTY5          = "category5";
    private static final String DESCRIPTION        = "description";
    private static final String TITLE              = "title";
    private static final String READ               = "read";
    private static final String CREATE_TABLES_FRS  = "CREATE TABLE "+TABLE_FIRST_NAME+" (" +
                                                     ID_NAME+" integer primary key autoincrement," +
                                                     NAME_NAME+" text," +
                                                     SYNC_NAME+" integer);";
    private static final String CREATE_TABLES_SEC  = "CREATE TABLE "+TABLE_SECOND_NAME+" (" +
                                                     ID_NAME+" integer primary key autoincrement," +
                                                     DATE_NAME+" text," +
                                                     TIME_NAME+" text," +
                                                     AUTHOR_NAME+" text," +
                                                     CATEGOTY1+" text," +
                                                     CATEGOTY2+" text," +
                                                     CATEGOTY3+" text," +
                                                     CATEGOTY4+" text," +
                                                     CATEGOTY5+" text," +
                                                     DESCRIPTION+" text," +
                                                     TITLE+" text," +
                                                     READ+" integer);";


    private void createArrays()
    {
        this.db.execSQL (CREATE_TABLES_FRS);
        this.db.execSQL (CREATE_TABLES_SEC);
    }


    public Database (Context context)
    {
        super(context, Environment.getExternalStorageDirectory() + File.separator+ DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate (SQLiteDatabase db)
    {
        this.db = db;
        createArrays();
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.db.execSQL ("DROP TABLE IF EXISTS "+TABLE_FIRST_NAME);
        this.db.execSQL ("DROP TABLE IF EXISTS "+TABLE_SECOND_NAME);
        createArrays();
    }


    //categories
    public boolean addCategory (String name, boolean synchronization)
    {
        if (name.isEmpty())
        {
            return false;
        }

        if (isExistCategory (name))
        {
            return true;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put (NAME_NAME, name);
        values.put (SYNC_NAME, synchronization);
        db.insertOrThrow (TABLE_FIRST_NAME,null, values);
        return true;
    }

    public boolean setSynchronizationCategory (String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        String[] columns = { NAME_NAME, SYNC_NAME};
        SQLiteDatabase db = getReadableDatabase();
        Cursor k = db.query (TABLE_FIRST_NAME, columns, null, null, null, null, null);

        while(k.moveToNext())
        {
            String name_new = k.getString(0);
            int synchronization = k.getInt(1);
            if (name_new.equals(name))
            {
                if (synchronization == 1)
                {
                    db = this.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put (SYNC_NAME, 0);
                    db.update (TABLE_FIRST_NAME, values,NAME_NAME +" = ?", new String[] { name });
                    return true;
                }
                else
                {
                    db = this.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put (SYNC_NAME, 1);
                    db.update (TABLE_FIRST_NAME, values,NAME_NAME +" = ?", new String[] { name });
                    return true;
                }
            }

        }
        return false;
    }

    public boolean isExistCategory (String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        Cursor cursor = getCategories();
        while (cursor.moveToNext())
        {
            if (cursor.getString(1).equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public Cursor getCategories ()
    {
        String[] columns = {ID_NAME, NAME_NAME, SYNC_NAME};
        SQLiteDatabase db = getReadableDatabase();
        return db.query (TABLE_FIRST_NAME, columns, null, null, null, null, null);
    }
    //categories

    //announcement
    public boolean addAnnouncement (String date, String time, String author, String category1, String category2, String category3, String category4, String category5, String description, String title)
    {
        if (isExistAnnouncement (title) == true)
        {
            return false;
        }

        if (date.isEmpty())
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(0)))
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(1)))
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(2)))
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(3)))
        {
            return false;
        }
        else if ((date.charAt(4)) != '-')
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(5)))
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(6)))
        {
            return false;
        }
        else if ((date.charAt(7)) != '-')
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(8)))
        {
            return false;
        }
        else if (!Character.isDigit(date.charAt(9)))
        {
            return false;
        }

        if (time.isEmpty())
        {
            return false;
        }
        else if (!Character.isDigit(time.charAt(0)))
        {
            return false;
        }
        else if (!Character.isDigit(time.charAt(1)))
        {
            return false;
        }
        else if ((time.charAt(2)) != ':')
        {
            return false;
        }
        else if (!Character.isDigit(time.charAt(3)))
        {
            return false;
        }
        else if (!Character.isDigit(time.charAt(4)))
        {
            return false;
        }
        else if ((time.charAt(5)) != ':')
        {
            return false;
        }
        else if (!Character.isDigit(time.charAt(6)))
        {
            return false;
        }
        else if (!Character.isDigit(time.charAt(7)))
        {
            return false;
        }

        if (author.isEmpty())
        {
            return false;
        }


        if (category1.isEmpty())
        {
            return false;
        }
        if (!isExistCategory (category1))
        {
            addCategory(category1, false);
        }

        if (!category2.isEmpty())
        {
            if (!isExistCategory(category2))
            {
                addCategory(category2, false);
            }
        }

        if (!category3.isEmpty())
        {
            if (!isExistCategory(category3))
            {
                addCategory(category3, false);
            }
        }

        if (!category4.isEmpty())
        {
            if (!isExistCategory(category4))
            {
                addCategory(category4, false);
            }
        }

        if (!category5.isEmpty())
        {
            if (!isExistCategory(category5))
            {
                addCategory(category5, false);
            }
        }

        if (description.isEmpty())
        {
            return false;
        }

        if (title.isEmpty())
        {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put (DATE_NAME, date);
        values.put (TIME_NAME, time);
        values.put (AUTHOR_NAME, author);
        values.put (CATEGOTY1, category1);
        values.put (CATEGOTY2, category2);
        values.put (CATEGOTY3, category3);
        values.put (CATEGOTY4, category4);
        values.put (CATEGOTY5, category5);
        values.put (DESCRIPTION, description);
        values.put (TITLE, title);
        values.put (READ, false);
        db.insertOrThrow (TABLE_SECOND_NAME,null, values);

        return true;
    }

    public Cursor getAnnouncements ()
    {
        String[] columns = {ID_NAME, DATE_NAME, TIME_NAME, AUTHOR_NAME, CATEGOTY1, CATEGOTY2, CATEGOTY3, CATEGOTY4, CATEGOTY5, DESCRIPTION, TITLE, READ };
        SQLiteDatabase db = getReadableDatabase();
        return db.query (TABLE_SECOND_NAME, columns, null, null, null, null, null);
    }

    public boolean setAnnouncementRead (String description)
    {
        if (description.isEmpty())
        {
            return false;
        }

        String[] columns = { DESCRIPTION, READ};
        SQLiteDatabase db = getReadableDatabase();
        Cursor k = db.query (TABLE_SECOND_NAME, columns, null, null, null, null, null);

        while (k.moveToNext())
        {
            String description_new = k.getString(0);
            int read = k.getInt(1);
            if (description_new.equals(description))
            {
                if (read == 1)
                {
                    db = this.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put (READ, 0);
                    db.update (TABLE_SECOND_NAME, values, DESCRIPTION +" = ?", new String[] { description });
                    return true;
                }
                else
                {
                    db = this.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put (READ, 1);
                    db.update (TABLE_SECOND_NAME, values, DESCRIPTION +" = ?", new String[] { description });
                    return true;
                }
            }

        }
        return false;
    }

    public boolean isExistAnnouncement (String title)
    {
        Cursor k = this.getAnnouncements();
        while (k.moveToNext())
        {
            if (k.getString(10) == title)
            {
                return true;
            }
        }
        return false;
    }
    //announcement
}
