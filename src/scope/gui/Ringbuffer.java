package scope.gui;

import java.util.*;

public class Ringbuffer<E> extends AbstractList<E> implements Iterator<E>, RandomAccess{
	private int bufferLength = 0;
	private int capacity = 0;
	private int size = 0;
	private int readPointer = 0;
	private int writePointer = 1;
	private List<E> buffer;
//	private Ringbuffer<E> readBuffer = new Ringbuffer<E>(60);

	public Ringbuffer(int capacity){
		this.capacity = capacity;
		this.bufferLength = capacity+1;
		this.buffer = new ArrayList<E>(Collections.nCopies(bufferLength, (E)null));
	}
	
	private int circShiftIndex(int index){
		return index % bufferLength;
	}
	
	private synchronized void setReadPointer(int index){
		this.readPointer = index;
	}
	
	private synchronized int getReadPointer(){
		return this.readPointer;
	}
	
	private synchronized void incReadPointer(){
		if (circShiftIndex(readPointer+1) == circShiftIndex(writePointer)){
			
		}
	}
	//writePointer writes first, than moves
	public void addItem(E e){
		if(circShiftIndex(writePointer+1) == getReadPointer()){
			setReadPointer(circShiftIndex(getReadPointer()+1));
			buffer.set(writePointer, e);
			writePointer = circShiftIndex(writePointer+1);
			//TODO inc/dec size capazity
		}else{
			buffer.set(writePointer, e);
			writePointer = circShiftIndex(writePointer+1);
		}
	}
	
	//readPointer moves first, than reads
	public E getItem() {
		if (circShiftIndex(getReadPointer()+1) != writePointer) {
			setReadPointer(circShiftIndex(getReadPointer()+1));
			return buffer.get(getReadPointer());
		} else {
			return null;
		}
	}
	
	@Override
	public E set(int i, E e){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E get(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(int index, E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasNext() {
		return size > 0;
	}
	
	@Override
	public E next() {
		if ((size > 0) & (circShiftIndex(readPointer+1) != circShiftIndex(writePointer))){
			return buffer.get(circShiftIndex(readPointer++));
		}else{
			return null;
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		return size;
	}
	
	@Override
	public String toString(){
		StringBuilder strBuild = new StringBuilder();
		strBuild.append("[");
		for (int i = 0; i <= (buffer.size()-1); i++){
			if (i == writePointer) strBuild.append("w>");
			else if (i == readPointer) strBuild.append("r>");
			else strBuild.append("  ");
			strBuild.append(buffer.get(i)+" ");
		}
		strBuild.append("]");
		return strBuild.toString();		
	}
}
