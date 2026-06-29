package by.artdementiev.tvbrowser;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class BookmarkStore {

    private static final String PREFS = "bookmarks_prefs";
    private static final String KEY = "bookmarks_json";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public BookmarkStore(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<Bookmark> load() {
        String json = prefs.getString(KEY, null);
        if (json == null) {
            return defaults();
        }
        List<Bookmark> list = gson.fromJson(
                json, new TypeToken<List<Bookmark>>() {}.getType());
        return list != null ? list : new ArrayList<Bookmark>();
    }

    public void save(List<Bookmark> list) {
        prefs.edit().putString(KEY, gson.toJson(list)).apply();
    }

    public void add(Bookmark b) {
        List<Bookmark> list = load();
        list.add(b);
        save(list);
    }

    public void update(int index, Bookmark b) {
        List<Bookmark> list = load();
        if (index >= 0 && index < list.size()) {
            list.set(index, b);
            save(list);
        }
    }

    public void remove(int index) {
        List<Bookmark> list = load();
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            save(list);
        }
    }

    public List<String> categories() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Bookmark b : load()) {
            String c = (b.category == null || b.category.trim().isEmpty())
                    ? "Без категории" : b.category;
            set.add(c);
        }
        return new ArrayList<>(set);
    }

    private List<Bookmark> defaults() {
        List<Bookmark> list = new ArrayList<>();
        list.add(new Bookmark("YouTube", "https://m.youtube.com", "Видео"));
        list.add(new Bookmark("Google", "https://www.google.com", "Поиск"));
        list.add(new Bookmark("Кинопоиск", "https://www.kinopoisk.ru", "Видео"));
        return list;
    }
}
