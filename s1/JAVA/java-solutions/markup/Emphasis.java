package markup;

import java.util.List;

public class Emphasis extends AbstractMarkup implements ItemsOfParagraph {
    public Emphasis (List<ItemsOfParagraph> text) {
        super(text, "em", "*");
    }

}
