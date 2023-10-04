package markup;

import java.util.List;

public class Strong extends AbstractMarkup implements ItemsOfParagraph {

    public Strong(List<ItemsOfParagraph> text) {
        super(text, "strong", "__");
    }


}
