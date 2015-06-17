package tschuba.util.props;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PropertiesSectionBuilder {

    public static final String COMMENT_PATTERN = "^\\s*[#!]";
    public static final String MULTILINE_PATTERN = "\\\\\\s*$";

    private final int lineIndex;
    private final List<String> lines;
    private boolean comment;

    /**
     * @param lineIndex
     * @param line
     */
    public PropertiesSectionBuilder(int lineIndex, String line) {
        this.lines = new ArrayList<>();
        this.lineIndex = lineIndex;
    }

    /**
     * @param line
     * @return
     */
    public boolean addLine(String line) {
        boolean addLine = false;
        if (this.isComment() && this.isComment(line)) {
            addLine = true;
        } else {
            int lastLineIndex = this.lines.size() - 1;
            String lastLine = this.lines.get(lastLineIndex);
            if (this.isEmpty(lastLine) && this.isEmpty(line) || this.isMultiline(lastLine)) {
                addLine = true;
            }
        }

        if (addLine) {
            this.lines.add(line);
        }
        return addLine;
    }

    /**
     * @return
     */
    public int lineCount() {
        return this.lines.size();
    }

    /**
     * @return
     */
    public boolean isComment() {
        return this.comment;
    }

    /**
     * @return
     */
    public PropertiesSection toSection() {
        StringBuilder contentBuilder = new StringBuilder();
        for (String line : lines) {
            if (contentBuilder.length() > 0) {
                contentBuilder.append("\n");
            }
            contentBuilder.append(line);
        }
        boolean multiline = this.lineCount() > 1;
        PropertiesSection section = new PropertiesSection(this.lineIndex, contentBuilder.toString(), this.comment, multiline);
        return section;
    }

    /**
     * @param line
     * @return
     */
    private boolean isComment(String line) {
        boolean matchesPattern = line.matches(COMMENT_PATTERN);
        return matchesPattern;
    }

    /**
     * @param line
     * @return
     */
    private boolean isMultiline(String line) {
        boolean multiline = line.matches(MULTILINE_PATTERN);
        return multiline;
    }

    /**
     *
     * @param line
     * @return
     */
    private boolean isEmpty(String line) {
        boolean empty = line.trim().isEmpty();
        return empty;
    }
}
