package org.pandora.master.scene.impl.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.BasicBuilder;
import org.pandora.master.utils.TimeUtils;

public class HomeUI {

    @Getter
    private Text consoleText;
    @Getter
    private ScrollPane consoleScrollPane;
    @Getter
    private final BasicBuilder basicBuilder;
    @Getter
    private final Master master;
    @Getter
    private AnchorPane anchorPane;
    @Getter
    private Scene scene;
    @Getter
    private Label agentNumber;
    @Getter
    private Label masterNumber;
    @Getter
    private Label countryNumber;
    @Getter
    private final ObservableList<PieChart.Data> pieChartData;
    @Getter
    private final PieChart countriesChart;
    @Getter
    private final Label chartValue;

    public HomeUI(final Master master, final BasicBuilder basicBuilder) {
        this.basicBuilder = basicBuilder;
        this.master = master;
        this.pieChartData = FXCollections.observableArrayList();
        this.countriesChart = new PieChart(this.pieChartData);
        this.chartValue = basicBuilder.getBuilderManager().getLabelBuilder().buildLabel("", 0, 0, "white", "Verdana", 15);
    }

    public void initialize() {
        final Scene scene = this.basicBuilder.getScene();
        final AnchorPane anchorPane = this.basicBuilder.getAnchorPane();
        final BuilderManager builderManager = this.basicBuilder.getBuilderManager();

        this.basicBuilder.getHome().setId("currentNavButton");

        final Pane bottomPane = builderManager.getPaneBuilder().buildPane(0, 552, 1080, 101, new Background(new BackgroundFill(Color.rgb(34, 35, 48), CornerRadii.EMPTY, Insets.EMPTY)));

        final Pane agentsPane = builderManager.getPaneBuilder().buildPane(15, 149, 336, 120, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));
        final Pane masterPane = builderManager.getPaneBuilder().buildPane(371, 149, 336, 120, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));
        final Pane diffCountriesPane = builderManager.getPaneBuilder().buildPane(727, 149, 336, 120, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));
        final Pane consolePane = builderManager.getPaneBuilder().buildPane(15, 289, 692, 349, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));
        final Pane chartPane = builderManager.getPaneBuilder().buildPane(727, 289, 336, 349, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        /*___________________________
        Chart
        ___________________________*/
        this.countriesChart.setTitle("TOP 10 COUNTRIES");
        this.countriesChart.setPrefSize(336, 334);
        this.countriesChart.setTranslateX(727);
        this.countriesChart.setTranslateY(294);
        this.countriesChart.setId("countriesChart");

        /*___________________________
        Agents Stats
        ___________________________*/
        final ImageView agentImage = builderManager.getImageViewBuilder().buildImageView("images/dashboard/agent.png", 12, 12, 92, 96);
        this.agentNumber = builderManager.getLabelBuilder().buildLabel("0", 112, 26, "WHITE", "Verdana", 25);
        final Label agentText = builderManager.getLabelBuilder().buildLabel("Active Agents", 112, 56, Color.rgb(189, 190, 207), "Verdana", 11);
        agentsPane.getChildren().addAll(this.agentNumber, agentText, agentImage);

        /*___________________________
        Master Stats
        ___________________________*/
        final ImageView masterImage = builderManager.getImageViewBuilder().buildImageView("images/dashboard/master.png", 11, 12, 96, 96);
        this.masterNumber = builderManager.getLabelBuilder().buildLabel("0", 112, 26, "WHITE", "Verdana", 25);
        final Label masterText = builderManager.getLabelBuilder().buildLabel("Active Master(s)", 112, 56, Color.rgb(189, 190, 207), "Verdana", 11);
        masterPane.getChildren().addAll(this.masterNumber, masterText, masterImage);

        /*___________________________
        Country Stats
        ___________________________*/
        final ImageView countryImage = builderManager.getImageViewBuilder().buildImageView("images/dashboard/country.png", 16, 21, 78, 78);
        this.countryNumber = builderManager.getLabelBuilder().buildLabel("0", 112, 26, "WHITE", "Verdana", 25);
        final Label countryText = builderManager.getLabelBuilder().buildLabel("Different Active Countries", 112, 56, Color.rgb(189, 190, 207), "Verdana", 11);
        diffCountriesPane.getChildren().addAll(countryText, this.countryNumber, countryImage);

        /*___________________________
        Console Log
        ___________________________*/
        this.consoleText = builderManager.getTextBuilder().buildText(TimeUtils.getDate() + "Welcome!", 0, 0, Color.WHITE, "Consolas", 12);
        this.consoleScrollPane = builderManager.getScrollPaneBuilder().buildScrollPane(true, this.consoleText, 0, 0, 692, 349);
        this.consoleScrollPane.setId("consoleScrollPane");
        this.consoleScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.consoleScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.consoleText.wrappingWidthProperty().bind(scene.widthProperty());

        consolePane.getChildren().addAll(this.consoleScrollPane);

        anchorPane.getChildren().addAll(bottomPane, consolePane, agentsPane, masterPane, diffCountriesPane, chartPane, this.countriesChart, this.chartValue);
        this.anchorPane = anchorPane;
        this.scene = scene;
    }

    public void addConsoleLine(final String text) {
        this.consoleText.setText(this.consoleText.getText() + "\n" + TimeUtils.getDate() + text);
        this.consoleScrollPane.setVvalue(1.0d);
    }
}
