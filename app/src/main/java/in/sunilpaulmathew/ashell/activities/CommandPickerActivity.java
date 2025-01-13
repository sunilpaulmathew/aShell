package in.sunilpaulmathew.ashell.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 08, 2025
 */
public class CommandPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Objects.equals(getIntent().getAction(), Intent.ACTION_SEND)) {
            String mString = null;
            Parcelable parcelable = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            if (parcelable != null) {
                if (!(parcelable instanceof Uri)) {
                    parcelable = Uri.parse(parcelable.toString());
                }
                Uri uri = (Uri) parcelable;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    for (int result = bis.read(); result != -1; result = bis.read()) {
                        buf.write((byte) result);
                    }
                    mString = buf.toString("UTF-8");
                } catch (IOException ignored) {}
            } else if (getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {
                mString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            }

            if (mString != null) {
                Intent startActivity = new Intent(this, StartActivity.class);
                startActivity.putExtra("command", mString);
                startActivity(startActivity);
            } else {
                Utils.toast(getString(R.string.file_manager_error_toast), this).show();
            }
            finish();
        }
    }

}