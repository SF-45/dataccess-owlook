package space.sadfox.dataccess.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import space.sadfox.dataccess.dataccess.DataEntity;

public class SingleDataTableView extends TableView<SingleDataTableView.TableEntity> {

    public static class TableEntity {
    	StringProperty field;
        String value;

        private TableEntity(StringProperty field, String value) {
            this.field = field;
            this.value = value;
        }
    }

    public SingleDataTableView() {
        TableColumn<TableEntity, String> fieldColumn = new TableColumn<>("Name");
        fieldColumn.setCellValueFactory(entity -> entity.getValue().field);
        getColumns().add(fieldColumn);
        
        TableColumn<TableEntity, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(entity -> {
            return new SimpleStringProperty(entity.getValue().value);
        });
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setEditable(true);
        getColumns().add(valueColumn);
        
        setEditable(true);
    }

    public void setDataEntity(DataEntity dataEntity) {
        ObservableList<TableEntity> tableEntities = FXCollections.observableArrayList();
        dataEntity.getFields().forEach(field -> {
            tableEntities.add(new TableEntity(field.fieldNameProperty(), dataEntity.getValue(field)));
        });
        setItems(tableEntities);
    }
}
