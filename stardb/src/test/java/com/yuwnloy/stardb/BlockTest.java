package com.yuwnloy.stardb;

import static org.iq80.leveldb.util.SizeOf.SIZE_OF_INT;

import java.util.ArrayList;
import java.util.List;

import org.iq80.leveldb.impl.InternalKeyComparator;
import org.iq80.leveldb.impl.InternalUserComparator;
import org.iq80.leveldb.table.Block;
import org.iq80.leveldb.table.BlockEntry;
import org.iq80.leveldb.table.BlockIterator;
import org.iq80.leveldb.table.BytewiseComparator;
import org.iq80.leveldb.table.UserComparator;
import org.iq80.leveldb.util.Slice;
import org.iq80.leveldb.util.SliceInput;
import org.iq80.leveldb.util.VariableLengthQuantity;

import com.yuwnloy.stardb.utils.ByteUtil;

public class BlockTest {

	public static void main(String[] args) {
		UserComparator userComparator = new BytewiseComparator();
		InternalKeyComparator internalKeyComparator = new InternalKeyComparator(userComparator);
//		public Block(Slice block, Comparator<Slice> comparator)
		byte[] bytes = createDataSlice();
		Slice slice = new Slice(bytes);
		Block block = new Block(slice, new InternalUserComparator(internalKeyComparator));
		
		
		BlockIterator it = block.iterator();
		it.seek(new Slice("bbbbbbbb".getBytes()));
		//it.seekToRestartPosition(1);
		int co = 3;
		while(it.hasNext()&&co>0){
			BlockEntry entry = it.next();
			System.out.println(new String(entry.getKey().getBytes())+","+new String(entry.getValue().getBytes()));
			co --;
		}
		
		System.out.println("****************");
		while(it.hasPrev()){
			BlockEntry entry = it.prev();
			System.out.println(new String(entry.getKey().getBytes())+","+new String(entry.getValue().getBytes()));
		}
	}
	/**
	 * structure : {data}{positions}{count} , positions is the bytes of int array,  count is int.
	 * each data item : {sharedKeyLength}{nonSharedKeyLength}{valueLength}{noSharedKey data}{value data}  
	 * @return
	 */
	private static byte[] createDataSlice(){
		byte[] bytes = null;
		List<String> list = new ArrayList<String>();
		list.add("aaaaaaaa");
		list.add("bbbbbbbb");
		list.add("cccccccc");
		list.add("dddddddd");
		list.add("eeeeeeee");
		int count = list.size();
		byte[] data = null;
		byte[] positions = null;
		int pos = 0;
		for(String keyStr : list){
			positions = ByteUtil.append(positions, ByteUtil.int2Bytes(pos));
			data = ByteUtil.append(data, ByteUtil.int2Bytes(0));
			data = ByteUtil.append(data, keyStr.getBytes().length);
			data = ByteUtil.append(data, (keyStr+"-value").getBytes().length);
			data = ByteUtil.append(data, keyStr.getBytes());
			data = ByteUtil.append(data, (keyStr+"-value").getBytes());
			pos = data.length;
		}
		bytes = ByteUtil.ByteArrayMerge(data, positions);
		bytes = ByteUtil.append(bytes, ByteUtil.int2Bytes(count));
		return bytes;
	}
}
