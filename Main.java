//package mitm304;
//// Java Program to Implement B+ Tree
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//// B+ Tree Node class to represent
//// internal and leaf nodes
//class BPlusTreeNode {
//    // True for leaf nodes, False for internal nodes
//    boolean isLeaf; 
//
//    // The keys stored in this node
//    List<Integer> keys; 
//
//    // Children nodes (for internal nodes)
//    List<BPlusTreeNode> children; 
//
//    // Link to the next leaf node
//    BPlusTreeNode next; 
//
//    // Constructor to initialize a node
//    public BPlusTreeNode(boolean isLeaf) {
//        this.isLeaf = isLeaf;
//        this.keys = new ArrayList<>();
//        this.children = new ArrayList<>();
//        this.next = null;
//    }
//}
//
//// B+ Tree class with basic operations: insert and search
//class BPlusTree {
//    // Root node of the tree
//    private BPlusTreeNode root;
//  
//    // Maximum number of keys per node
//    private final int order; 
//
//    // Constructor to initialize the B+ Tree
//    public BPlusTree(int order) {
//        if (order < 3) {
//            throw new IllegalArgumentException("Order must be at least 3");
//        }
//        this.root = new BPlusTreeNode(true);
//        this.order = order;
//    }
//
//    // Find the appropriate leaf node for insertion
//    private BPlusTreeNode findLeaf(int key) {
//        BPlusTreeNode node = root;
//        while (!node.isLeaf) {
//            int i = 0;
//            while (i < node.keys.size() && key >= node.keys.get(i)) {
//                i++;
//            }
//            node = node.children.get(i);
//        }
//        return node;
//    }
//
//    // Insert a key into the B+ Tree
//    public void insert(int key) {
//        BPlusTreeNode leaf = findLeaf(key);
//        insertIntoLeaf(leaf, key);
//
//        // Split the leaf node if it exceeds the order
//        if (leaf.keys.size() > order - 1) {
//            splitLeaf(leaf);
//        }
//    }
//
//    // Insert into the leaf node
//    private void insertIntoLeaf(BPlusTreeNode leaf, int key) {
//        int pos = Collections.binarySearch(leaf.keys, key);
//        if (pos < 0) {
//            pos = -(pos + 1);
//        }
//        leaf.keys.add(pos, key);
//    }
//
//    // Split a leaf node and update parent nodes
//    private void splitLeaf(BPlusTreeNode leaf) {
//        int mid = (order + 1) / 2;
//        BPlusTreeNode newLeaf = new BPlusTreeNode(true);
//
//        // Move half the keys to the new leaf node
//        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
//        leaf.keys.subList(mid, leaf.keys.size()).clear();
//
//        newLeaf.next = leaf.next;
//        leaf.next = newLeaf;
//
//        // If the root splits, create a new root
//        if (leaf == root) {
//            BPlusTreeNode newRoot = new BPlusTreeNode(false);
//            newRoot.keys.add(newLeaf.keys.get(0));
//            newRoot.children.add(leaf);
//            newRoot.children.add(newLeaf);
//            root = newRoot;
//        } else {
//            insertIntoParent(leaf, newLeaf, newLeaf.keys.get(0));
//        }
//    }
//
//    // Insert into the parent node after a leaf split
//    private void insertIntoParent(BPlusTreeNode left, BPlusTreeNode right, int key) {
//        BPlusTreeNode parent = findParent(root, left);
//
//        if (parent == null) {
//            throw new RuntimeException("Parent node not found for insertion");
//        }
//
//        int pos = Collections.binarySearch(parent.keys, key);
//        if (pos < 0) {
//            pos = -(pos + 1);
//        }
//
//        parent.keys.add(pos, key);
//        parent.children.add(pos + 1, right);
//
//        // Split the internal node if it exceeds the order
//        if (parent.keys.size() > order - 1) {
//            splitInternal(parent);
//        }
//    }
//
//    // Split an internal node
//    private void splitInternal(BPlusTreeNode internal) {
//        int mid = (order + 1) / 2;
//        BPlusTreeNode newInternal = new BPlusTreeNode(false);
//
//        // Move half the keys to the new internal node
//        newInternal.keys.addAll(internal.keys.subList(mid + 1, internal.keys.size()));
//        internal.keys.subList(mid, internal.keys.size()).clear();
//
//        // Move half the children to the new internal node
//        newInternal.children.addAll(internal.children.subList(mid + 1, internal.children.size()));
//        internal.children.subList(mid + 1, internal.children.size()).clear();
//
//        // If the root splits, create a new root
//        if (internal == root) {
//            BPlusTreeNode newRoot = new BPlusTreeNode(false);
//            newRoot.keys.add(internal.keys.get(mid));
//            newRoot.children.add(internal);
//            newRoot.children.add(newInternal);
//            root = newRoot;
//        } else {
//            insertIntoParent(internal, newInternal, internal.keys.remove(mid));
//        }
//    }
//
//    // Find the parent node of a given node
//    private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode target) {
//        if (current.isLeaf || current.children.isEmpty()) {
//            return null;
//        }
//
//        for (int i = 0; i < current.children.size(); i++) {
//            BPlusTreeNode child = current.children.get(i);
//
//            if (child == target) {
//                // Parent found
//                return current; 
//            }
//
//            BPlusTreeNode possibleParent = findParent(child, target);
//            if (possibleParent != null) {
//                return possibleParent;
//            }
//        }
//
//        // Parent not found
//        return null; 
//    }
//
//    // Search for a key in the B+ Tree
//    public boolean search(int key) {
//        BPlusTreeNode node = findLeaf(key);
//        int pos = Collections.binarySearch(node.keys, key);
//        return pos >= 0;
//    }
//
//    // Display the Tree (for debugging purposes)
//    public void printTree() {
//        printNode(root, 0);
//    }
//
//    private void printNode(BPlusTreeNode node, int level) {
//        System.out.println("Level " + level + ": " + node.keys);
//        if (!node.isLeaf) {
//            for (BPlusTreeNode child : node.children) {
//                printNode(child, level + 1);
//            }
//        }
//    }
//}
//
//// Main class with example usage
//public class Main {
//    public static void main(String[] args) {
//        BPlusTree tree = new BPlusTree(3);
//
//        // Insert keys into the B+ Tree
//        tree.insert(10);
//        tree.insert(20);
//        tree.insert(30);
//        tree.insert(40);
//        tree.insert(50);
//        tree.insert(60);
//
//        // Print the B+ Tree structure
//        System.out.println("Tree after insertion:");
//        tree.printTree();
//
//        // Search for keys in the B+ Tree
//        System.out.println("Search for 30: " + tree.search(30));  // True
//        System.out.println("Search for 25: " + tree.search(25));  // False
//    }
//}
