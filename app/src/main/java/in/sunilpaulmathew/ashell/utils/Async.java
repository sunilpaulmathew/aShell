package in.sunilpaulmathew.ashell.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2025
 */
public abstract class Async {

    /*
     * Shared: every instance used to spin up an executor of its own and shut it down
     * again, which means a thread created and torn down per background task. Cached
     * rather than single threaded so tasks still run concurrently, as they did when
     * each had its own executor.
     */
    private static final ExecutorService executors = Executors.newCachedThreadPool();

    private void startBackground() {
        onPreExecute();
        executors.execute(() -> {
            doInBackground();
            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        });
    }

    public void execute() {
        startBackground();
    }

    public abstract void onPreExecute();

    public abstract void doInBackground();

    public abstract void onPostExecute();

}