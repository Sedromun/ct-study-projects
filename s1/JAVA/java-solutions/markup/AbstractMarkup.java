package markup;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMarkup implements Html, Markdown {
    protected List<ItemsOfParagraph> text;
    protected String typeMarkdown;
    protected String typeHtml;

    protected AbstractMarkup() {
        text = new ArrayList<>();
        typeHtml = "";
        typeMarkdown = "";
    }


    protected AbstractMarkup(List<ItemsOfParagraph> list, String typeHtml, String typeMarkdown) {
        text = list;
        this.typeHtml = typeHtml;
        this.typeMarkdown = typeMarkdown;
    }

    @Override
    public void toMarkdown(StringBuilder txt) {
        txt.append("<" + typeMarkdown + ">");
        for (Markdown val: text) {
            val.toMarkdown(txt);
        }
        txt.append("</" + typeMarkdown + ">");
    }

    @Override
    public void toHtml(StringBuilder txt) {
        txt.append("<" + typeHtml + ">");
        for (Html val: text) {
            val.toHtml(txt);
        }
        txt.append("</" + typeHtml + ">");
    }

}
