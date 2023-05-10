package utils;

import java.util.Iterator;

/**
 * Intrusive doubly linked list,
 * with which insertion and removal from the container in O(1) time can be achieved.
 * @param <T> Type (T) of elements in the list (i.e. data object holding nodes).
 * @param <P> Type of the parent (P) object holding the list.
 *
 * @see <a href="https://github.com/torvalds/linux/blob/v5.18/include/linux/list.h">
 *     Linux Intrusive List Implementation</a>
 */
public class IntrusiveList<T, P> implements Iterable<IntrusiveList.Node<T, P>> {

    /**
     * Node for intrusive lists.
     * <br>
     * Node is implemented as a static nested class because of the needs to visiting
     * and reassign some IntrusiveList's private fields.
     * @param <T> Type (T) of elements in the list (i.e. data object holding the Node itself).
     * @param <P> Type of th e parent (P) object holding the list that this node belongs to.
     *
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html">
     *     Oracle Tut: Why use static nested class</a>
     */
    public static class Node<T, P> {
        /**
         * The data object referring the node.
         * (In intrusive lists, data hold the node other than being
         * held by node)
         */
        private T dataElem = null;

        public T getData() {
            return dataElem;
        }

        public void setData(T dataElem) {
            this.dataElem = dataElem;
        }

        /**
         * Whether the Node is a dummy sentinel with no data referring it.
         * @return Yes or no.
         */
        private boolean isDummy() {
            return dataElem == null;
        }


        /**
         * Intrusive list object (parent) where the node resides.
         */
        private IntrusiveList<T, P> parentList = null;

        public IntrusiveList<T, P> getParentList() {
            return parentList;
        }

        /**
         * Whether the Node belongs to no intrusive list.
         * @return True if it has no parent list. Otherwise, return false.
         */
        public boolean isDangling() {
            return this.getParentList() == null;
        }

        /**
         * Retrieve the reference to the Object holding the parent list of the node.
         * @return Parent object that holds the parent list of the node.
         * If the node is dangling, return null.
         */
        public P getParentHolder() {
            return this.isDangling() ? null : this.getParentList().getParent();
        }

        /**
         * Previous Node.
         */
        private Node<T, P> prev = null;

        /**
         * Retrieve the previous data node.
         * @return The previous data node. If no data node (null/dummy node) found, return null.
         */
        public Node<T, P> getPrev() {
            return prev.isDummy() ? null : prev;
        }

        /**
         * Next Node.
         */
        private Node<T, P> next = null;

        /**
         * Retrieve the next data node.
         * @return The next data node. If no data node (null/dummy node) found, return null.
         */
        public Node<T, P> getNext() {
            return next.isDummy() ? null : next;
        }

        /**
         * Construct a Node without data object referring it (i.e. a dummy sentinel).
         */
        private Node() {

        }

        /**
         * Construct a Node.
         * @param data The data object referring the list.
         */
        public Node(T data) {
            this.dataElem = data;
        }


        /**
         * Insert this Node itself right after another specified Node.
         * @param node The Node specified that will be the new previous node of this.
         */
        public void insertAfter(Node<T, P> node) {
            // Modify pointers.
            this.prev = node;
            this.next = node.next;
            node.next = this;
            if (this.next != null) {
                this.next.prev = this;
            }
            // Redirect list holder.
            this.parentList = node.parentList;
            // Increase list size.
            this.parentList.numNode++;
        }

        /**
         * Insert this Node itself right before another specified Node.
         * @param node The Node specified that will be the new next node of this.
         */
        public void insertBefore(Node<T, P> node) {
            // Modify pointers.
            this.prev = node.prev;
            this.next = node;
            node.prev = this;
            if (this.prev != null) {
                this.prev.next = this;
            }
            // Redirect list holder.
            this.parentList = node.parentList;
            // Increase list size.
            this.parentList.numNode++;
        }

        /**
         * Remove this Node from its parent list.
         * If the Node belongs to no parent list, an Exception will be thrown.
         */
        public void removeSelf() {
            // Security check.
            if (this.parentList == null) {
                throw new RuntimeException("Try to remove a Node from a non-existent parent list.");
            }
            // Modify pointers.
            this.next.prev = this.prev;
            this.prev.next = this.next;
            // Decrease the size counter of the list.
            this.parentList.numNode--;
            // No longer belongs to the list.
            this.parentList = null;
        }

        @Override
        public String toString() {
            return "Intrusive Node " + this.hashCode() + ":\t[" +
                    (this.prev.isDummy() ? "dummy" : this.prev.hashCode()) + " << " +
                    "{" + this.dataElem + "}" +
                    " >> " + (this.next.isDummy() ? "dummy" : this.next.hashCode())
                    + "]";
        }
    }


    /**
     * The parent object holding this intrusive list.
     */
    private P parent;

    public P getParent() {return parent;}

    /**
     * Dummy head node. (sentinel)
     */
    private final Node<T, P> head;

    /**
     * Dummy trailer node. (sentinel)
     */
    private final Node<T, P> tail;

    /**
     * Number of nodes in the intrusive list.
     */
    private int numNode = 0;

    public int size() {
        return numNode;
    }


    /**
     * Construct a intrusive list.
     * @param parent The parent object that will hold the list constructed.
     */
    public IntrusiveList(P parent) {
        this.parent = parent;
        // Create the dummy head and tail,
        head = new IntrusiveList.Node<>();
        head.parentList = this;
        tail = new Node<>();
        tail.parentList = this;
        // and initialize their links.
        head.next = tail;
        tail.prev = head;
    }


    /**
     * If the list is empty (with no data Node inside).
     * @return Yes or no.
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Get the first data Node in the list.
     * @return The first data Node. Null if the list is empty.
     */
    public Node<T, P> getFirst() {
        return this.isEmpty() ? null : head.getNext();
    }

    /**
     * Get the last data Node in the list.
     * @return The last data Node. Null if the list is empty.
     */
    public Node<T, P> getLast() {
        return this.isEmpty() ? null : tail.getPrev();
    }

    /**
     * Returns true if this list contains the specified element.
     * More formally, returns true if and only if this list contains
     * at least one node such that node.dataElem == elem.
     * Obviously, it's an O(n) search.
     * @param elem The data element to be looked up.
     * @return true if this list contains the specified element. Otherwise, return false.
     */
    public boolean contains(T elem) {
        for (Node<T, P> node : this) {
            if (node.dataElem == elem) {
                return true;
            }
        }
        return false;
    }

    /**
     * Insert the given Node at the front of the list.
     * @param node The give Node to be the new first data node of the list.
     */
    public void insertAtFront(Node<T, P> node) {
        node.insertAfter(this.head);
    }

    /**
     * Insert the given Node at the end of the list.
     * @param node The give Node to be the new last data node of the list.
     */
    public void insertAtEnd(Node<T, P> node) {
        node.insertBefore(this.tail);
    }

    public class IntrusiveListIterator implements Iterator<Node<T, P>> {
        /**
         * Dummy head.
         */
        Node<T, P> head;

        /**
         * Dummy tail.
         */
        Node<T, P> tail;

        /**
         * Pointer to the last Node been read.
         */
        Node<T, P> cur;

        IntrusiveListIterator(Node<T, P> head, Node<T, P> tail) {
            this.head = head;
            this.tail = tail;
            this.cur = head;
        }

        @Override
        public boolean hasNext() {
            return cur.next != tail;
        }

        @Override
        public Node<T, P> next() {
            this.cur = this.cur.next;
            return this.cur;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public IntrusiveListIterator iterator() {
        return new IntrusiveListIterator(head, tail);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node<T, P> node : this) {
            stringBuilder
                    .append("{").append(node.dataElem).append("}");
            if (node.getNext() != null) {
                stringBuilder.append(" <-> ");
            }
        }
        return stringBuilder.toString();
    }
}


