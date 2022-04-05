package Server.Model;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class Cella extends ListCell<String> {
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if(empty || item==null){
            setGraphic(null);
            setText(null);
        }else{
            Label text = new Label();
            text.setText(item);
            if(item.startsWith("Error:")){
                text.setStyle("-fx-text-fill: #5d0808;");
            }else{
                text.setStyle("-fx-text-fill: #000000;");
            }
            setGraphic(text);
        }
    }
}
