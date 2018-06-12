package com.view.settingpage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.util.JsonLoader;
import com.view.RootLayoutController;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;


public class EditController {
    @FXML
    private JFXButton choosefile;
    @FXML
    private Label name;
    @FXML
    private JFXButton confirmButton;
    @FXML
    private Label modify;
    @FXML
    private Label ps;
    @FXML
    private JFXTextField textname;
    @FXML
    private AnchorPane edituppane;
    @FXML
    private JFXTextField textsign;
    @FXML
    private StackPane stackpane;

    String imageURL;

    public JFXTextField getTextname(){return  textname;}
    public JFXTextField getTextsign(){return  textsign;}
    public AnchorPane getEdituppane(){
        return edituppane;
    }
    private RootLayoutController rootLayoutController;

    String WaringTitle="警告";
    String WaringMessage="你没有选择任何文件";
    String WaringButton="我知道了";

    public void setRootLayoutController(RootLayoutController rootLayoutController){
        this.rootLayoutController=rootLayoutController;
    }

    /* *  this is used to set warning text for different language.
     * @author PennaLia
     * @date 2018/6/12 10:35
     * @param
     * @return
     */
    public void setWaringText(String language){
        if (language.equals("cn")){
            WaringTitle="警告";
            WaringMessage="你没有选择任何文件";
            WaringButton="我知道了";
        }else if(language.equals("en")){
            WaringTitle="Waring";
            WaringMessage="You did not choose any file";
            WaringButton="OK, I konw";
        }else if(language.equals("fr")){
            WaringTitle="Avertissement";
            WaringMessage="Vous n'avez sélectionné aucun fichier";
            WaringButton="Je sais";
        }
    }
    /* *  when you clicked that, you can change the information of user.
     * @author PennaLia
     * @date 2018/6/12 10:36
     * @param
     * @return
     */
    @FXML
    public void handleEdit(){
        rootLayoutController.getDrawerContentController().changNameAndSign(
                textname.getText(),textsign.getText()
        );
        if(imageURL!=null)
            rootLayoutController.getDrawerContentController().changeUserImage(imageURL);
    }
    /* *  choose file from your computer to replace your image.
     * @author PennaLia
     * @date 2018/6/12 10:37
     * @param
     * @return
     */
    @FXML
    public void handleChooseFile() throws MalformedURLException {
        FileChooser fc = new FileChooser();
        configureFileChooser(fc);
        File seletedFile =fc.showOpenDialog(null);
        if(seletedFile!=null){
            imageURL=seletedFile.toURI().toURL().toExternalForm();//加载出文件的路径
        }else {
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
            imageURL = null;
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"/src/resources/user_icon"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    public void loadLanguage(String language) {
        JSONObject jsonObject = JsonLoader.getJsonValue(language,"edit");
        try {
            assert jsonObject != null;
            name.setText(jsonObject.getString("name"));
            ps.setText(jsonObject.getString("ps"));
            confirmButton.setText(jsonObject.getString("confirm"));
            modify.setText(jsonObject.getString("modify1"));
        } catch (JSONException | NullPointerException e){
            e.printStackTrace();
        }
    }
}
