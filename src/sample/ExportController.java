package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jxl.read.biff.BiffException;

import java.beans.Visibility;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExportController implements Initializable {

    private static final int TYPE_FILE_1 = 1;
    private static final int TYPE_FILE_2 = 2;

    private int fileType;
    private Stage stage;
    private Scene scene;
    private ArrayList<String[]> list;
    private String savePath;
    private Task copyWorker;
    private Thread thread;

    @FXML
    private Button btChooseFile;
    @FXML
    private Label lbFilePath;
    @FXML
    private Label lbFileName;
    @FXML
    private TextField tfColumn1;
    @FXML
    private TextField tfColumn2;
    @FXML
    private Button btExportFile;
    @FXML
    private ProgressBar pbDownload;
    @FXML
    private ToggleButton tbFileType1;
    @FXML
    private ToggleButton tbFileType2;
    @FXML
    private Button btPreview;
    @FXML
    private Label lbFinish;
    @FXML
    private Button btStop;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pbDownload.setProgress(0);

        lbFinish.setVisible(false);
        ToggleGroup group = new ToggleGroup();
        tbFileType1.setToggleGroup(group);
        tbFileType1.setUserData(TYPE_FILE_1);
        tbFileType1.setSelected(true);

        fileType = TYPE_FILE_1;

        tbFileType2.setToggleGroup(group);
        tbFileType2.setUserData(TYPE_FILE_2);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                fileType = (int) group.getSelectedToggle().getUserData();
            }
        });

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    // When user click on myButton
    // this method will be called.
    public void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("Excel files (*.txt)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter1);
        File file = fileChooser.showOpenDialog(stage);
        lbFilePath.setText(file.getAbsolutePath());
        System.out.println(file);
    }

    public void exportFile(ActionEvent event) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(stage);
            String filePath = lbFilePath.getText();
            ExcelHelper excel = new ExcelHelper(filePath);
            switch (fileType) {
                case TYPE_FILE_1:
                    list = excel.readExcelType1();
                    break;
                case TYPE_FILE_2:
                    list = excel.readExcelType2();
                default:
                    break;
            }

            savePath = file.getAbsolutePath();

            copyWorker = createWorker();
            pbDownload.progressProperty().unbind();
            pbDownload.progressProperty().bind(copyWorker.progressProperty());
            copyWorker.messageProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {
                    System.out.println(newValue);
                }
            });
            thread = new Thread(copyWorker);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public void stopExport(){
        thread.stop();
    }

    public void preview() {
        try {
            int column1 = Integer.parseInt(tfColumn1.getText()) - 1;
            int column2 = Integer.parseInt(tfColumn2.getText()) - 1;
            String filePath = lbFilePath.getText();
            ExcelHelper excel = new ExcelHelper(filePath);
            switch (fileType) {
                case TYPE_FILE_1:
                    list = excel.readExcelType1();
                    String[] row1 = list.get(2);
                    lbFileName.setText(row1[column1] + "+" + row1[column2]);
                    break;
                case TYPE_FILE_2:
                    list = excel.readExcelType2();
                    String[] row0 = list.get(0);
                    lbFileName.setText(row0[column1] + "+" + row0[column2]);
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                lbFinish.setVisible(false);
                int column1 = Integer.parseInt(tfColumn1.getText()) - 1;
                int column2 = Integer.parseInt(tfColumn2.getText()) - 1;
                for (int i = 0; i < list.size(); i++) {
                    int k = 1;
                    String[] str = list.get(i);
                    for (int j = 0; j < str.length; j++) {
                        System.out.print(str[j] + '\t');
                        if (str[j].startsWith("http")) {
                            DownloadHelper.download(str[j], str[column1] + str[column2] + k + ".jpg", savePath);
                            updateProgress(i+1, list.size());
                            k++;
                        }
                    }
                    System.out.println();
                }
                pbDownload.progressProperty().unbind();
                lbFinish.setVisible(true);
                return true;
            }
        };
    }

}
