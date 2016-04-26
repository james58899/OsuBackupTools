package tw.oktw.OsuBackupTools;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Main extends Application {
    private File osuDir;
    private File saveFile;
    private static Main main;
    private static Button startBackupButton;
    private BackupHandler backupHandler;

    @Override
    public void start(Stage primaryStage) {
        main = this;
        //設定視窗標題
        primaryStage.setTitle("Beatmap 備份工具");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 15, 20, 15));

        //建立scene並指定
        Scene scene = new Scene(grid, 500, 250);
        primaryStage.setScene(scene);

        //設定內容
        Text Title = new Text("osu! Beatmap 備份工具\n");
        Title.setFont(Font.font("Sean", FontWeight.NORMAL, 25));
        grid.add(Title, 1, 0);

        Label osuPathLabel = new Label("osu安裝路徑：");
        grid.add(osuPathLabel, 0, 1);

        TextField osuPathDisplay = new TextField();
        osuPathDisplay.setEditable(false);
        grid.add(osuPathDisplay, 1, 1, 3, 1);

        Button osuPathSelect = new Button("瀏覽");
        HBox osuPathSelectHBox = new HBox(osuPathSelect);
        osuPathSelectHBox.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(osuPathSelectHBox, 3, 1);

        Label savePathLabel = new Label("備份儲存路徑：");
        grid.add(savePathLabel, 0, 2);

        TextField savePathDisplay = new TextField();
        savePathDisplay.setEditable(false);
        grid.add(savePathDisplay, 1, 2, 3, 1);

        Button savePathSelect = new Button("瀏覽");
        HBox savePathSelectHBox = new HBox(savePathSelect);
        savePathSelectHBox.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(savePathSelectHBox, 3, 2);

        startBackupButton = new Button("開始備份");
        startBackupButton.setDefaultButton(true);
        grid.add(startBackupButton, 3, 5);

        //設定按鈕事件
        osuPathSelect.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("選擇osu安裝路徑");
            osuDir = directoryChooser.showDialog(primaryStage);
            osuPathDisplay.setText(osuDir.toString());
        });

        savePathSelect.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("選擇備份儲存路徑");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("純文字文件", "*.txt"));
            fileChooser.setInitialFileName("OsuBackup.txt");
            saveFile = fileChooser.showSaveDialog(primaryStage);
            savePathDisplay.setText(saveFile.toString());
        });

        startBackupButton.setOnAction(event -> {
            if (osuDir != null & saveFile != null) {
                startBackupButton.setText("備份中...");
                startBackupButton.setDisable(true);
                backupHandler = new BackupHandler(osuDir, saveFile);
                backupHandler.start();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("錯誤");
                alert.setHeaderText("OSU安裝路徑或備份儲存路徑無效！");
                alert.setContentText("請選擇有效路徑");
                alert.showAndWait();
            }
        });

        //顯示視窗
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Main getMain() {
        return main;
    }

    public void onSuccessful() {
        Platform.runLater(() -> {
            startBackupButton.setText("開始備份");
            startBackupButton.setDisable(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("成功");
            alert.setHeaderText("已備份完成！");
            alert.setContentText("備份檔案已儲存至" + saveFile.toString());
            alert.showAndWait();
        });
    }

    public void onException(Exception e, Boolean stop) {
        Platform.runLater(() -> {
            if (stop) backupHandler.stop();
            StringWriter ex = new StringWriter();
            e.printStackTrace(new PrintWriter(ex));
            startBackupButton.setText("開始備份");
            startBackupButton.setDisable(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(true);
            alert.setTitle("錯誤");
            alert.setHeaderText("發生錯誤！");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        });
    }
}
