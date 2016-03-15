package com.gotb.databaseactiveandroid;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.activeandroid.query.Delete;

import java.util.List;


public class ListViewDatabase extends AppCompatActivity implements View.OnCreateContextMenuListener {

    private ListView listViewDB;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Cursor cursor;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_database);

        listViewDB = (ListView) findViewById(R.id.lvDatabase);
        listViewDB.setOnCreateContextMenuListener(this);

        buildListDB();
    }

    public void simpleBuildListDB(){
        List<Database> allContacts = Database.getAll();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allContacts);
        listViewDB.setAdapter(listAdapter);
    }

    private void buildListDB() {
        cursor = Database.fetchResultCursor();
        String[] from = new String[] { "_id", "name", "telephone"};
        int[] to = new int[] {R.id.tvId, R.id.tvName, R.id.tvTelephone };

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.items, cursor, from, to, 0);
        listViewDB.setAdapter(simpleCursorAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String id = Integer.toString((int)adapterContextMenuInfo.id);

        switch (item.getItemId()){
            case R.id.itemDelete:
                new Delete().from(Database.class).where("_id = ?", id).execute();
                buildListDB();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
