package tschuba.util.collection;

/**
 * Interface for linked structures using backward references.
 *
 * @author tsc
 * @param <L> type of the referenced link.
 */
public interface BackLink<L extends BackLink> extends Link {

    /**
     * @return this link's predecessor (back reference).
     */
    L getPredecessor();
}
