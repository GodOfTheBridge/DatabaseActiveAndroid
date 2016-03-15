package com.gotb.databaseactiveandroid;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Contacts", id = "_id")
public class Database extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "telephone")
    public String telephone;

    public Database() {
        super();
    }

    public Database(String name, String telephone) {
        super();
        this.name = name;
        this.telephone = telephone;
    }

    public static List<Database> getAll() {
        return new Select()
                .from(Database.class)
                .execute();
    }

    public static Database getContactById(String id) {
        return new Select()
                .from(Database.class)
                .where("_id = ?", id)
                .executeSingle();
    }

    public static Cursor fetchResultCursor(String orderByColumn) {
        String tableName = Cache.getTableInfo(Database.class).getTableName();
        String resultRecords = new Select(tableName + ".*").from(Database.class).orderBy(orderByColumn).toSql();
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }

    public static Cursor fetchResultCursor() {
        String tableName = Cache.getTableInfo(Database.class).getTableName();
        String resultRecords = new Select(tableName + ".*").from(Database.class).toSql();
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }

    @Override
    public String toString() {
        return "ID = " + getId().toString() + '\n'
                + "Name = " + name + '\n'
                + "Telephone = " + telephone;
    }

}
