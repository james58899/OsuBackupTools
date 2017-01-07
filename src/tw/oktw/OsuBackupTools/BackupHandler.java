package tw.oktw.OsuBackupTools;

import tw.oktw.OsuBackupTools.Runnable.ListFiles;
import tw.oktw.OsuBackupTools.Runnable.ReadFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentSkipListSet;

class BackupHandler {
    private File osuDir;
    private File saveFile;
    private ConcurrentSkipListSet<Integer> idList = new ConcurrentSkipListSet<>();
    private ArrayDeque<Path> files = new ArrayDeque<>();
    private ThreadGroup group = new ThreadGroup("Backup Thread");

    BackupHandler(File osuDir, File saveFile) {
        this.osuDir = osuDir;
        this.saveFile = saveFile;
    }

    void start() {
        ListFiles listFiles = new ListFiles(osuDir, files);
        ReadFile readFile = new ReadFile(files, idList, saveFile);
        Thread listFilesThread = new Thread(group, listFiles);
        Thread readFileThread = new Thread(group, readFile);
        readFileThread.setDaemon(true);
        listFilesThread.start();
        readFileThread.start();
    }
}
