/**
 *
 */
package com.allendowney.thinkdast;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Map using a binary search tree.
 *
 * @param <K>
 * @param <V>
 *
 */
public class MyTreeMap<K, V> implements Map<K, V> {

	private int size = 0;
	private Node root = null;

	/**
	 * Represents a node in the tree.
	 *
	 */
	protected class Node {
		public K key;
		public V value;
		public Node left = null;
		public Node right = null;

		/**
		 * @param key
		 * @param value
		 * @param left
		 * @param right
		 */
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public boolean containsKey(Object target) {
		return findNode(target) != null;
	}

	/**
	 * Returns the entry that contains the target key, or null if there is none.
	 *
	 * @param target
	 */
	private Node findNode(Object target) {
		// some implementations can handle null as a key, but not this one
		if (target == null) {
			throw new IllegalArgumentException();
		}

		// something to make the compiler happy
		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) target;
		
		Node current = root;
		
		while (current != null) {
			int comparison = k.compareTo(current.key);
			
			if (comparison < 0) {
				if (current.left == null) {
					break;
				}
				
				current = current.left;
			} 
			else if (comparison > 0) {
				if (current.right == null) {
					break;
				}
				
				current = current.right;
			}
			else {
				return current;
			}
		}

		return null;
	}

	/**
	 * Compares two keys or two values, handling null correctly.
	 *
	 * @param target
	 * @param obj
	 * @return
	 */
	private boolean equals(Object target, Object obj) {
		if (target == null) {
			return obj == null;
		}
		return target.equals(obj);
	}

	@Override
	public boolean containsValue(Object target) {
		return containsValueHelper(root, target);
	}

	private boolean containsValueHelper(Node node, Object target) {
		Node current = node;
		List<Node> nodesToCheck = new LinkedList<Node>();
		nodesToCheck.add(current);
		
		while (!nodesToCheck.isEmpty()) {
			if (current.left != null) {
				nodesToCheck.add(nodesToCheck.size(), current.left);
			}
			
			if (current.right != null) {
				nodesToCheck.add(nodesToCheck.size(), current.right);
			}
			
			if (equals(target, current.value)) {
				return true;
			}
			
			current = nodesToCheck.remove(0);
		}
		
		return false;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object key) {
		Node node = findNode(key);
		if (node == null) {
			return null;
		}
		return node.value;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<K> keySet() {
		Set<K> set = new LinkedHashSet<K>();
		keySetHelper(root, set);
		return set;
	}
	
	private void keySetHelper(Node node, Set<K> set) {
		if (node == null) return;
		
		if (node.left != null) {
			keySetHelper(node.left, set);
		}
		
		set.add(node.key);
		
		if (node.right != null) {
			keySetHelper(node.right, set);
		}
	}

	@Override
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		if (root == null) {
			root = new Node(key, value);
			size++;
			return null;
		}
		return putHelper(root, key, value);
	}

	private V putHelper(Node node, K key, V value) {
		Node current = node;
		
		while (current != null) {
			@SuppressWarnings("unchecked")
			Comparable<K> k = (Comparable<K>) key;
			
			int comparison = k.compareTo(current.key);
						
			if (comparison < 0) {
				if (current.left == null) {
					current.left = new Node(key, value);
					size++;
					return null;
				}
				else {
					current = current.left;
				}
			}
			else if (comparison > 0) {
				if (current.right == null) {
					current.right = new Node(key, value);
					size++;
					return null;
				}
				else {
					current = current.right;
				}
			}
			else {
				V previousValue = current.value;
				current.value = value;
				return previousValue;
			}

		}
		
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry: map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		Node current = root;
		Node nodeToDelete = null;
		Node parent = null;
		
		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) key;
		
		while (current != null) {
			int comparison = k.compareTo(current.key);
			
			if (comparison < 0) {
				parent = current;
				current = current.left;
			}
			else if (comparison > 0) {
				parent = current;
				current = current.right;
			} 
			else {
				nodeToDelete = current;
				break;
			}
		}
		
		if (nodeToDelete == null) {
			return null;
		}
		
		V previousValue = null;
		
		// Has no children
		if (nodeToDelete.left == null && nodeToDelete.right == null) {
			if (parent.left == nodeToDelete) {
				parent.left = null;
			}
			else {
				parent.right = null;
			}
			
			previousValue = nodeToDelete.value;
		}
		// Has one child
		else if (nodeToDelete.left == null || nodeToDelete.right == null) {
			if (nodeToDelete.left == null) {
				if (parent.left == nodeToDelete) {
					parent.left = nodeToDelete.right;
				}
				else {
					parent.right = nodeToDelete.right;
				}
			}
			else if (nodeToDelete.right == null) {
				if (parent.left == nodeToDelete) {
					parent.left = nodeToDelete.left;
				}
				else {
					parent.right = nodeToDelete.left;
				}
			}
			
			previousValue = nodeToDelete.value;
		}
		// Has two children
		else {
			Node successor = nodeToDelete.right;
			
			while (successor.left != null) {
				Node newSuccessor = successor.left;
				
				if (newSuccessor.left == null) {
					successor.left = null;
				}
				
				successor = newSuccessor;
			}
			
			previousValue = nodeToDelete.value;
			
			nodeToDelete.key = successor.key;
			nodeToDelete.value = successor.value;
		}
		
		size--;
		return previousValue;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Collection<V> values() {
		Set<V> set = new HashSet<V>();
		Deque<Node> stack = new LinkedList<Node>();
		stack.push(root);
		while (!stack.isEmpty()) {
			Node node = stack.pop();
			if (node == null) continue;
			set.add(node.value);
			stack.push(node.left);
			stack.push(node.right);
		}
		return set;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Integer> map = new MyTreeMap<String, Integer>();
		map.put("Word1", 1);
		map.put("Word2", 2);
		Integer value = map.get("Word1");
		System.out.println(value);

		for (String key: map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
	}

	/**
	 * Makes a node.
	 *
	 * This is only here for testing purposes.  Should not be used otherwise.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public MyTreeMap<K, V>.Node makeNode(K key, V value) {
		return new Node(key, value);
	}

	/**
	 * Sets the instance variables.
	 *
	 * This is only here for testing purposes.  Should not be used otherwise.
	 *
	 * @param node
	 * @param size
	 */
	public void setTree(Node node, int size ) {
		this.root = node;
		this.size = size;
	}

	/**
	 * Returns the height of the tree.
	 *
	 * This is only here for testing purposes.  Should not be used otherwise.
	 *
	 * @return
	 */
	public int height() {
		return heightHelper(root);
	}

	private int heightHelper(Node node) {
		if (node == null) {
			return 0;
		}
		int left = heightHelper(node.left);
		int right = heightHelper(node.right);
		return Math.max(left, right) + 1;
	}
}
