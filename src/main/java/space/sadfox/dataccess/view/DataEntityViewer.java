package space.sadfox.dataccess.view;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import space.sadfox.dataccess.dataccess.DataEntity;


public class DataEntityViewer extends Stage {

    private AnchorPane root = new AnchorPane();

    public DataEntityViewer(DataEntity dataEntity) {
        SingleDataTableView tableView = new SingleDataTableView();
        tableView.setDataEntity(dataEntity);
        AnchorPane.setTopAnchor(tableView, 0d);
        AnchorPane.setRightAnchor(tableView, 0d);
        AnchorPane.setBottomAnchor(tableView, 0d);
        AnchorPane.setLeftAnchor(tableView, 0d);
        root.getChildren().add(tableView);

        //setTitle(dataEntity.getFields().stream().filter(Field::isSchemePreview).map(dataEntity::getValue).collect(Collectors.joining(" - ")));

        setScene(new Scene(root, 350, 500));
        setAlwaysOnTop(true);
    }


}
