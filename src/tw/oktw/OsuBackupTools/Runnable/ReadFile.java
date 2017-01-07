package tw.oktw.OsuBackupTools.Runnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadFile implements Runnable {
    private int deadLine;
    private final ArrayDeque<Path> files;
    private final ConcurrentSkipListSet<Integer> idList;
    private final File saveFile;
    private boolean isContinue = true;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public ReadFile(ArrayDeque<Path> files, ConcurrentSkipListSet<Integer> idList, File saveFile) {
        this.files = files;
        this.idList = idList;
        this.saveFile = saveFile;
    }

    @Override
    public void run() {
        while (isContinue) {
            boolean match = false;
            String s;
            Path file = files.pollFirst();
            if (file != null) {
                deadLine = 0;
                try (BufferedReader bufferedReader = Files.newBufferedReader(file)) {
                    while ((s = bufferedReader.readLine()) != null) {
                        if (s.matches("^BeatmapSetID:\\d+")) {
                            s = s.replaceAll("BeatmapSetID:", "");
                            if (Integer.parseInt(s) == 0) {
                                break;
                            }
                            idList.add(Integer.parseInt(s));
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        OnlineGetID onlineGetID = new OnlineGetID(file, idList);
                        threadPool.execute(onlineGetID);
                        //Thread onlineGetIDThread = new Thread(onlineGetID);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                deadLine++;
                if (deadLine >= 3) {
                    threadPool.shutdown();
                    if (threadPool.isTerminated()) {
                        SaveListFile saveListFile = new SaveListFile(idList, saveFile);
                        Thread saveListFileThread = new Thread(saveListFile);
                        saveListFileThread.start();
                        isContinue = false;
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
