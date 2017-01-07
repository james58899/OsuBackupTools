package tw.oktw.OsuBackupTools.Runnable;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentSkipListSet;

class OnlineGetID implements Runnable {

    private final Path file;
    private final ConcurrentSkipListSet<Integer> idList;

    OnlineGetID(Path file, ConcurrentSkipListSet<Integer> idList) {
        this.file = file;
        this.idList = idList;
    }

    @Override
    public void run() {
        try {
            String MD5 = getMD5(file);
            String url = "https://osu.ppy.sh/api/get_beatmaps?k=f4479181523b2c437d09775b546fdab8cae58c4e&h=" + MD5;
            String ID;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()))) {
                ID = bufferedReader.readLine().split(",")[0];
            }
            ID = ID.replaceAll("\\D", "");
            if (ID != null) {
                idList.add(Integer.parseInt(ID));
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String getMD5(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(Files.readAllBytes(file));
        byte[] digest = messageDigest.digest();
        return DatatypeConverter.printHexBinary(digest);
    }
}
