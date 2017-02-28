package com.yuwnloy.stardb.test;

import org.iq80.leveldb.impl.InternalEntry;
import org.iq80.leveldb.impl.InternalKeyComparator;
import org.iq80.leveldb.impl.MemTable;
import org.iq80.leveldb.impl.MemTable.MemTableIterator;
import org.iq80.leveldb.impl.ValueType;
import org.iq80.leveldb.table.BytewiseComparator;
import org.iq80.leveldb.table.UserComparator;
import org.iq80.leveldb.util.Slice;

public class MemTableTest {

	public static void main(String[] args) {
		UserComparator userComparator = new BytewiseComparator();
		InternalKeyComparator internalKeyComparator = new InternalKeyComparator(userComparator);
		
		MemTable table = new MemTable(internalKeyComparator);
		//public void add(long sequenceNumber, ValueType valueType, Slice key, Slice value)
		table.add(1, ValueType.VALUE, new Slice("aaa".getBytes()), new Slice("aaa-value".getBytes()));
		table.add(2, ValueType.VALUE, new Slice("bbb".getBytes()), new Slice("bbb-value".getBytes()));
		table.add(3, ValueType.VALUE, new Slice("ccc".getBytes()), new Slice("ccc-value".getBytes()));
		table.add(4, ValueType.VALUE, new Slice("ddd".getBytes()), new Slice("ddd-value".getBytes()));
		table.add(5, ValueType.VALUE, new Slice("eee".getBytes()), new Slice("eee-value".getBytes()));
		
		MemTableIterator iterator = table.iterator();
		while(iterator.hasNext()){
			InternalEntry entry = iterator.next();
			printEntry(entry);
		}
		
		iterator = table.iterator();
		System.out.println("***********next********************");
		System.out.println(iterator.next());
		System.out.println(iterator.next());
		System.out.println("***********prev********************");
		System.out.println(iterator.prev());
		System.out.println("***********next********************");
		System.out.println(iterator.next());
		System.out.println(iterator.next());
	}
	
	private static void printEntry(InternalEntry entry){
		System.out.println(entry.getKey().toString()+","+new String(entry.getValue().getBytes()));
	}
}
