/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.props;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import tschuba.util.collection.DoubleLink;

/**
 *
 * @author tsc
 */
public class PropertiesMerger {

    private final Map<String, DoubleLink<PropertiesSection>> sectionMap = new LinkedHashMap<>();
    private DoubleLink<PropertiesSection> lastLink;
    private DoubleLink<PropertiesSection> lastBuffereredLink;

    /**
     * Default constructor
     */
    public PropertiesMerger() {
        super();
    }

    /**
     *
     * @param reader
     */
    public void add(PropertiesReader reader) {
        if (this.sectionMap.isEmpty()) {
            this.addFirstFile(reader);
        } else {
            this.addAdditionalFile(reader);
        }
    }

    /**
     *
     * @param reader
     */
    private void addFirstFile(PropertiesReader reader) {
        while (reader.hasNext()) {
            PropertiesSection section = reader.next();
            this.addToEnd(section);
        }
    }

    /**
     *
     * @param reader
     */
    private void addAdditionalFile(PropertiesReader reader) {
        Stack<PropertiesSection> sectionStack = new Stack<>();
        DoubleLink<PropertiesSection> anchor = null;
        while (reader.hasNext()) {
            PropertiesSection section = reader.next();
            String key = section.getKey();
            if (key != null) {
                DoubleLink<PropertiesSection> linkForKey = sectionMap.get(key);
                if (linkForKey != null) {
                    linkForKey.setValue(section);

                    // clear buffer
                    if (lastBuffereredLink != null) {
                        if (anchor == null) {
                            linkForKey.setPredecessor(lastBuffereredLink);
                        } else {
                            anchor.setSuccessor(lastBuffereredLink);
                        }
                        this.lastBuffereredLink = null;
                    }

                    anchor = linkForKey;

                } else {
                    linkForKey = this.addToEnd(section);
                    this.sectionMap.put(key, linkForKey);

                }

            } else {
                this.buffer(section);

            }
        }
    }

    /**
     *
     * @param section
     * @return
     */
    private DoubleLink<PropertiesSection> addToEnd(PropertiesSection section) {
        DoubleLink<PropertiesSection> link = new DoubleLink<>(section);
        this.addToEnd(link);
        return link;
    }

    /**
     *
     * @param section
     */
    private void addToEnd(DoubleLink<PropertiesSection> section) {
        if (lastLink != null) {
            lastLink.setSuccessor(section);
        }
        lastLink = section;
    }

    /**
     *
     * @param section
     */
    private void buffer(PropertiesSection section) {
        DoubleLink<PropertiesSection> sectionLink = new DoubleLink<>(section);
        if (this.lastBuffereredLink != null) {
            this.lastBuffereredLink.setSuccessor(sectionLink);
        }
        this.lastBuffereredLink = sectionLink;
    }
}
