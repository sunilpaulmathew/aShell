package in.sunilpaulmathew.ashell.serializable;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ExtrasEntry implements Serializable {

    private final int description, icon, id, title;

    public ExtrasEntry(int icon, int title, int description, int id) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public int getDescription() {
        return this.description;
    }

    public int getIcon() {
        return this.icon;
    }

    public int getId() {
        return this.id;
    }

    public int getTitle() {
        return this.title;
    }

    public void load(MaterialCardView cardView, ImageButton icon, TextView title, TextView description) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Context context = cardView.getContext();
                handler.post(() -> {
                    icon.setImageResource(this.icon);
                    title.setText(this.title);
                    description.setText(this.description);
                    cardView.setCardBackgroundColor(Settings.getCardBackgroundColor(this.icon, context));
                    title.setTextColor(Settings.getIconColor(this.icon, context));
                    icon.setColorFilter(Settings.getIconColor(this.icon, context));
                });
            });
        }
    }

}