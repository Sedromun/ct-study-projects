package markup;

import java.util.List;

public abstract class AbstractListItems implements Html {
    protected List<ItemsOfList> listItem;

    public AbstractListItems(List<ItemsOfList> list) {
        listItem = list;
    }
}
