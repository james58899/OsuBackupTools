package tw.oktw.OsuBackupTools.Runnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;

import static tw.oktw.OsuBackupTools.Main.getMain;

public class ListFiles implements Runnable {
    private final File osuDir;
    private final ArrayDeque<Path> files;

    public ListFiles(File osuDir, ArrayDeque<Path> files) {
        this.osuDir = osuDir;
        this.files = files;
    }

    @Override
    public void run() {
        try {
            Files.newDirectoryStream(Paths.get(osuDir.toString(), "Songs")).forEach(path -> {
                try {
                    files.offerLast(Files.newDirectoryStream(path, "*.osu").iterator().next());
                } catch (IOException e) {
                    e.printStackTrace();
                    getMain().onException(e, false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            getMain().onException(e, true);
        }
    }
}
