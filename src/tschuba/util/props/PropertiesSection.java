package tschuba.util.props;

/**
 *
 *
 */
public class PropertiesSection extends Section {

    private boolean comment;
    private boolean multiline;
    private String key;
    private String value;

    /**
     *
     */
    public PropertiesSection() {
        super();
        this.initKeyAndValue();
    }

    /**
     * @param lineIndex
     * @param content
     */
    public PropertiesSection(int lineIndex, String content) {
        super(lineIndex, content);
        this.initKeyAndValue();
    }

    /**
     * @param lineIndex
     * @param content
     * @param comment
     * @param multiline
     */
    public PropertiesSection(int lineIndex, String content, boolean comment, boolean multiline) {
        super(lineIndex, content);
        this.comment = comment;
        this.multiline = multiline;
        this.initKeyAndValue();
    }

    /**
     *
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value 
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return
     */
    public boolean isComment() {
        return comment;
    }

    /**
     * @return
     */
    public boolean isMultiline() {
        return multiline;
    }

    private void initKeyAndValue() {
        if (this.isComment()) {
            return;
        }

        String content = this.getContent().trim();
        boolean keyFound = true, valueFound = false;
        for (int index = 0; index < content.length(); index++) {
            char currentChar = content.charAt(index);
            boolean delimiter = (currentChar == ' ' || currentChar == '=' || currentChar == ':');
            if (delimiter && !keyFound) {
                this.key = content.substring(0, index);
                this.key = this.key.trim();
                keyFound = true;

            } else if (!delimiter && keyFound) {
                this.value = content.substring(index).trim();
                break;
            }
        }
    }
}
