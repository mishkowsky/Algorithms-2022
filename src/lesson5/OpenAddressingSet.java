package lesson5;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class OpenAddressingSet<T> extends AbstractSet<T> {

    private final int bits;

    private final int capacity;

    private final Object[] storage;

    private int size = 0;

    private final static Object EMPTY = new Object();

    private int startingIndex(Object element) {
        return element.hashCode() & (0x7FFFFFFF >> (31 - bits));
    }

    public OpenAddressingSet(int bits) {
        if (bits < 2 || bits > 31) {
            throw new IllegalArgumentException();
        }
        this.bits = bits;
        capacity = 1 << bits;
        storage = new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    @Override
    public boolean contains(Object o) {
        int startingIndex = startingIndex(o);
        int index = startingIndex;
        Object current = storage[index];
        while (current != null) {
            if (current.equals(o)) {
                return true;
            }
            index = (index + 1) % capacity;
            if (index == startingIndex) {
                return false;
            }
            current = storage[index];
        }
        return false;
    }

    /**
     * Добавление элемента в таблицу.
     * <p>
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     * <p>
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    @Override
    public boolean add(T t) {
        int startingIndex = startingIndex(t);
        int index = startingIndex;
        Object current = storage[index];
        while (current != null && current != EMPTY) {
            if (current.equals(t)) {
                return false;
            }
            index = (index + 1) % capacity;
            if (index == startingIndex) {
                throw new IllegalStateException("Table is full");
            }
            current = storage[index];
        }
        storage[index] = t;
        size++;
        return true;
    }

    /**
     * Удаление элемента из таблицы
     * <p>
     * Если элемент есть в таблице, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     * <p>
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     * <p>
     * Средняя
     */

    // Быстродействие: Худшее: O(n) все n элементов имеют одинаковые bits младших битов и удаляется последний
    //                         добавленный элемент.
    //                 Лучшее: O(1) все n элементов имеют различные bits младших битов.
    // Ресурсоемкость: O(1)
    @Override
    public boolean remove(Object o) {
        int startingIndex = startingIndex(o);
        int index = startingIndex;
        Object current = storage[index];
        while (current != null) {
            if (current.equals(o)) {
                storage[index] = EMPTY;
                size--;
                return true;
            }
            index = (index + 1) % capacity;
            if (index == startingIndex) return false;
            current = storage[index];
        }
        return false;
    }

    /**
     * Создание итератора для обхода таблицы
     * <p>
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     * <p>
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     * <p>
     * Средняя (сложная, если поддержан и remove тоже)
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new OpenAddressingSetIterator(this);
    }

    public class OpenAddressingSetIterator implements Iterator<T> {

        OpenAddressingSet<T> set;
        int index = 0;
        int lastIndex;
        T next;
        T last;

        // Быстродействие: O(n)
        // Ресурсоемкость: O(1)
        private OpenAddressingSetIterator(OpenAddressingSet<T> set) {
            this.set = set;
            next = (T) set.storage[index];
            while ((next == null || next == EMPTY) && index < set.storage.length - 1) next = (T) set.storage[++index];
        }

        // Быстродействие: O(1)
        // Ресурсоемкость: O(1)
        @Override
        public boolean hasNext() {
            return next != null && index < set.storage.length;
        }

        // Быстродействие: O(n)
        // Ресурсоемкость: O(1)
        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            last = next;
            lastIndex = index;
            next = null;
            while ((next == null || next == EMPTY) && index < set.storage.length - 1) next = (T) set.storage[++index];
            if (next == EMPTY) next = null;
            return last;
        }

        // Быстродействие: O(1)
        // Ресурсоемкость: O(1)
        @Override
        public void remove() {
            if (last == null) throw new IllegalStateException();
            set.storage[lastIndex] = EMPTY;
            size--;
            last = null;
        }
    }
}
