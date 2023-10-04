package markup;

import java.util.List;

public class Paragraph extends AbstractMarkup implements ItemsOfList {

    public Paragraph(List<ItemsOfParagraph> list) {
        super(list, "", "");
    }


    @Override
    public void toMarkdown(StringBuilder txt) {
        for (Markdown val: text) {
            val.toMarkdown(txt);
        }
    }

    @Override
    public void toHtml(StringBuilder txt) {
        for (Html val: text) {
            val.toHtml(txt);
        }
    }


}
