package tschuba.util.collection;

/**
 * Interface for linked structures using forward references.
 *
 * @author tsc
 * @param <L> type of the referenced link.
 */
public interface ForthLink<L extends ForthLink> extends Link {

    /**
     * @return this link's successor (forward reference).
     */
    L getSuccessor();
}
