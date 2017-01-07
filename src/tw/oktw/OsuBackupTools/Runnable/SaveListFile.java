package tw.oktw.OsuBackupTools.Runnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;

class SaveListFile implements Runnable {
    private final ConcurrentSkipListSet<Integer> idList;
    private final File saveFile;

    SaveListFile(ConcurrentSkipListSet<Integer> idList, File saveFile) {
        this.idList = idList;
        this.saveFile = saveFile;
    }

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(saveFile)) {
            int[] list = new int[idList.size()];
            final int[] i = {0};
            idList.forEach(integer -> {
                list[i[0]] = integer;
                i[0]++;
            });
            Arrays.sort(list);
            for (int i1 : list) {
                writer.println(i1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
