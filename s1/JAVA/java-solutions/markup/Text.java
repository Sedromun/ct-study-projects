package markup;


public class Text extends AbstractMarkup implements ItemsOfParagraph {
    public String string;
    public Text() {
        string = "";
    }

    public Text(String str) {
        string = str;
    }

    @Override
    public void toMarkdown(StringBuilder txt) {
        txt.append(string);
    }

    @Override
    public void toHtml(StringBuilder txt) {
        txt.append(string);
    }

    @Override
    public String toString() {
        return string;
    }
}
