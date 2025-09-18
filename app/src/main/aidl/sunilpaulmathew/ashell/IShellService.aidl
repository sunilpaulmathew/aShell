package sunilpaulmathew.ashell;

import sunilpaulmathew.ashell.IShellCallback;

interface IShellService {
    void runCommand(String command, IShellCallback callback);
    void destroyProcess();
}