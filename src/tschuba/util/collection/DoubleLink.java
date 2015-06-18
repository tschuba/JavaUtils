package tschuba.util.collection;

/**
 * Link using backward and forward linking. Can hold a value and maintains index
 * in its chain.
 *
 * @author tsc
 * @param <T> the value's type
 */
public class DoubleLink<T> implements BackLink<DoubleLink<T>>, ForthLink<DoubleLink<T>> {

    private int index = 0;
    private DoubleLink<T> predecessor;
    private DoubleLink<T> successor;
    private T value;

    /**
     * Creates a new empty link for specified value.
     */
    public DoubleLink() {
        super();
    }

    /**
     * Creates a new link for specified value.
     *
     * @param value the value
     */
    public DoubleLink(T value) {
        this.value = value;
    }

    /**
     * @return index of this link in its chain.
     */
    public int getIndex() {
        return index;
    }

    @Override
    public DoubleLink<T> getPredecessor() {
        return predecessor;
    }

    /**
     * Sets the predecessor.
     *
     * @param predecessor the predecessor
     */
    public void setPredecessor(DoubleLink<T> predecessor) {
        if (this.predecessor != null && predecessor == null) {
            this.predecessor.setSuccessor(null);
        }
        this.predecessor = predecessor;
        if (predecessor != null) {
            predecessor.setSuccessor(this);
        }
        this.invalidateIndex();
    }

    @Override
    public DoubleLink<T> getSuccessor() {
        return successor;
    }

    /**
     * Sets the successor.
     *
     * @param successor the new successor
     */
    public void setSuccessor(DoubleLink<T> successor) {
        if (this.successor != null && successor == null) {
            this.successor.setPredecessor(null);
        }
        this.successor = successor;
        if (successor != null) {
            successor.setPredecessor(this);
        }
        this.invalidateIndex();
    }

    /**
     * @return returns the first link in this chain by iterating through
     * predecessors.
     */
    public DoubleLink<T> first() {
        DoubleLink<T> currentLink = this;
        while (currentLink != null) {
            currentLink = currentLink.getPredecessor();
        }
        return currentLink;
    }

    /**
     * @return returns the last link in this chain by iterating through
     * successors.
     */
    public DoubleLink<T> last() {
        DoubleLink<T> currentLink = this;
        while (currentLink != null) {
            currentLink = currentLink.getSuccessor();
        }
        return currentLink;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value.
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Prepends a value by creating a new link for it and inserting that link
     * between this link and its predecessor.
     *
     * @param value the value to insert.
     * @return the link created for the value.
     */
    public DoubleLink<T> prepend(T value) {
        DoubleLink<T> link = new DoubleLink<>(value);
        this.setPredecessor(link);
        return link;
    }

    /**
     * Appends a value by creating a new link for it and inserting that link
     * between this link and its successor.
     *
     * @param value the value to insert.
     * @return the link created for the value.
     */
    public DoubleLink<T> append(T value) {
        DoubleLink<T> link = new DoubleLink<>(value);
        this.setSuccessor(link);
        return link;
    }

    @Override
    public void remove() {
        if (this.successor != null) {
            if (this.predecessor != null) {
                this.successor.setPredecessor(this.successor);
            } else {
                this.successor.setPredecessor(null);
            }
        } else if (this.predecessor != null) {
            this.predecessor.setSuccessor(null);
        }
        this.invalidateIndex();
    }

    /**
     * @param index index of the link to find
     * @return returns a link from the chain by its index.
     */
    public DoubleLink<T> findByIndex(int index) {
        DoubleLink<T> currentLink = this;
        while (currentLink != null && currentLink.getIndex() != index) {
            currentLink = this.getLinkClosestToIndex(index);
        }
        if (currentLink != null && currentLink.index == index) {
            return currentLink;
        } else {
            return null;
        }
    }

    /**
     * @param index index determining direction
     * @return returns the next link closest to given index.
     */
    private DoubleLink<T> getLinkClosestToIndex(int index) {
        if (this.index < index) {
            return this.successor;
        } else if (this.index > index) {
            return this.predecessor;
        } else {
            return this;
        }
    }

    /**
     * Invalidates and recalculates index.
     */
    private void invalidateIndex() {
        int oldIndex = this.index,
                newIndex = 0;
        if (this.getPredecessor() != null) {
            newIndex = this.getPredecessor().getIndex() + 1;
        }
        this.index = newIndex;
        if (oldIndex != newIndex && this.successor != null) {
            this.successor.invalidateIndex();
        }
    }
}
