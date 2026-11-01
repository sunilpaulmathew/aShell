package in.sunilpaulmathew.ashell.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 31, 2026
 */
public class ShellViewModel extends ViewModel {
    public int mPosition = 1;
    public final List<String> mHistory = new ArrayList<>();
    public final List<String> mResult = Collections.synchronizedList(new ArrayList<>());

}