package com.example.sherbakov17pr;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sherbakov17pr.DBHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "MyLogs";
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private EditText etText1, etText2, etNum1, etNum2;
    private TextView tvResult;
    private Button btnAdd, btnRead, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText1 = findViewById(R.id.etText1);
        etText2 = findViewById(R.id.etText2);
        etNum1 = findViewById(R.id.etNum1);
        etNum2 = findViewById(R.id.etNum2);
        tvResult = findViewById(R.id.tvResult);
        btnAdd = findViewById(R.id.btnAdd);
        btnRead = findViewById(R.id.btnRead);
        btnClear = findViewById(R.id.btnClear);

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View v) {
        db = dbHelper.getWritableDatabase();
        String t1 = etText1.getText().toString().trim();
        String t2 = etText2.getText().toString().trim();
        String n1Str = etNum1.getText().toString().trim();
        String n2Str = etNum2.getText().toString().trim();

        String tag = (String) v.getTag();

        switch (tag) {
            case "add":
                if (t1.isEmpty() || t2.isEmpty() || n1Str.isEmpty() || n2Str.isEmpty()) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues cv = new ContentValues();
                cv.put("text1", t1);
                cv.put("text2", t2);
                cv.put("num1", Integer.parseInt(n1Str));
                cv.put("num2", Integer.parseInt(n2Str));

                long rowId = db.insert(DBHelper.TABLE_NAME, null, cv);
                Log.d(LOG_TAG, "Запись вставлена, ID = " + rowId);
                Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show();
                clearInputs();
                break;


            case "read":
                Log.d(LOG_TAG, "--- Чтение всех записей ---");
                Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
                StringBuilder sb = new StringBuilder();
                if (c.moveToFirst()) {
                    int idCol = c.getColumnIndex("id");
                    int t1Col = c.getColumnIndex("text1");
                    int t2Col = c.getColumnIndex("text2");
                    int n1Col = c.getColumnIndex("num1");
                    int n2Col = c.getColumnIndex("num2");
                    do {
                        String line = "ID: " + c.getInt(idCol) +
                                " | Т1: " + c.getString(t1Col) +
                                " | Т2: " + c.getString(t2Col) +
                                " | Ч1: " + c.getInt(n1Col) +
                                " | Ч2: " + c.getInt(n2Col);
                        sb.append(line).append("\n");
                        Log.d(LOG_TAG, line);
                    } while (c.moveToNext());
                } else {
                    sb.append("Записей нет (0 rows)");
                    Log.d(LOG_TAG, "0 rows");
                }
                tvResult.setText(sb.toString());
                c.close();
                break;

            case "clear":
                Log.d(LOG_TAG, "--- Очистка таблицы ---");
                int count = db.delete(DBHelper.TABLE_NAME, null, null);
                db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + DBHelper.TABLE_NAME + "'");
                Log.d(LOG_TAG, "Удалено строк: " + count);
                tvResult.setText("Таблица очищена. Удалено: " + count);
                break;

             default:
                Toast.makeText(this, "баг игры", Toast.LENGTH_SHORT).show();
                break;
        }
        dbHelper.close();
    }

    private void clearInputs() {
        etText1.setText(""); etText2.setText("");
        etNum1.setText(""); etNum2.setText("");
    }
}