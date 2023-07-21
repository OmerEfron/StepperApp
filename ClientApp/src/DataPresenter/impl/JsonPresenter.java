package DataPresenter.impl;

import DTO.FlowExecutionData.IOData;
import DataPresenter.api.DataPresenterAbstractClass;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class JsonPresenter extends DataPresenterAbstractClass {

    public JsonPresenter(IOData data){
        super(data);
        JsonObject json=data.getDataValue(JsonObject.class);
        TreeItem<String> rootItem = convertJsonObjectToTreeItem(json, "Root");
        TreeView<String> treeView = new TreeView<>(rootItem);
        presentation.getChildren().add(treeView);
    }
    private TreeItem<String> convertJsonObjectToTreeItem(JsonObject jsonObject, String name) {
        TreeItem<String> rootItem = new TreeItem<>(name);
        for (String key : jsonObject.keySet()) {
            JsonElement value = jsonObject.get(key);
            if (value.isJsonObject()) {
                TreeItem<String> childItem = convertJsonObjectToTreeItem(value.getAsJsonObject(), key);
                rootItem.getChildren().add(childItem);
            } else {
                TreeItem<String> leafItem = new TreeItem<>(key + ": " + value.getAsString());
                rootItem.getChildren().add(leafItem);
            }
        }
        return rootItem;
    }
}
