/*
The MIT License (MIT)
Copyright (c) 2016, Starschema Ltd

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.starschema.tabadmin_cli;

import static net.starschema.tabadmin_cli.FileHelper.filePregMatch;

class WorkerRepositoryServer extends WorkerAbstract {

    private static final String WINDOWS_PROCESS_NAME = "pg_ctl.exe";

    WorkerRepositoryServer() {
    }

    public int getProcessId() throws Exception {
        return -1;
    }

    public String getWindowsProcessName() { return WINDOWS_PROCESS_NAME; }

    public String toString() {
        return "repository";
    }

    static String getAppPath() throws Exception {

        if (!FileHelper.checkIfDir(CliControl.TABSVC_CONFIG_DIR)) {
            throw new Exception(CliControl.TABSVC_CONFIG_DIR +" is not a directory.");
        }

        return filePregMatch(CliControl.TABSVC_CONFIG_DIR + "//" + FileHelper.WORKGROUP_YAML_FILENAME, "^pgsql\\.pgctl: (.*)$");

    }

    static String getDataDir() throws Exception {

        if (!FileHelper.checkIfDir(CliControl.TABSVC_CONFIG_DIR)) {
            throw new Exception(CliControl.TABSVC_CONFIG_DIR +" is not a directory.");
        }

        return filePregMatch(CliControl.TABSVC_CONFIG_DIR + "//" +  FileHelper.WORKGROUP_YAML_FILENAME, "^pgsql\\.data\\.dir: (.*)$");

    }
}
