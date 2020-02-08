package cn.eli486.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eli
 */
public class Title<T>{
	/**
	 * 内部构造节点类
	 * 
	 * @param <T>
	 */
	private class Node<T> {
		private T data;
		private Node<T> next; // 指向下一个节点的引用
		private Node<T> prev; // 指向前一个节点的引用

		public Node(T data) {
			this.data = data;
		}
	}
	/**
	 * 模拟头结点
 	 */
	private Node<T> head;
	/**
	*模拟尾部节点
	 */
	private Node<T> last;
	/**
	 * 暂定一个临时节点，用作指针节点
 	 */
	private Node<T> other;
	private int length;

	/**
	 * 链表是否为空
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		return length == 0;
	}

	/**
	 * 普通添加，往链表尾部添加
	 * 
	 * @param data
	 */
	public void append(T data) {
		// 链表为空，新创建一个链表
		if (isEmpty()) {
			head = new Node<T>(data);
			last = head;
			length++;
		} else {
			other = new Node<T>(data);
			other.prev = last;
			// 将新的节点与原来的尾部节点进行结构上的关联
			last.next = other;
			// other将成为最后一个节点
			last = other;
			length++;
		}
	}

	/**
	 * 在指定的数据后面添加数据
	 * 
	 * @param data
	 * @param insertData
	 */
	public void addAfter(T data, T insertData) {
		other = head;
		// 我们假定这个head是不为空的。
		while (other != null) {
			if (other.data.equals(data)) {
				Node<T> t = new Node<T>(insertData);
				t.prev = other;
				// 对新插入的数据进行一个指向的定义
				t.next = other.next;
				other.next = t;

				if (t.next == null) {
					last = t;
				}
				length++;
			}
			other = other.next;
		}
	}

	/**
	 * 删除，删除指定的数据
	 * 
	 * @param data
	 */
	public void remove(T data) {
		// 我们假定这个head是不为空的。
		other = head;
		while (other != null) {
			if (other.data.equals(data)) {
				other.prev.next = other.next;
				length--;
			}
			other = other.next;
		}

	}

	/**
	 * 测试打印数据
	 */
	public void printList() {
		other = head;
		for (int i = 0; i < length; i++) {
			System.out.print(other.data + "  ");
			other = other.next;
		}
	}
	public int len() {
		return length;
	}
	
	/**
	 * 以数组的形式返回节点数据
	 */
	public Object[] show() {
		Object[] data = new String[length];
		other = head;
		for (int i = 0; i < length; i++) {
			data[i]=other.data;
			other = other.next;
		}
		return data;
	}
	
	/**
	 * 建立map
	 * 存储title的位置-表头名称
	 */
	public Map<Integer,String> local() {
		Map<Integer, String> map = new HashMap<> ();
		Object[] data = new String[length];
		other = head;
		int loc = 0;
		for (int i = 0; i < length; i++) {
			data[i]=other.data;
			map.put(i, (String) data[i]);
			other = other.next;
		}
		return map;
	}
	
	

	public static void main(String[] args) {
		Title<String> list = new Title<> ();
		list.printList();
		System.out.println("====================");
		list.addAfter("b", "d");
		list.printList();
		System.out.println("====================");
		list.remove("b");
		list.printList();
		System.out.println(list.len());
	}
}
