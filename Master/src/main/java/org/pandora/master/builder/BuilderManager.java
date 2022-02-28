package org.pandora.master.builder;

import lombok.Getter;
import org.pandora.master.builder.impl.node.*;

public class BuilderManager {

    @Getter
    private final PaneBuilder paneBuilder;
    @Getter
    private final TextFieldBuilder textFieldBuilder;
    @Getter
    private final ButtonBuilder buttonBuilder;
    @Getter
    private final ImageViewBuilder imageViewBuilder;
    @Getter
    private final LabelBuilder labelBuilder;
    @Getter
    private final RectangleBuilder rectangleBuilder;
    @Getter
    private final DropShadowBuilder dropShadowBuilder;
    @Getter
    private final ScrollPaneBuilder scrollPaneBuilder;
    @Getter
    private final TextBuilder textBuilder;
    @Getter
    private final SelectorBuilder selectorBuilder;
    @Getter
    private final ChoiceBoxBuilder choiceBoxBuilder;
    @Getter
    private final TableViewBuilder tableViewBuilder;
    @Getter
    private final TableColumnBuilder tableColumnBuilder;
    @Getter
    private final CheckBoxBuilder checkBoxBuilder;
    @Getter
    private final BasicLineBuilder basicLineBuilder;

    public BuilderManager() {
        this.paneBuilder = new PaneBuilder();
        this.labelBuilder = new LabelBuilder();
        this.textFieldBuilder = new TextFieldBuilder();
        this.buttonBuilder = new ButtonBuilder();
        this.textBuilder = new TextBuilder();
        this.imageViewBuilder = new ImageViewBuilder();
        this.choiceBoxBuilder = new ChoiceBoxBuilder();
        this.tableViewBuilder = new TableViewBuilder();
        this.rectangleBuilder = new RectangleBuilder();
        this.dropShadowBuilder = new DropShadowBuilder();
        this.scrollPaneBuilder = new ScrollPaneBuilder();
        this.selectorBuilder = new SelectorBuilder();
        this.tableColumnBuilder = new TableColumnBuilder();
        this.checkBoxBuilder = new CheckBoxBuilder();
        this.basicLineBuilder = new BasicLineBuilder();
    }
}
