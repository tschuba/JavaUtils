package tschuba.util.props;

public class Section {
    private int lineIndex;
    private String content;

    public Section() {
    }

    public Section(int lineIndex, String content) {
        this.lineIndex = lineIndex;
        this.content = content;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Section{" +
                "lineIndex=" + lineIndex +
                ", content='" + content + '\'' +
                '}';
    }
}
