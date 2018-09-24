/**
 * Created by conor on 22/02/2018.
 */

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class MainGUIController {

    //Array List for counting sheep
    private ArrayList<Integer> sheepCounter = new ArrayList<>();

    //Array List for drawing sheep boundaries
    private ArrayList<Integer> squaresDrawn = new ArrayList<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public ImageView imageView;

    @FXML
    public String imagePath;

    @FXML
    private ImageView blackWhiteImageView;

    @FXML
    private Label sheepNumberLabel;

    //Fields
    private int[] imagePixel;
    private BufferedImage image;
    private BufferedImage blackWhiteImage;
    int iAlpha;
    int iRed;
    int iGreen;
    int iBlue;
    int rootNode;

    //Iterative version of find
    private int find(int[] a, int id) {
        while(a[id]!=id) id=a[id];
        return id;
    }

    //Quick union of disjoint sets containing elements p and q (Version 1)
    private void union(int[] a, int p, int q) {
        a[find(a, q)] = p; //The root of q is made reference p
    }

    //Opens selected image and opens black and white version of the image
    @FXML
    void showFileChooser(ActionEvent event) throws IOException {
        //Open image file
        FileChooser fileChooser = new FileChooser();
        //Limit chooser options to image files
        fileChooser.getExtensionFilters().addAll
                (new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg", "*.gif"));
        fileChooser.setInitialDirectory(new File("C:\\Users\\conor\\Pictures\\Saved Pictures"));
        fileChooser.setTitle("Open File Dialog");
        Stage stage = (Stage) anchorPane.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        imagePath = file.getPath();

        //Open black and white version of image
        if (file != null) {
            image = ImageIO.read(file);
            imageView.setImage(SwingFXUtils.toFXImage(image, null));
            imageView.setPreserveRatio(true);
            blackWhiteImage = ImageIO.read(file);
            try {
                for (int w = 0; w < blackWhiteImage.getWidth(); w++) {
                    for (int h = 0; h < blackWhiteImage.getHeight(); h++) {
                        int rgb = blackWhiteImage.getRGB(w, h);
                        iAlpha = rgb >> 24 & 0xff;
                        iRed = rgb >> 16 & 0xff;
                        iGreen = rgb >> 8 & 0xff;
                        iBlue = rgb & 0xff;
                        if ((iGreen < 220) && (iRed < 220) && (iBlue < 220)) {
                            blackWhiteImage.setRGB(w, h, 0);
                        } else {
                            blackWhiteImage.setRGB(w, h, 0xffffff);
                        }
                    }
                }
                blackWhiteImageView.setImage(SwingFXUtils.toFXImage(blackWhiteImage, null));
            } catch (Exception e) {
                System.out.println(e);
            }

            //Creates one dimensional image array long enough to store all pixels
            imagePixel = new int[blackWhiteImage.getHeight() * blackWhiteImage.getWidth()];
//            System.out.println(imagePixel.length);

            for (int h = 0; h < blackWhiteImage.getHeight(); h++) {
                for (int w = 0; w < blackWhiteImage.getWidth(); w++) {
                    int argb = SwingFXUtils.toFXImage(blackWhiteImage, null).getPixelReader().getArgb(w, h);
                    imagePixel[w + h * blackWhiteImage.getWidth()] = argb != -16777216 ? w + h * blackWhiteImage.getWidth() : 0;
                }
            }

            for (int i = 0; i < imagePixel.length; i++) {
                if (imagePixel[i] > 0) {
                    imagePixel[i] = i;
                }
            }
        }
    }

//    @FXML
//    void countWhitePixels(ActionEvent event) throws Exception {
//        int whitePixels = 0;
//        for (int w = 0; w < image.getWidth(); w++) {
//            for (int h = 0; h < image.getHeight(); h++) {
//                int color = image.getRGB(w, h);
//                iAlpha = color >> 24 & 0xff;
//                iRed = color >> 16 & 0xff;
//                iGreen = color >> 8 & 0xff;
//                iBlue = color & 0xff;
//                if ((iGreen > 200) && (iRed > 200) && (iBlue > 200)) {
//                    whitePixels++;
//                }
//            }
//        }
//        System.out.println(whitePixels);
//    }
//

    //Counts the sheep in the image using union-find implementation
    @FXML
    void countSheep() {
        for (int i = 0; i < imagePixel.length; i++) {
            //Union left
            if (imagePixel[i] > 0 && imagePixel[i + 1] > 0) {
                union(imagePixel, i, i + 1);
//                System.out.println("Union Left -- The value of " + i + " is " + find(imagePixel, i));
            }

            //Union down
            if (imagePixel[i] > 0 && imagePixel[i + (blackWhiteImage.getWidth())] > 0) {
                union(imagePixel, i, imagePixel[i + (blackWhiteImage.getWidth())]);
//                System.out.println("Union Down -- The value of " + i + " is " + find(imagePixel, i));
            }
        }

        //Building Array List for counting sheep
        for (int nodes : imagePixel) {
            rootNode = find(imagePixel, nodes);
            if (!sheepCounter.contains(rootNode)) {
                sheepCounter.add(rootNode);
            }
        }
        sheepNumberLabel.setText("Number of Sheep: " + (sheepCounter.size()-1));
        sheepCounter.clear();
    }

    //Used to identify sheep and draw red squares around the sheep (NOT FUNCTIONING)
    @FXML
    void identifySheep() {
        for (int i = 0; i < imagePixel.length; i++) {
            if (sheepCounter.contains(i ) && !squaresDrawn.contains(rootNode)) {
                int x = (i%image.getWidth())/image.getWidth();
                int y = (imagePixel[i]/image.getWidth());
                Rectangle rectangle = new Rectangle();
                rectangle.setBounds(450+x, 106+y, 5, 5);
                squaresDrawn.add(rootNode);
            }
        }
    }

    @FXML
    void exitApplication(ActionEvent event) throws Exception {
        System.exit(0);
    }

    @FXML
    void initialize() {
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'MainGUI.fxml'.";


    }

}
