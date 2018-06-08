public class MyHashSet implements MyStructure {
    private MyLinkedList[] content;
    private int count;

    public MyHashSet() {
        count = 100;
        content = new MyLinkedList[count];
    }

    public MyHashSet(int initialCapacity) {
        count = initialCapacity;
        content = new MyLinkedList[count];
    }

    public boolean isEmpty() {
        for (MyLinkedList list: content) {
            if (list != null && !list.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean needRehash() {
        int amount = 0; //количество списков, в которых два и больше элементов
        for (MyLinkedList list: content) {
            if (list != null && list.size() >= 2) {
                amount++;
            }
        }
        return amount / count >= 0.75;
    }

    private void rehash() {
        int newCount = count * 2;
        MyLinkedList[] newContent = new MyLinkedList[newCount];
        for (MyLinkedList list: content) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Object element = list.get(i);
                    int newIndex = element.hashCode() % newCount;
                    if (newContent[newIndex] == null) {
                        newContent[newIndex] = new MyLinkedList();
                    }
                    newContent[newIndex].add(element);
                }
            }
        }
        content = newContent;
        count = newCount;
    }

    public void add(Object o) {
        if (needRehash()) {
            rehash();
        }
        int index = o.hashCode() % count;
        if (content[index] == null) {
            content[index] = new MyLinkedList();
        }
        if (!content[index].contains(o)) {
            content[index].add(o);
        }
    }

    public boolean contains(Object o) {
        int index = o.hashCode() % count;
        return content[index] != null && content[index].contains(o);
    }

    public void remove(Object o) throws ElementDoesNotExistException {
        int index = o.hashCode() % count;
        MyLinkedList list = content[index];
        if (list != null && list.contains(o)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(o)) {
                    list.remove(i);
                }
            }
        } else {
            throw new ElementDoesNotExistException();
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (MyLinkedList list: content) {
            if (list != null && !list.isEmpty()) {
                hashCode += list.hashCode();
            }
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MyHashSet set = (MyHashSet) obj;
        return hashCode() == set.hashCode();
    }

    @Override
    public String toString() {
        String s = "{";
        for (MyLinkedList list: content) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    s += list.get(i) + ", ";
                }
            }
        }
        if (!isEmpty()) {
            s = s.substring(0, s.length() - 2); //удаление ненужной последней запятой
        }
        s += "}";
        return s;
    }

    @Override
    public Object get(int index) {
        return null;
    }
}
