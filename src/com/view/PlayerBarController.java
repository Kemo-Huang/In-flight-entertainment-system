package com.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSlider;
import com.util.DataUpdater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Controller of the media player bar
 * It is connected to the player page
 * 
 */
public class PlayerBarController {
    @FXML
    private JFXSlider TimeBar;

    @FXML
    private JFXSlider volume;

    @FXML
    private ImageView PlayOrStop;
    
    @FXML
    private Label playTime;
    
    private PlayerPageController playerPageController;
    private Image stop;
    private Image play;
    public String localMovieURL = null;
    
    /* *  
     * Connect to the controller of upper level
     * @author PennaLia
     * @date 2018/6/2 19:03
     * @param
     * @return
     */
    public void setPlayPageController(PlayerPageController Controller){
        this.playerPageController=Controller;
    }
    
    /* *
     * Set the images 
     * @author PennaLia
     * @date 2018/6/2 19:03
     * @param
     * @return
     */
    @FXML
    private void initialize(){
        stop=new Image("resources/icon/vediostop.png");
        play=new Image("resources/icon/vedioplay.png");
        PlayOrStop.setImage(play);
    }

    /* *  
     * @author PennaLia
     * @date 2018/6/2 19:03
     * @param
     * @return
     */
    @FXML
    private void handlePlayOrStop(){
        MediaPlayer.Status status = mp.getStatus();
        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return;
        }
        if (status == MediaPlayer.Status.PAUSED
                || status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.STOPPED) {
            mp.play();
        } else {
            mp.pause();
        }
    }
    private MediaPlayer mp;
    private boolean stopRequested = false;
    private Duration duration;
    FileChooser fc;

    /**
     * This method link the media player with the control bar
     * <h>
     *     MediaPlayer Controlling
     * </h>
     * <p>
     *     Add listener to the media to make it play repeatedly.
     *     Show the media current play time with the total time.
     *     Change the media progress and volume as soon as dragging the sliders
     * </p>
     *
     * @param player the media player which will be controlled
     */
    public void controlPlayer(MediaPlayer player) {
        this.mp = player;

        player.currentTimeProperty().addListener(ov -> updateValues());

        player.setOnPlaying(() -> {
            if (stopRequested) {
                player.pause();
                stopRequested = false;
            } else {
                PlayOrStop.setImage(stop);
            }
        });

        player.setOnPaused(() -> PlayOrStop.setImage(play));

        player.setOnReady(() -> {
            duration = player.getMedia().getDuration();
            updateValues();
            playerPageController.getSpinner().setVisible(false);
            if (playerPageController.advertisementPlayer.getStatus()==MediaPlayer.Status.STOPPED){
                playerPageController.getBar().setDisable(false);
                playerPageController.mediaView.setMediaPlayer(playerPageController.mediaPlayer);
                playerPageController.mediaPlayer.setAutoPlay(true);
            }
        });
        
        // make the video able to play many times
        player.setCycleCount(MediaPlayer.INDEFINITE);
        
        player.setOnEndOfMedia(() -> {
            mp.seek(mp.getStartTime());
            player.pause();
        });
        
        TimeBar.valueProperty().addListener(ov -> {
            if (TimeBar.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mp.seek(duration.multiply(TimeBar.getValue() / 100.0));
            }
        });

        volume.valueProperty().addListener(ov -> {
            if (volume.isValueChanging()) {
                mp.setVolume(volume.getValue() / 100.0);
            }
        });
    }
    private void updateValues() {
        if (playTime != null && TimeBar != null && volume != null) {
            Platform.runLater(() -> {
                Duration currentTime = mp.getCurrentTime();
                playTime.setText(formatTime(currentTime, duration));
                TimeBar.setDisable(duration.isUnknown());
                if (!TimeBar.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !TimeBar.isValueChanging()) {
                    TimeBar.setValue(currentTime.divide(duration).toMillis()
                            * 100.0);
                }
                if (!volume.isValueChanging()) {
                    volume.setValue((int) Math.round(mp.getVolume()
                            * 100));
                }
            });
        }
    }

    /**
     * @author 黄珂邈
     * 
     * This method changes the time into 0:00:00 / 0:00:00 format
     * 
     * @param elapsed the current time of the media with duration format
     * @param duration the total time of the media with duration format
     * @return
     */
    static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        int temp = intElapsed;
        if (elapsedHours > 0) {
            temp -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = temp / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            temp = intDuration;
            if (durationHours > 0) {
                temp -= durationHours * 60 * 60;
            }
            int durationMinutes = temp / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    
    
    private boolean hasChooser = false;
    /**
     * This method is to make user able to choose local movie trailers
     * to display instead of seeing movies from sites
     * We control the file chooser to be single.
     *
     * @throws MalformedURLException
     */
    @FXML
    public void handle_local_movies() throws MalformedURLException {
        if(!hasChooser) {
            fc = new FileChooser();
            hasChooser = true;
            configureFileChooser(fc);
            File seletedFile = fc.showOpenDialog(null);
            if (seletedFile != null) {
                hasChooser = false;
                localMovieURL = seletedFile.toURI().toURL().toExternalForm();
                playerPageController.getSpinner().setVisible(false);
                playerPageController.mediaPlayer = new MediaPlayer(new Media(localMovieURL));
                playerPageController.mediaView.setMediaPlayer(playerPageController.mediaPlayer);
                playerPageController.setMoviePane();
                playerPageController.initPlayerBar();
                playerPageController.mediaPlayer.setAutoPlay(true);
                playerPageController.getSkip().setVisible(false);
                DataUpdater.insertLocalMovie(localMovieURL);
            } else {
                hasChooser = false;
                playerPageController.getWarningPane().setVisible(true);
                JFXDialogLayout content = new JFXDialogLayout();
                content.setHeading(new Text("Wrong"));
                content.setBody(new Text("You did not choose any file"));
                JFXDialog dialog = new JFXDialog(playerPageController.getWarningPane(), content, JFXDialog.DialogTransition.CENTER);
                JFXButton button = new JFXButton("I know");
                button.setOnAction(event -> {
                    playerPageController.getWarningPane().setVisible(false);
                    dialog.close();
                });
                content.setActions(button);
                dialog.show();
                localMovieURL = null;
            }
        }
    }

    /**
     * initially show the movie trailers
     * only .mp4 file
     * @param fileChooser
     */
    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Videos");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"/src/resources/sakai/"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4", "*.mp4"));
    }
}
