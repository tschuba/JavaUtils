package tschuba.util.collection;

/**
 *
 * @author tsc
 * @param <L>
 */
public interface BackLink<L extends BackLink> extends Link {

    /**
     *
     * @return
     */
    L getPredecessor();
}
