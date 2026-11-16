package tool;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.internal.JacksonUtils;
import com.vaadin.flow.internal.nodefeature.ElementListenerMap;

public class Action {

    public static void click(Component item) {
        item.getElement()
            .getNode()
            .getFeature(ElementListenerMap.class)
            .fireEvent(new com.vaadin.flow.dom.DomEvent(item.getElement(), "click", JacksonUtils.createObjectNode()));
    }

}
