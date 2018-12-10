package edu.uoc.curs.bookshare.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);

    @Delete
    void delete (Book book);

    @Query("DELETE FROM book_table")
    void deleteAll();

    @Query("SELECT * from book_table ORDER BY isbn13 ASC")
    LiveData<List<Book>> getAllBooks();
}
