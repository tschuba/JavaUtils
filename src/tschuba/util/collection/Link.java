package tschuba.util.collection;

/**
 * Interface defining common methods for linked structures.
 *
 * @author tsc
 */
public interface Link {

    /**
     * Removes this element linking its predecessor and successor to keep the
     * chain complete.
     */
    void remove();
}
