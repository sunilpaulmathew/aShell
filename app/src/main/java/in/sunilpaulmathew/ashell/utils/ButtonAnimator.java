package in.sunilpaulmathew.ashell.utils;

import android.content.Context;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public abstract class ButtonAnimator {

    public ButtonAnimator(MaterialButton button) {
        appearText(button, null, 500);
    }

    public ButtonAnimator(MaterialButton button, String text) {
        appearText(button, text, 500);
    }

    public ButtonAnimator(MaterialButton button, String text, long durationMs) {
        appearText(button, text, durationMs);
    }

    public ButtonAnimator(MaterialButton button, int textID, long durationMs, Context context) {
        appearText(button, context.getString(textID), durationMs);
    }

    public ButtonAnimator(MaterialButton button, int textID, Context context) {
        appearText(button, context.getString(textID), 500);
    }

    private void appearText(MaterialButton button, String text, long durationMs) {
        TransitionManager.beginDelayedTransition((ViewGroup) button.getParent());

        String oldText = button.getText().toString().trim();
        float oldTextSizePx = button.getTextSize();
        int oldIconSizePx = button.getIconSize();
        float targetTextSizePx = oldTextSizePx * 1.15f;
        int targetIconSizePx = (int) (oldIconSizePx * 1.15f);

        if (!oldText.isEmpty() && text == null) {
            button.setText(null);
        } else {
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSizePx);
            button.setText(text);
        }
        button.setIconSize(targetIconSizePx);

        onItemClicked();
        
        button.postDelayed(() -> {
            TransitionManager.beginDelayedTransition((ViewGroup) button.getParent());
            button.setIconSize(oldIconSizePx);
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, oldTextSizePx);
            button.setText(oldText.isEmpty() ? null : oldText);
        }, durationMs);
    }

    public abstract void onItemClicked();

}