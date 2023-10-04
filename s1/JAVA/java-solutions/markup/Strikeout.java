package markup;

import java.util.List;

public class Strikeout extends AbstractMarkup implements ItemsOfParagraph {
    public Strikeout(List<ItemsOfParagraph> text) {
        super(text, "s", "~");
    }


}
