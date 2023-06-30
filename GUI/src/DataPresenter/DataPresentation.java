package DataPresenter;


import DTO.FlowExecutionData.impl.IOData;
import javafx.scene.Node;

import java.util.List;
import java.util.Set;

public interface DataPresentation {
    Node getDataPresent(Set<IOData> data);
}
