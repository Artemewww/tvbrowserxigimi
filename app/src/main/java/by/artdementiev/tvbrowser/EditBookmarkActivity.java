package by.artdementiev.tvbrowser;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EditBookmarkActivity extends AppCompatActivity {

    private BookmarkStore store;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        store = new BookmarkStore(this);

        EditText title = findViewById(R.id.editTitle);
        EditText url = findViewById(R.id.editUrl);
        EditText category = findViewById(R.id.editCategory);
        Button save = findViewById(R.id.btnSave);

        index = getIntent().getIntExtra("index", -1);

        if (index >= 0) {
            List<Bookmark> list = store.load();
            if (index < list.size()) {
                Bookmark b = list.get(index);
                title.setText(b.title);
                url.setText(b.url);
                category.setText(b.category);
            }
        }

        save.setOnClickListener(v -> {
            String t = title.getText().toString().trim();
            String u = url.getText().toString().trim();
            String c = category.getText().toString().trim();

            if (t.isEmpty() || u.isEmpty()) {
                Toast.makeText(this, "Заполните название и ссылку",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (c.isEmpty()) c = "Без категории";

            Bookmark b = new Bookmark(t, u, c);
            if (index >= 0) {
                store.update(index, b);
            } else {
                store.add(b);
            }
            finish();
        });
    }
}
