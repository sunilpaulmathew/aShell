package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public abstract class SortOptionsDialog extends BottomSheetDialog {

    public SortOptionsDialog(Context context) {
        super(context);

        View view = View.inflate(context, R.layout.layout_sort_options, null);

        ChipGroup chipGroupShow = view.findViewById(R.id.showApps);
        SwitchMaterial switchReverse = view.findViewById(R.id.reverseOrder);

        int currentShow = Utils.getInt("appType", 1, context);
        if (currentShow == 0) chipGroupShow.check(R.id.all);
        else if (currentShow == 1) chipGroupShow.check(R.id.system);
        else if (currentShow == 2) chipGroupShow.check(R.id.user);
        else if (currentShow == 3) chipGroupShow.check(R.id.disabled);

        switchReverse.setChecked(Utils.getBoolean("reverse_order", false, context));

        chipGroupShow.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);

                int targetShow = 0;
                if (checkedId == R.id.system) targetShow = 1;
                else if (checkedId == R.id.user) targetShow = 2;
                else if (checkedId == R.id.disabled) targetShow = 3;

                Utils.saveInt("appType", targetShow, context);

                onItemClicked();
            }
        });

        switchReverse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Utils.saveBoolean("reverse_order", isChecked, context);
            onItemClicked();
        });

        setContentView(view);
        show();
    }

    public abstract void onItemClicked();
}