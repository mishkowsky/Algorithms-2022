package lesson4;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Префиксное дерево для строк
 */
public class Trie extends AbstractSet<String> implements Set<String> {

    private static class Node {
        Character value;
        Node parent;
        NavigableMap<Character, Node> children = new TreeMap<>();
    }

    private final Node root = new Node();

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.children.clear();
        size = 0;
    }

    private String withZero(String initial) {
        return initial + (char) 0;
    }

    // Быстродействие: O(log(m) * n), где m - число элементов children, n - длина входной строки element
    // Ресурсоемкость: O(1)
    @Nullable
    private Node findNode(String element) {
        Node current = root;
        for (char character : element.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(character);
        }
        return current;
    }

    @Override
    public boolean contains(Object o) {
        String element = (String) o;
        return findNode(withZero(element)) != null;
    }

    @Override
    public boolean add(String element) {
        Node current = root;
        boolean modified = false;
        for (char character : withZero(element).toCharArray()) {
            Node child = current.children.get(character);
            if (child != null) {
                current = child;
            } else {
                modified = true;
                Node newChild = new Node();
                newChild.value = character;
                newChild.parent = current;
                current.children.put(character, newChild);
                current = newChild;
            }
        }
        if (modified) {
            size++;
        }
        return modified;
    }

    // Быстродействие: O(findNode) = O(log(m) * n)
    // Ресурсоемкость: O(1)
    @Override
    public boolean remove(Object o) {
        String element = (String) o;
        Node current = findNode(element);
        if (current == null) return false;
        if (current.children.remove((char) 0) != null) {
            size--;
            return true;
        }
        return false;
    }


    private String getValueByNode(Node node) {
        StringBuilder s = new StringBuilder();
        node = node.parent; // skip (char) 0
        while (node.parent != null) {
            s.append(node.value);
            node = node.parent;
        }
        return s.reverse().toString();
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new TrieIterator(this);
    }

    public class TrieIterator implements Iterator<String> {

        private final Trie trie;
        private Node nextNode;
        private String last;

        // Быстродействие: O(noTerminatorBelow) + O(getNewNext)
        // Ресурсоемкость: O(1)
        private TrieIterator(Trie trie) {
            this.trie = trie;
            nextNode = root;
            if (root.children.isEmpty()) {
                nextNode = null;
                return;
            }
            if (noTerminatorBelow()) getNewNext();
        }

        // Быстродействие: O(1)
        // Ресурсоемкость: O(1)
        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public String next() {
            if (!hasNext()) throw new NoSuchElementException();
            last = trie.getValueByNode(nextNode);
            getNewNext();
            return last;
        }

        // Быстродействие: Худшее: Зависит от рекурсивных вызовов (когда в конце ветки был удален терминирующий узел
        //                         методом Trie.remove()).
        //                         O(Кол-во удаленных элементов * Среднее)
        //                 Среднее: O(log(n) + m * log(h)) + O(noTerminatorBelow), где
        //                          m - количество узлов на пути выхода из текущей ветки,
        //                          n - количество братьев,
        //                          log(h) - поиск последнего ключа на k-ом уровне из m среди h элементов множества
        //                          братьев nextNode.parent.children.
        // Ресурсоемкость: O(1)
        public void getNewNext() {

            // если nextNode == root, значит все children у root'a уже были перебраны
            if (nextNode == root) {
                nextNode = null;
                return;
            }
            char previousChar = nextNode.value;

            // Если есть братья, которых еще не перебрали
            if (previousChar != nextNode.parent.children.lastKey()) { // O(log(h))
                // Получаем следующий узел
                nextNode = nextNode.parent.children.ceilingEntry((char) (previousChar + 1)).getValue(); // O(log(n))
                if (noTerminatorBelow()) getNewNext();
            }
            // Иначе идем вверх
            else {
                nextNode = nextNode.parent;
                getNewNext();
            }
        }

        // Спуск вниз по самой левой ветке, возвращаемое значение - нижний узел == терминирующий
        // Быстродействие: O(n * log(m)) - получение firstEntry() на каждом n-ом уровне,
        //                 m - число элементов в children на k-ом уровне из n, n - число узлов в левой ветке.
        // Ресурсоемкость: O(1)
        private boolean noTerminatorBelow() {
            while (!nextNode.children.isEmpty()) {
                nextNode = nextNode.children.firstEntry().getValue();
                if (nextNode.value == (char) 0) return false;
            }
            return true;
        }

        // Быстродействие: O(remove) = O(log(m) * n)
        // Ресурсоемкость: O(1)
        @Override
        public void remove() {
            if (last == null) throw new IllegalStateException();
            trie.remove(last);
            last = null;
        }
    }
}