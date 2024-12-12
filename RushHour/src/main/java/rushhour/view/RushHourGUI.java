package rushhour.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import rushhour.model.*;

public class RushHourGUI extends Application implements RushHourObserver {
    private RushHour rushHour;
    private HashMap<Button, String> originalButtonStyles;
    private GridPane boardGrid;
    public Label statusLabel;
    private Label moveCounterLabel;
    private Integer moveCount = 0;
    private boolean gameOver = false;
    private Button highlightedVehicleButton = null;
    public HashSet vehicleB = new HashSet();
    private HashMap<Character, Color> vehicleColors;
    private List<Color> usedColors = new ArrayList<>();
    private final String filename = "data/03_00.csv";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        vehicleColors = new HashMap<>();
        rushHour = new RushHour(filename);
        statusLabel = new Label();
        originalButtonStyles = new HashMap<>();
        boardGrid = createBoardGrid();
        rushHour.registerObserver(this);

        initializeVehicles();

        Button hintButton = new Button("Hint");
        hintButton.setOnAction(event -> {
            try {
                handleHintButton();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });

        Button solveButton = new Button("Solve");
        solveButton.setOnMouseClicked(event -> {
            if (canMoveVehicle()) {
                solveGame();
            }
        });

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> handleResetButton(filename));

        moveCounterLabel = new Label("Moves: " + moveCount);

        VBox leftControlBox = new VBox(statusLabel); 
        leftControlBox.setSpacing(0);
        HBox middleControlBox = new HBox(moveCounterLabel);
        middleControlBox.setSpacing(0);
        HBox rightControlBox = new HBox(hintButton, solveButton, resetButton);
        rightControlBox.setSpacing(0);

        HBox controlBox = new HBox(leftControlBox, middleControlBox, rightControlBox);
        controlBox.setSpacing(10);

        HBox.setHgrow(leftControlBox, Priority.ALWAYS);
        HBox.setHgrow(middleControlBox, Priority.ALWAYS);
        HBox.setHgrow(rightControlBox, Priority.ALWAYS);

        leftControlBox.setAlignment(Pos.CENTER_LEFT);
        middleControlBox.setAlignment(Pos.CENTER);
        rightControlBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(boardGrid, controlBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rush Hour");
        primaryStage.show();
    }

    private void handleResetButton(String filename) {
        rushHour = new RushHour(filename);
        updateBoard();
        moveCount=0;
        gameOver = false;
        moveCounterLabel.setText("Moves: " + moveCount);
        statusLabel.setText("    New Game!");
        statusLabel.setStyle("-fx-text-fill: green;");
    }

    private void solveGame() {
        RushHourSolver solver = RushHourSolver.solve(rushHour);
        if (solver != null) {
            new Thread(() -> {
                ArrayList<Move> moves = solver.getMoves(); // get all moves from RushHourSolver
                for (Move move : moves) {
                    Platform.runLater(() -> {
                        try {
                            rushHour.moveVehicle(move); // make move
                            vehicleMoved(rushHour.getVehicleBySymbol(move.symbol));
                        } catch (RushHourException e) {
                            statusLabel.setText("    Invalid Move: " + e.getReason());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        }
                    });
                    try {
                        Thread.sleep(250); // sleep small amount time (~250ms)
                    } catch (InterruptedException e) {
                        statusLabel.setText("    No solution found!");
                        statusLabel.setStyle("-fx-text-fill: red;");
                    }
                }
            }).start();
        }
    }
    

    private Object handleHintButton() throws CloneNotSupportedException {
        if (!gameOver) {
            Move hint = rushHour.getHint();
            if (hint == null) {
                statusLabel.setText("    No moves available!");
            } else {
                Vehicle vehicle = rushHour.getVehicleBySymbol(hint.symbol);
                highlightHint(vehicle, hint.dir);
            }
        }
        return null;
    }

    private void highlightHint(Vehicle vehicle, Direction direction) {
        if (highlightedVehicleButton != null) {
            clearHint();
        }
    
        // Find the front button of the vehicle
        Button frontButton = null;
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Button && ((Button) node).getText().equals(vehicle.vertical ? "\u2193" : "\u2192")) {
                frontButton = (Button) node;
                break;
            }
        }
    
        // Find the buttons that the vehicle occupies
        List<Button> buttons = new ArrayList<>();
        buttons.add(frontButton);
        if (vehicle.length > 2) {
            for (int i = 1; i < vehicle.length - 1; i++) {
                Button button = null;
                int row, col;
                if (vehicle.vertical) {
                    row = vehicle.getFront().getRow() - i;
                    col = vehicle.getFront().getCol();
                } else {
                    row = vehicle.getFront().getRow();
                    col = vehicle.getFront().getCol() - i;
                }
                for (Node node : boardGrid.getChildren()) {
                    if (node instanceof Button && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                        button = (Button) node;
                        break;
                    }
                }
                buttons.add(button);
            }
        }
    
        // Update the style of the buttons to add a border around the hint
        for (Button button : buttons) {
            String originalStyle = button.getStyle();
            originalButtonStyles.put(button, originalStyle);
            buttons.get(0).setStyle(buttons.get(0).getStyle() + " -fx-border-color: yellow; -fx-border-width: 2;");
            frontButton.setStyle(frontButton.getStyle() + " -fx-border-color: yellow; -fx-border-width: 2;");
            buttons.get(buttons.size() - 1).setStyle(buttons.get(buttons.size() - 1).getStyle() + " -fx-border-color: yellow; -fx-border-width: 2;");

        }

        statusLabel.setText("    Try the highlighted hint above!");
    }
    
    private void clearHint() {
        if (highlightedVehicleButton != null) {
            String originalStyle = originalButtonStyles.get(highlightedVehicleButton);
            if (originalStyle != null) {
                highlightedVehicleButton.setStyle(originalStyle);
            }
            highlightedVehicleButton = null;
        }
    }
    
    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
    
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Rectangle cell = new Rectangle(100, 100, Color.DARKGREY);
                StackPane stackPane = new StackPane(cell);
                stackPane.setBorder(new Border(
                    new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                if (row == 2 && col == 5) {
                    Label exitLabel = new Label("EXIT");
                    exitLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 28;");
                    stackPane.getChildren().add(exitLabel);
                }
    
                grid.add(stackPane, col, row);
            }
        }
        return grid;
    }
    
    
    private void initializeVehicles() {
        for (Vehicle vehicle : rushHour.getVehicles()) {
            if (!vehicleColors.containsKey(vehicle.symbol)) {
                vehicleColors.put(vehicle.symbol, vehicle.symbol == 'R' ? Color.RED : generateUniqueColor());
            }
    
            Button vehicleButtonF = createVehicleButton(vehicle, vehicleColors.get(vehicle.symbol), true, false);
            Button vehicleButtonB = createVehicleButton(vehicle, vehicleColors.get(vehicle.symbol), false, false);
    
            // Render middle parts for vehicles with length 3 or more
            List<Button> middleButtons = new ArrayList<>();
            if (vehicle.length > 2) {
                for (int i = 1; i < vehicle.length - 1; i++) {
                    Button vehicleButtonM = createVehicleButton(vehicle, vehicleColors.get(vehicle.symbol), false, true);
                    middleButtons.add(vehicleButtonM);
                    int row, col;
                    if (vehicle.vertical) {
                        row = vehicle.getFront().getRow() - i;
                        col = vehicle.getFront().getCol();
                    } else {
                        row = vehicle.getFront().getRow();
                        col = vehicle.getFront().getCol() - i;
                    }
                    boardGrid.add(vehicleButtonM, col, row);
                }
            }
    
            vehicleB.add(vehicleButtonB);
            vehicleB.add(vehicleButtonF);
            vehicleB.addAll(middleButtons);
    
            vehicleButtonF.setOnMouseClicked(event -> {
                if (canMoveVehicle()) {
                    try {
                        if (vehicle.vertical) {
                            rushHour.moveVehicle(new Move(vehicle.symbol, Direction.DOWN));
                        } else {
                            rushHour.moveVehicle(new Move(vehicle.symbol, Direction.RIGHT));
                        }
                        vehicleMoved(vehicle);
                    } catch (RushHourException e) {
                        statusLabel.setText("    Invalid Move: " + e.getReason());
                        statusLabel.setStyle("-fx-text-fill: red;");
                    }
                }
            });

            vehicleButtonB.setOnMouseClicked(event -> {
                if (canMoveVehicle()) {
                    try {
                        if (vehicle.vertical) {
                            rushHour.moveVehicle(new Move(vehicle.symbol, Direction.UP));
                        } else {
                            rushHour.moveVehicle(new Move(vehicle.symbol, Direction.LEFT));
                        }
                        vehicleMoved(vehicle);
                    } catch (RushHourException e) {
                        statusLabel.setText("    Invalid Move: " + e.getReason());
                        statusLabel.setStyle("-fx-text-fill: red;");
                    }
                }
            });

            boardGrid.add(vehicleButtonF, vehicle.getFront().getCol(), vehicle.getFront().getRow());
            boardGrid.add(vehicleButtonB, vehicle.getBack().getCol(), vehicle.getBack().getRow());
        }
    }
    
    private Button createVehicleButton(Vehicle vehicle, Color color, boolean isFront, boolean isMiddle) {
        Button button = new Button();
        button.setMinSize(100, 100);
        button.setMaxSize(100, 100);
    
        // Define corner radius based on whether it's the front of the vehicle
        String cornerRadius = "";
        if (isFront) {
            if (vehicle.vertical) {
                cornerRadius = "-fx-background-radius: 0 0 15 15;"; // rounded corners at the bottom for vertical
            } else {
                cornerRadius = "-fx-background-radius: 0 15 15 0;"; // rounded corners on the right for horizontal
            }
        } else {
            cornerRadius = "-fx-background-radius: 0;"; // no rounded corners for middle or back
        }
    
        // Define arrow style
        String arrowStyle = "-fx-font-size: 24; -fx-text-fill: white;";
    
        // Set the text to arrows for front and back, and remove text for middle blocks
        if (isFront) {
            if (vehicle.vertical) {
                button.setText("\u2193"); // Down arrow for vertical front
            } else {
                button.setText("\u2192"); // Right arrow for horizontal front
            }
        } else if (isMiddle) {
            button.setText(""); // no text for middle
        } else {
            if (vehicle.vertical) {
                button.setText("\u2191"); // Up arrow for vertical back
            } else {
                button.setText("\u2190"); // Left arrow for horizontal back
            }
        }
    
        // Combine the color, arrow style, and corner radius into one style string
        String buttonStyle = "-fx-base: " + color.toString().replace("0x", "#") + "; " + cornerRadius + arrowStyle;
    
        // Set the combined style on the button
        button.setStyle(buttonStyle);

        // Prevent the button from receiving focus
        button.setFocusTraversable(false);
    
        return button;
    }    

    private Color generateUniqueColor() {
        Color color;
        do {
            // Generate a new random color
            color = Color.color(Math.random(), Math.random(), Math.random());
            // Check if the generated color is too similar to any used color
        } while (isColorTooSimilar(color, usedColors));
        
        // Add the newly generated unique color to the list of used colors
        usedColors.add(color);
        
        return color;
    }
    
    private boolean isColorTooSimilar(Color newColor, List<Color> usedColors) {
        for (Color usedColor : usedColors) {
            if (colorDifference(newColor, usedColor) < 0.2) { // Threshold can be adjusted
                return true; // Colors are too similar
            }
        }
        return false; // New color is sufficiently distinct
    }
    
    private double colorDifference(Color c1, Color c2) {
        double rDiff = Math.abs(c1.getRed() - c2.getRed());
        double gDiff = Math.abs(c1.getGreen() - c2.getGreen());
        double bDiff = Math.abs(c1.getBlue() - c2.getBlue());
        return rDiff + gDiff + bDiff; // Sum of differences
    }

    public void updateBoard() {
        boardGrid.getChildren().removeAll(vehicleB);
        initializeVehicles();
    }

    private void updateGUI() {
        boardGrid.getChildren().clear(); // Remove all current children nodes from the grid

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                char symbol = rushHour.getSymbolAt(row, col); // Assuming there is a method to get the symbol at a specific position in the game model
                Text cellText = new Text(String.valueOf(symbol));
                boardGrid.add(cellText, col, row);
            }
        }
    }

    private boolean canMoveVehicle() {
        if (gameOver) {
            statusLabel.setStyle("-fx-text-fill: black;");
            statusLabel.setText("    Game over!  -  Hit Reset To Start Over");
            return false;
        }
        return true;
    }

    @Override
    public void vehicleMoved(Vehicle vehicle) {
        clearHint();
        updateBoard();
        statusLabel.setText("    Smart Move!");
        statusLabel.setStyle("-fx-text-fill: black;");

        moveCount++;
        moveCounterLabel.setText("Moves: " + moveCount);

        if(rushHour.isGameOver()) {
            statusLabel.setText("    Congratulation!");
            statusLabel.setStyle("-fx-text-fill: green;");
            gameOver = true;
        }
    }
}