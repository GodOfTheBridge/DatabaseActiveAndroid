package com.gotb.databaseactiveandroid;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Update;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAdd, btnShow, btnDeleteTable, btnUpdate, btnFind, btnDeleteById, btnShowList;
    private EditText editName, editTelephone, editId;
    private RadioButton rbtnName, rbtnId, rbtnTelephone;
    private TableLayout tableLayout;
    private String name, telephone, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActiveAndroid.initialize(this);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);
        btnDeleteTable = (Button) findViewById(R.id.btnDeleteTable);
        btnDeleteTable.setOnClickListener(this);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(this);
        btnDeleteById = (Button) findViewById(R.id.btnDeleteById);
        btnDeleteById.setOnClickListener(this);
        btnShowList = (Button) findViewById(R.id.btnShowList);
        btnShowList.setOnClickListener(this);

        rbtnId = (RadioButton) findViewById(R.id.radioBtnById);
        rbtnId.setChecked(true);
        rbtnTelephone = (RadioButton) findViewById(R.id.radioBtnByTelephone);
        rbtnName = (RadioButton) findViewById(R.id.radioBtnByName);

        editName = (EditText) findViewById(R.id.editName);
        editTelephone = (EditText) findViewById(R.id.editTelephone);
        editId = (EditText) findViewById(R.id.editId);

        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

    }

    @Override
    public void onClick(View view) {
        name = editName.getText().toString();
        telephone = editTelephone.getText().toString();
        id = editId.getText().toString();

        switch (view.getId()) {
            case R.id.btnAdd:
                if (checkFieldsIsEmpty()) {
                    Database tableContacts = new Database(name, telephone);
                    tableContacts.save();
                    editName.setText("");
                    editTelephone.setText("");
                    tableLayout.removeAllViews();
                    buildTable();
                }
                editName.requestFocus();
                break;

            case R.id.btnShow:
                tableLayout.removeAllViews();
                buildTable();
                break;

            case R.id.btnDeleteTable:
                new Delete().from(Database.class).execute();
                tableLayout.removeAllViews();
                break;

            case R.id.btnUpdate:
                if (checkFieldsIsEmpty()) {
                    new Update(Database.class)
                            .set("name = ?, telephone = ?", name, telephone)
                            .where("_id = ?", id)
                            .execute();
                    editName.setText("");
                    editTelephone.setText("");
                    editId.setText("");
                    tableLayout.removeAllViews();
                    buildTable();
                }
                editName.requestFocus();
                break;

            case R.id.btnFind:
                Database contactById = Database.getContactById(id);
                if (contactById != null) {
                    editName.setText(contactById.name);
                    editTelephone.setText(contactById.telephone);
                    editName.requestFocus();
                } else Toast.makeText(this, "Id not found", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnDeleteById:
                new Delete().from(Database.class).where("_id = ?", id).execute();
                tableLayout.removeAllViews();
                buildTable();
                break;

            case R.id.btnShowList:
                Intent intent = new Intent(this, ListViewDatabase.class);
                startActivity(intent);
                break;
        }
        tableLayout.removeAllViews();
        buildTable();
    }

    protected Boolean checkFieldsIsEmpty() {
        if (editName.getText().toString().equals("") || editTelephone.getText().toString().equals("")) {
            Toast.makeText(this, "Please, input data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void buildTable() {
        String sortByColumn = "_id";
        if (rbtnId.isChecked()) {
            sortByColumn = "_id";
        } else if (rbtnName.isChecked()) {
            sortByColumn = "name";
        } else if (rbtnTelephone.isChecked()) {
            sortByColumn = "telephone";
        }

        Cursor cursor = Database.fetchResultCursor(sortByColumn);
        int rows = cursor.getCount();
        int columns = cursor.getColumnCount();
        if (cursor != null) {
            cursor.moveToFirst();
        }

        makeHeadersForTable();

        for (int i = 0; i < rows; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < columns; j++) {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                textView.setBackgroundResource(R.drawable.cell_shape);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(18);
                textView.setPadding(0, 5, 0, 5);
                textView.setText(cursor.getString(j));
                tableRow.addView(textView);
            }
            cursor.moveToNext();
            tableLayout.addView(tableRow);
        }
        cursor.close();
    }

    private void makeHeadersForTable() {
        String[] headers = {"ID", "Name", "Telephone"};
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        for (int j = 0; j < headers.length; j++) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            textView.setBackgroundResource(R.drawable.cell_shape);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18);
            textView.setPadding(0, 5, 0, 5);
            textView.setText(headers[j]);
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tableLayout.removeAllViews();
        buildTable();
    }
}
