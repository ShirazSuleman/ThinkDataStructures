/**
 *
 */
package com.allendowney.thinkdast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a HashMap using a collection of MyLinearMap and
 * resizing when there are too many entries.
 *
 * @author downey
 * @param <K>
 * @param <V>
 *
 */
public class MyHashMap<K, V> extends MyBetterMap<K, V> implements Map<K, V> {

	// average number of entries per map before we rehash
	protected static final double FACTOR = 1.0;
	
	private int size = 0;
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public V put(K key, V value) {
		MyLinearMap<K, V> map = chooseMap(key);
		size -= map.size();
		V oldValue = map.put(key, value);
		size += map.size();
		
		if (size() > maps.size() * FACTOR) {
			size = 0;
			rehash();
		}
		
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry: map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public V remove(Object key) {
		MyLinearMap<K, V> map = super.chooseMap(key);
		size -= map.size();
		V value = map.remove(key);
		size += map.size();
		return value;
	}
	
	@Override
	public void clear() {
		super.clear();
		size = 0;
	}


	/**
	 * Doubles the number of maps and rehashes the existing entries.
	 */
	/**
	 *
	 */
	protected void rehash() {
		List<Map.Entry<K, V>> entries =  new ArrayList<Map.Entry<K, V>>();
		for (MyLinearMap<K, V> map: maps) {
			entries.addAll(map.getEntries());
		}
		
		makeMaps(maps.size() * 2);
		
		for (Map.Entry<K, V> entry: entries) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Integer> map = new MyHashMap<String, Integer>();
		for (int i=0; i<10; i++) {
			map.put(new Integer(i).toString(), i);
		}
		Integer value = map.get("3");
		System.out.println(value);
	}
}
