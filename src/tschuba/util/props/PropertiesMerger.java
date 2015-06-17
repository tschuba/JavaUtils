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

    private Map<String, DoubleLink<PropertiesSection>> sectionMap = new LinkedHashMap<>();
    private DoubleLink<PropertiesSection> lastLink;

    public PropertiesMerger() {

    }

    public void add(PropertiesReader reader) {
        if (this.sectionMap.isEmpty()) {
            this.addFirstFile(reader);
        } else {
            this.addAdditionalFile(reader);
        }
    }

    private void addFirstFile(PropertiesReader reader) {
        while (reader.hasNext()) {
            PropertiesSection section = reader.next();
            this.addSectionToEnd(section);
        }
    }

    private void addAdditionalFile(PropertiesReader reader) {
        Stack<PropertiesSection> sectionStack = new Stack<>();
        DoubleLink<PropertiesSection> anchor = null;
        while (reader.hasNext()) {
            PropertiesSection section = reader.next();
            DoubleLink<PropertiesSection> linkForKey = null;
            String key = section.getKey();
            if (key != null) {
                linkForKey = sectionMap.get(key);
                if (linkForKey != null) {
                    linkForKey.setValue(section);
                    anchor = linkForKey;
                } else {
                    linkForKey = this.addSectionToEnd(section);
                }
                anchor = linkForKey;
            }

        }
    }

    /**
     *
     * @param section
     * @return
     */
    private DoubleLink<PropertiesSection> addSectionToEnd(PropertiesSection section) {
        DoubleLink<PropertiesSection> link = new DoubleLink<>(section);
        if (lastLink != null) {
            lastLink.setSuccessor(link);
        }
        lastLink = link;
        return link;
    }
}
