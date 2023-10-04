package markup;

import java.util.List;

public class Code extends AbstractMarkup implements ItemsOfParagraph {

    public Code(List<ItemsOfParagraph> text) {
        super(text, "code", "'");
    }


}