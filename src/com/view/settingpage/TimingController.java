package com.view.settingpage;

import com.jfoenix.controls.*;
import com.view.RootLayoutController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimingController {
    @FXML
    private AnchorPane timinguppane;
    @FXML
    private JFXDatePicker choosedata;

    @FXML
    private JFXButton commit;

    @FXML
    private StackPane stackpane;

    @FXML
    private JFXTimePicker chosetime;
    private RootLayoutController rootLayoutController;
    //the time that appliction should close auto
    private String closeTime;
    public Timeline timer;
    public boolean waitForClose=false;
    String WaringTitle="不可以哦";
    String WaringMessage="你设置的时间早就过了";
    String WaringButton="我知道了";


    public void setRootLayoutController(RootLayoutController rootLayoutController){
        this.rootLayoutController=rootLayoutController;
    }
    public JFXButton getCommit() {
        return commit;
    }
    public AnchorPane getTiminguppane() {
        return timinguppane;
    }
    /* *  this is used for set warning text for different language.
     * @author PennaLia
     * @date 2018/6/12 10:25
     * @param
     * @return
     */
    public void setWaringText(String language){
        if (language.equals("cn")){
            WaringTitle="不可以哦";
            WaringMessage="你设置的时间早就过了";
            WaringButton="我知道了";
        }else if(language.equals("en")){
            WaringTitle="Waring";
            WaringMessage="You set the past time";
            WaringButton="OK, I konw";
        }else if(language.equals("fr")){
            WaringTitle="Tu ne peux pas faire ça";
            WaringMessage="Le temps que vous avez passé a longtemps passé";
            WaringButton="Bon";
        }
    }

    /* *  this method give a timeline to handle the timing close.
     * @author PennaLia
     * @date 2018/6/12 10:25
     * @param
     * @return
     */
    @FXML
    public void handleTimingClose(){
        if(choosedata.getValue()==null||chosetime.getValue()==null){
            return;
        }
        closeTime=choosedata.getValue().toString().substring(0,5)+choosedata.getValue().toString().substring(6,10)+" "+chosetime.getValue();

        int res =closeTime.compareTo(new Date().toLocaleString().toString().substring(0, 15));
        if(res<0){
            stackpane.setVisible(true);
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Text(WaringTitle));
            content.setBody(new Text(WaringMessage));
            JFXDialog dialog = new JFXDialog(stackpane, content, JFXDialog.DialogTransition.CENTER);
            JFXButton button = new JFXButton(WaringButton);
            button.setOnAction(event -> {
                stackpane.setVisible(false);
                dialog.close();
            });
            content.setActions(button);
            dialog.show();
            return;
        }
        //after click the button, it should be gray
        commit.setStyle("-fx-background-color: #A9A9A9;" +
                "    -fx-font-color: #000000;");

        timer = new Timeline(new KeyFrame(Duration.seconds(10),ev->{
            if(waitForClose){
                rootLayoutController.getMainApp().closeWindows();
            }else {
                String now = new Date().toLocaleString().toString().substring(0, 15);
                if (now.equals(closeTime)) {
                    waitForClose=true;//next time it will close auto
                    rootLayoutController.timerOutClose();
                }
            }
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }



    public void loadLanguage(String language) {

    }
}
