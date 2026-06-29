package by.artdementiev.tvbrowser;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookmarkStore store;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        store = new BookmarkStore(this);

        container = findViewById(R.id.container);

        Button addBtn = findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, EditBookmarkActivity.class);
            i.putExtra("index", -1);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        container.removeAllViews();
        List<Bookmark> all = store.load();
        List<String> cats = store.categories();

        if (all.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Закладок пока нет. Нажмите «Добавить».");
            empty.setTextSize(20);
            empty.setPadding(24, 24, 24, 24);
            container.addView(empty);
            return;
        }

        for (String cat : cats) {
            TextView header = new TextView(this);
            header.setText(cat);
            header.setTextSize(22);
            header.setPadding(24, 28, 24, 12);
            header.setTextColor(0xFFEEEEEE);
            container.addView(header);

            List<Bookmark> inCat = new ArrayList<>();
            final List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < all.size(); i++) {
                Bookmark b = all.get(i);
                String c = (b.category == null || b.category.trim().isEmpty())
                        ? "Без категории" : b.category;
                if (c.equals(cat)) {
                    inCat.add(b);
                    indices.add(i);
                }
            }

            RecyclerView rv = new RecyclerView(this);
            rv.setLayoutManager(new GridLayoutManager(this, 4));
            rv.setAdapter(new TileAdapter(inCat, indices));
            rv.setPadding(16, 0, 16, 0);
            container.addView(rv);
        }
    }

    private void openUrl(String url) {
        Intent i = new Intent(this, BrowserActivity.class);
        i.putExtra("url", url);
        startActivity(i);
    }

    private void showLongPress(int globalIndex, Bookmark b) {
        new AlertDialog.Builder(this)
                .setTitle(b.title)
                .setItems(new CharSequence[]{"Открыть", "Изменить", "Удалить"},
                        (d, which) -> {
                            if (which == 0) {
                                openUrl(b.url);
                            } else if (which == 1) {
                                Intent i = new Intent(this, EditBookmarkActivity.class);
                                i.putExtra("index", globalIndex);
                                startActivity(i);
                            } else {
                                store.remove(globalIndex);
                                render();
                            }
                        })
                .show();
    }

    class TileAdapter extends RecyclerView.Adapter<TileAdapter.VH> {
        private final List<Bookmark> data;
        private final List<Integer> indices;

        TileAdapter(List<Bookmark> data, List<Integer> indices) {
            this.data = data;
            this.indices = indices;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_tile, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            Bookmark b = data.get(pos);
            int globalIndex = indices.get(pos);
            h.title.setText(b.title);
            h.itemView.setFocusable(true);
            h.itemView.setOnClickListener(v -> openUrl(b.url));
            h.itemView.setOnLongClickListener(v -> {
                showLongPress(globalIndex, b);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView title;
            VH(View v) {
                super(v);
                title = v.findViewById(R.id.tileTitle);
            }
        }
    }
}
