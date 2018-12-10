package edu.uoc.curs.bookshare.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "book_table")
public class Book {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "isbn13")
    private String mIsbn13;
    @ColumnInfo(name = "isbn1")
    private String mIsbn10;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "author")
    private String mAuthor;
    @ColumnInfo(name = "description")
    private String mDescription;
    @ColumnInfo(name = "date")
    private String mDate;
    @ColumnInfo(name = "imageUrl")
    private String mImageUrl;

    public Book () {
        this.mIsbn13 = "";
        this.mIsbn10 = "";
        this.mTitle = "";
        this.mAuthor = "";
        this.mDescription = "";
        this.mDate = "";
        this.mImageUrl = "";
    }

    @NonNull
    public String getIsbn13() {
        return mIsbn13;
    }

    public void setIsbn13(@NonNull String mIsbn13) {
        this.mIsbn13 = mIsbn13;
    }

    public String getIsbn10() {
        return mIsbn10;
    }

    public void setIsbn10(String mIsbn10) {
        this.mIsbn10 = mIsbn10;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
