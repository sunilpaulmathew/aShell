package sunilpaulmathew.ashell;

import sunilpaulmathew.ashell.IShellCallback;

interface IShellService {
    String runShellCommand(String command) = 0;
        void runCommand(String command, IShellCallback callback) = 1;
        void destroyProcess() = 2;
}