package tschuba.util.props;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 */
public class PropertiesReader implements Iterator<PropertiesSection> {

    private final Scanner scanner;
    private String nextLine;
    private int cursor;

    /**
     * @param file
     * @throws FileNotFoundException
     */
    public PropertiesReader(String file) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(file);
        this.scanner = new Scanner(inputStream);
    }

    /**
     * @param input
     */
    public PropertiesReader(InputStream input) {
        this.scanner = new Scanner(input);
    }

    /**
     * @return
     */
    @Override
    public boolean hasNext() {
        return this.nextLine != null || scanner.hasNextLine();
    }

    /**
     * @return
     */
    @Override
    public PropertiesSection next() {
        if (this.nextLine == null) {
            this.nextLine = this.nextLine();
        }

        // create section by iterating over matching lines
        PropertiesSectionBuilder builder = new PropertiesSectionBuilder(this.cursor, this.nextLine);
        boolean hasNextLine;
        while (hasNextLine = this.scanner.hasNextLine()) {
            this.nextLine = this.nextLine();
            if (!builder.addLine(this.nextLine)) {
                break;
            }
        }

        if (!hasNextLine) {
            this.nextLine = null;
        }

        // return section made by builder
        return builder.toSection();
    }

    /**
     *
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation \"remove\" not supported!");
    }

    /**
     * @return
     */
    private String nextLine() {
        this.cursor++;
        return this.scanner.nextLine();
    }
}
