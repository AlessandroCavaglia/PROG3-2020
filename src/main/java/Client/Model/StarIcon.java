package Client.Model;

import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.value.ObservableValueBase;


public class StarIcon extends ObservableValueBase<MaterialDesignIcon> {
    MaterialDesignIcon icon=MaterialDesignIcon.STAR_BORDER;
    @Override
    public MaterialDesignIcon getValue() {
        return icon;
    }
    public void setFavuorite(){
        icon=MaterialDesignIcon.STAR;
        fireValueChangedEvent();
    }
    public void setNotFavuorite(){
        icon=MaterialDesignIcon.STAR_BORDER;
        fireValueChangedEvent();
    }
}
