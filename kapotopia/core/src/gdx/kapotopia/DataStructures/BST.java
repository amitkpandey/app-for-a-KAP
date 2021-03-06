package gdx.kapotopia.DataStructures;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class BST<Key extends Comparable<Key>, Value> implements Tree<Key, Value> {
    private Node root;

    class Node {
        Key key;
        Value value;
        Node left;
        Node right;
        int n;
        boolean color;

        public Node(Key key, Value value, int n) {
            this.key = key;
            this.value = value;
            this.n = n;
            this.color = false;
        }

        Node (Key key, Value val, int n, boolean color) {
            this.key = key;
            this.value = val;
            this.n = n;
            this.color = color;
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    int size(Node x) {
        if(x == null) return 0;
        return x.n;
    }

    @Override
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) return null;

        int comp = key.compareTo(x.key);
        if( comp < 0) return get(x.left, key);
        else if (comp > 0) return get(x.right, key);
        return x.value;
    }

    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    protected Node put(Node x, Key key, Value val) {
        if(x == null) return new Node(key, val, 1);

        int comp = key.compareTo(x.key);
        if (comp < 0) x.left = put(x.left, key, val);
        else if(comp > 0) x.right = put(x.right, key, val);
        else x.value = val;
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public Key min() {
        if( size(root) == 0) throw new NoSuchElementException();
        return min(root).key;
    }

    private Node min(Node x) {
        if(x.left == null) return x;
        return min(x.left);
    }

    @Override
    public Key max() {
        if (size(root) == 0) throw new NoSuchElementException();
        return max(root).key;
    }

    private Node max(Node x) {
        if(x.right == null) return x;
        return max(x.right);
    }

    @Override
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null) throw new NoSuchElementException();
        return x.key;
    }

    private Node floor(Node x, Key key) {
        if(x == null) return null;
        int comp = key.compareTo(x.key);
        if (comp == 0) return x;
        if (comp < 0) return floor(x.left, key);
        Node t = floor(x.right, key);
        if (t != null) return t;
        return x;
    }

    @Override
    public Key select(int k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException();
        return select(root, k).key;
    }

    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k-t-1);
        return x;
    }

    @Override
    public int rank(Key key) {
        return rank(key, root);
    }

    private int rank(Key key, Node x) {
        if ( x == null) return 0;

        int comp = key.compareTo(x.key);
        if (comp < 0) return rank(key, x.left);
        else if (comp > 0) return 1 + size(x.left) + rank(key, x.right);
        return size(x.left);
    }

    @Override
    public void deleteMin() {
        if(size(root) == 0) throw new NoSuchElementException();
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public void deleteMax() {
        if (size(root) == 0) throw new NoSuchElementException();
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        if (x.right == null) return x.left;
        x.right = deleteMax(x.right);
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public void delete(Key key) {
        root = delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;
        int comp = key.compareTo(x.key);
        if (comp < 0) x.left = delete(x.left, key);
        else if (comp > 0) x.right = delete(x.right, key);
        else {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.n = size(x.left) + size(x.right) + 1;
        return x;
    }

    public boolean isBST() {
        return isBST(root, null, null);
    }

    public boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (min != null && x.key.compareTo(min) <= 0) return false;
        if (max != null && x.key.compareTo(max) >= 0) return false;
        return isBST(x.left, min, x.key)
                && isBST(x.right, x.key, max);
    }

    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> queue = new ArrayDeque<Key>();
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null) return;
        int complo = lo.compareTo(x.key);
        int comphi = hi.compareTo(x.key);
        if (complo < 0) keys(x.left, queue, lo, hi);
        if (complo <= 0 && comphi >= 0) queue.add(x.key);
        if (comphi > 0) keys(x.right, queue, lo, hi);
    }

    @NotNull
    @Override
    public Iterator<Key> iterator() {
        return keys().iterator();
    }
}
