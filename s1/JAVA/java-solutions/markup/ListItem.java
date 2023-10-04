package markup;

import java.util.List;

public class ListItem extends AbstractListItems {

    public ListItem(List<ItemsOfList> li) {
        super(li);
    }

    @Override
    public void toHtml(StringBuilder txt) {
        txt.append("<li>");
        for (Html val: listItem) {
            val.toHtml(txt);
        }
        txt.append("</li>");
    }
}
