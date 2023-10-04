package markup;

import java.util.List;

public abstract class AbstractLists implements ItemsOfList {
    protected List<ListItem> list;
    protected String type;

    protected AbstractLists(List<ListItem> list, String type) {
        this.list = list;
        this.type = type;
    }


    @Override
    public void toHtml(StringBuilder txt){
        txt.append("<" + type + ">");
        for (Html val: list) {
            StringBuilder x = new StringBuilder();
            val.toHtml(x);
            txt.append(x);
        }
        txt.append("</" + type + ">");
    }

}
