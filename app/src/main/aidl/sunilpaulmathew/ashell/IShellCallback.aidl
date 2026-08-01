package sunilpaulmathew.ashell;

import java.util.List;

interface IShellCallback {
    void onLines(in List<String> lines);
    void onFinished(int exitCode);
}