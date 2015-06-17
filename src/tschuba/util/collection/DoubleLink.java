package tschuba.util.collection;

/**
 *
 * @author tsc
 * @param <T>
 */
public class DoubleLink<T> implements BackLink<DoubleLink<T>>, ForthLink<DoubleLink<T>> {

    private int index = 0;
    private DoubleLink<T> predecessor;
    private DoubleLink<T> successor;
    private T value;

    /**
     *
     */
    public DoubleLink() {
        super();
    }

    /**
     *
     * @param value
     */
    public DoubleLink(T value) {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    @Override
    public DoubleLink<T> getPredecessor() {
        return predecessor;
    }

    /**
     *
     * @param predecessor
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
     *
     * @param successor
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
     *
     * @return
     */
    public DoubleLink<T> first() {
        DoubleLink<T> currentLink = this;
        while (currentLink != null) {
            currentLink = currentLink.getPredecessor();
        }
        return currentLink;
    }

    /**
     *
     * @return
     */
    public DoubleLink<T> last() {
        DoubleLink<T> currentLink = this;
        while (currentLink != null) {
            currentLink = currentLink.getSuccessor();
        }
        return currentLink;
    }

    /**
     *
     * @return
     */
    public T getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     *
     * @param value
     * @return
     */
    public DoubleLink<T> prepend(T value) {
        DoubleLink<T> link = new DoubleLink<>(value);
        this.setPredecessor(link);
        return link;
    }

    /**
     *
     * @param value
     * @return
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
     *
     * @param index
     * @return
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
     *
     * @param index
     * @return
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
     *
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
