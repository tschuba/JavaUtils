package tschuba.util.collection;

/**
 *
 * @author tsc
 * @param <L>
 */
public interface ForthLink<L extends ForthLink> extends Link {

    /**
     *
     * @return
     */
    L getSuccessor();
}
