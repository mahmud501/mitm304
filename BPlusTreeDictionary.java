package mitm304;

import java.io.*;
import java.util.*;

class BPlusTreeNode {
	boolean isLeaf;
	List<String> keys;
	List<String> values;
	List<BPlusTreeNode> children;
	BPlusTreeNode next;

	public BPlusTreeNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		this.keys = new ArrayList<>();
		this.values = new ArrayList<>();
		this.children = new ArrayList<>();
		this.next = null;
	}
}

class BPlusTree {
	private int order;
	private BPlusTreeNode root;

	public BPlusTree(int order) {
		this.order = order;
		this.root = new BPlusTreeNode(true);
	}

	private BPlusTreeNode findLeafNode(String key) {
		BPlusTreeNode current = root;
		while (!current.isLeaf) {
			int i = 0;
			while (i < current.keys.size() && key.compareTo(current.keys.get(i)) > 0) {
				i++;
			}
			current = current.children.get(i);
		}
		return current;
	}

	private void splitChild(BPlusTreeNode parent, int index) {
	    BPlusTreeNode nodeToSplit = parent.children.get(index);
	    int midIndex = order / 2;

	    // Create a new node for the split
	    BPlusTreeNode newNode = new BPlusTreeNode(nodeToSplit.isLeaf);
	    newNode.keys = new ArrayList<>(nodeToSplit.keys.subList(midIndex, nodeToSplit.keys.size()));
	    
	    if (nodeToSplit.isLeaf) {
	        newNode.values = new ArrayList<>(nodeToSplit.values.subList(midIndex, nodeToSplit.values.size()));
	        newNode.next = nodeToSplit.next;
	        nodeToSplit.next = newNode;
	    } else {
	        newNode.children = new ArrayList<>(nodeToSplit.children.subList(midIndex + 1, nodeToSplit.children.size()));
	    }

	    nodeToSplit.keys = new ArrayList<>(nodeToSplit.keys.subList(0, midIndex));
	    if (nodeToSplit.isLeaf) {
	        nodeToSplit.values = new ArrayList<>(nodeToSplit.values.subList(0, midIndex));
	    } else {
	        nodeToSplit.children = new ArrayList<>(nodeToSplit.children.subList(0, midIndex + 1));
	    }

	    String promoteKey = nodeToSplit.isLeaf ? newNode.keys.get(0) : nodeToSplit.keys.get(midIndex);
	    parent.keys.add(index, promoteKey);
	    parent.children.add(index + 1, newNode);
	}

	private void insertInternal(String key, String value) {
		BPlusTreeNode leaf = findLeafNode(key);

		int pos = Collections.binarySearch(leaf.keys, key);
		if (pos < 0)
			pos = -pos - 1;
		leaf.keys.add(pos, key);
		leaf.values.add(pos, value);

		if (leaf.keys.size() >= order) {
			if (leaf == root) {
				BPlusTreeNode newRoot = new BPlusTreeNode(false);
				newRoot.children.add(root);
				splitChild(newRoot, 0);
				root = newRoot;
			} else {
				BPlusTreeNode parent = findParent(root, leaf);
				int index = findParentChildIndex(parent, leaf);
				splitChild(parent, index);
			}
		}
	}

	private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode child) {
		if (current.isLeaf || current.children.isEmpty())
			return null;

		for (int i = 0; i < current.children.size(); i++) {
			if (current.children.get(i) == child)
				return current;
			BPlusTreeNode parent = findParent(current.children.get(i), child);
			if (parent != null)
				return parent;
		}
		return null;
	}

	private int findParentChildIndex(BPlusTreeNode parent, BPlusTreeNode child) {
		for (int i = 0; i < parent.children.size(); i++) {
			if (parent.children.get(i) == child)
				return i;
		}
		return -1;
	}

	public void insert(String key, String value) {
		insertInternal(key, value);
	}

	public String search(String key) {
		BPlusTreeNode leaf = findLeafNode(key);
		int index = Collections.binarySearch(leaf.keys, key);
		if (index >= 0) {
			return leaf.values.get(index);
		}
		return "Word not found";
	}

	public void display() {
		displayNode(root);
	}

	private void displayNode(BPlusTreeNode node) {
		if (node == null)
			return;

		for (String key : node.keys) {
			System.out.print(key + " ");
		}
		System.out.println();

		if (!node.isLeaf) {
			for (BPlusTreeNode child : node.children) {
				displayNode(child);
			}
		}
	}
}

public class BPlusTreeDictionary {
	public static void loadDictionary(String filePath, BPlusTree tree) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				int delimiterPos = line.indexOf(',');
				if (delimiterPos == -1)
					continue;

				String word = line.substring(0, delimiterPos);
				String meaning = line.substring(delimiterPos + 1);
				tree.insert(word, meaning);
			}
		} catch (IOException e) {
			System.err.println("Failed to open file: " + filePath);
		}
	}

	public static void main(String[] args) {
	    BPlusTree tree = new BPlusTree(4);
	    loadDictionary(
	            "E:\\Study\\DU MIT\\1st Sem\\MITM 304\\Assignment\\Dictionary-BPlusTree-main\\EnglishDictionary.csv",
	            tree);
	    System.out.println("Dictionary loaded. Testing search:");

	    try (Scanner scanner = new Scanner(System.in)) {
	        while (true) {
	            System.out.print("Enter a word to search: ");
	            String word = scanner.nextLine();

	            String meaning = tree.search(word);
	            System.out.println("Meaning: " + meaning);
	        }
	    } catch (Exception e) {
	        System.err.println("Error reading input: " + e.getMessage());
	    }
	}
}
