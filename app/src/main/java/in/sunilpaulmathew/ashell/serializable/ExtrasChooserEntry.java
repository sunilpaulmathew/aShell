package in.sunilpaulmathew.ashell.serializable;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class ExtrasChooserEntry implements Serializable {

    private boolean enabled;
    private final int description, icon, id, title;

    public ExtrasChooserEntry(ExtrasEntry extrasEntry, boolean enabled) {
        this.icon = extrasEntry.getIcon();
        this.title = extrasEntry.getTitle();
        this.description = extrasEntry.getDescription();
        this.id = extrasEntry.getId();
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getId() {
        return this.id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void load(CardView cardView, CheckBox checkBox, ImageButton icon, TextView title, TextView description) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Context context = cardView.getContext();
                handler.post(() -> {
                    icon.setImageResource(this.icon);
                    title.setText(this.title);
                    description.setText(this.description);
                    description.setVisibility(VISIBLE);
                    checkBox.setChecked(this.enabled);
                    cardView.setCardBackgroundColor(Settings.getCardBackgroundColor(this.icon, context));
                    title.setTextColor(Settings.getIconColor(this.icon, context));
                    icon.setColorFilter(Settings.getIconColor(this.icon, context));
                });
            });
        }
    }

}