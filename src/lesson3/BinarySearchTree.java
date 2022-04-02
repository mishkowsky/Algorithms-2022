package lesson3;

import java.util.*;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    // Быстродействие: худшее - O(n) (все узлы в одной левой ветке), среднее - O(log(n)), n - число узлов
    // Ресурсоемкость: O(1)
    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     * <p>
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * <p>
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     * <p>
     * Пример
     */
    @Override
    public boolean add(T t) {

        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
            newNode.parent = closest;
        } else {
            assert closest.right == null;
            closest.right = newNode;
            newNode.parent = closest;
        }
        size++;
        return true;
    }

    /**
     * Удаление элемента из дерева
     * <p>
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     * <p>
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     * <p>
     * Средняя
     */

    // Быстродействие: худшее - O(n) (все узлы в одной левой ветке), среднее - O(log(n)), n - число узлов
    // Ресурсоемкость: O(1)
    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> nodeToRemove = find(t);
        if (nodeToRemove == null) return false;
        if (nodeToRemove.value != t) return false;

        if (nodeToRemove.left == null && nodeToRemove.right == null) {
            // nodeToRemove - листок.
            if (nodeToRemove.parent != null) {
                if (nodeToRemove.parent.left == nodeToRemove) nodeToRemove.parent.left = null;
                else nodeToRemove.parent.right = null;
            } else root = null;
        } else {
            // nodeToRemove имеет потомка(ов).
            Node<T> newNode;
            if (nodeToRemove.right != null) {
                newNode = nodeToRemove.right;
                while (newNode.left != null) {
                    newNode = newNode.left;
                }
            } else newNode = nodeToRemove.left;
            replaceNodeWith(nodeToRemove, newNode);
        }
        size--;
        return true;
    }

    private void replaceNodeWith(Node<T> nodeToRemove, Node<T> newNode) {

        // В этом случае newNode может иметь только одного правого потомка.
        // При вызове replaceNodeWith мы брали самый левый узел (newNode не может иметь левого потомка) в правом
        // поддереве (см. строки 125-129) и тогда условие (newNode.parent != nodeToRemove) верно.
        // Если правый потомок отсутствует, мы берем левого потомка (см. строку 130), у которого могут быть оба
        // потомка и тогда условие (newNode.parent != nodeToRemove) ложно.
        if (newNode.parent != nodeToRemove) {
            if (newNode.right != null) {
                newNode.right.parent = newNode.parent;
            }
            newNode.parent.left = newNode.right;
        }

        if (nodeToRemove.left != newNode) {
            if (nodeToRemove.right != newNode) {
                newNode.right = nodeToRemove.right;
                if (nodeToRemove.right != null) nodeToRemove.right.parent = newNode;
            }
            newNode.left = nodeToRemove.left;
            if (nodeToRemove.left != null) nodeToRemove.left.parent = newNode;
        }
        newNode.parent = nodeToRemove.parent;

        if (nodeToRemove.parent != null) {
            if (nodeToRemove.parent.left == nodeToRemove) nodeToRemove.parent.left = newNode;
            else nodeToRemove.parent.right = newNode;
        }

        if (root == nodeToRemove) root = newNode;
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
    }

    public class BinarySearchTreeIterator implements Iterator<T> {

        // Ресурсоемкость: O(1)
        private Node<T> next;

        // Быстродействие: худшее - O(n) (все узлы в одной левой ветке), среднее - O(log(n)), n - число узлов
        private BinarySearchTreeIterator() {
            next = root;
            if (next == null) return;

            while (next.left != null)
                next = next.left;
        }

        /**
         * Проверка наличия следующего элемента
         * <p>
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         * <p>
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         * <p>
         * Средняя
         */

        // Быстродействие: O(1)
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Получение следующего элемента
         * <p>
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         * <p>
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         * <p>
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         * <p>
         * Средняя
         */

        // Быстродействие: худшее - O(n) (все узлы в одной ветке), среднее - O(log(n)), n - число узлов
        // Ресурсоемкость: O(1)
        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            Node<T> r = next;

            if (next.right != null) {
                next = next.right;
                while (next.left != null)
                    next = next.left;
                return r.value;
            }

            while (true) {
                if (next.parent == null) {
                    next = null;
                    return r.value;
                }
                if (next.parent.left == next) {
                    next = next.parent;
                    return r.value;
                }
                next = next.parent;
            }
        }

        /**
         * Удаление предыдущего элемента
         * <p>
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         * <p>
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         * <p>
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         * <p>
         * Сложная
         */
        @Override
        public void remove() {
            // TODO
            throw new NotImplementedError();
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new SubBinarySearchTree<>(fromElement, toElement, this);
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        return new SubBinarySearchTree<>(null, toElement, this);
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new SubBinarySearchTree<>(fromElement, null, this);
    }

    public static class SubBinarySearchTree<T extends Comparable<T>> extends BinarySearchTree<T> {

        // Дерево не может содержать элементов >= upperBound и < lowerBound
        // Ресурсоемкость: O(1)
        private final T upperBound;
        private final T lowerBound;
        private final BinarySearchTree<T> tree;

        // Быстродействие: O(1)
        public SubBinarySearchTree(T lowerBound, T upperBound, BinarySearchTree<T> tree) {
            if (upperBound != null && lowerBound != null && upperBound.compareTo(lowerBound) < 0)
                throw new IllegalArgumentException();
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.tree = tree;
            super.root = tree.root;
        }

        @Override
        public boolean contains(Object o) {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            if (outOfBounds(t)) return false;
            return tree.contains(o);
        }

        @Override
        public boolean add(T t) {
            if (outOfBounds(t)) throw new IllegalArgumentException();
            return tree.add(t);
        }

        // Оценку см. BinarySearchTree.remove()
        @Override
        public boolean remove(Object o) {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            if (outOfBounds(t)) return false;
            return tree.remove(o);
        }

        // Оценку см. countSize()
        @Override
        public int size() {
            return countSize();
        }

        // Быстродействие: O(1)
        // Ресурсоемкость: O(1)
        @NotNull
        @Override
        public SortedSet<T> headSet(T toElement) {
            if (toElement.compareTo(upperBound) > 0 || (lowerBound != null && toElement.compareTo(lowerBound) < 0))
                throw new IllegalArgumentException();
            return new SubBinarySearchTree<>(lowerBound, toElement, tree);//super.headSet(toElement);
        }

        // Быстродействие: O(1)
        // Ресурсоемкость: O(1)
        @NotNull
        @Override
        public SortedSet<T> tailSet(T fromElement) {
            if (outOfBounds(fromElement)) throw new IllegalArgumentException();
            return new SubBinarySearchTree<>(fromElement, upperBound, tree);//super.headSet(toElement);
            //super.tailSet(fromElement);
        }

        // Быстродействие: O(1)
        // Ресурсоемкость: O(1)
        @NotNull
        @Override
        public SortedSet<T> subSet(T fromElement, T toElement) {
            if ((lowerBound != null && fromElement.compareTo(lowerBound) < 0) ||
                    (upperBound != null && toElement.compareTo(upperBound) > 0)) throw new IllegalArgumentException();
            return new SubBinarySearchTree<>(fromElement, toElement, tree);//super.headSet(toElement);

            //return super.subSet(fromElement, toElement);
        }

        private boolean outOfBounds(T t) {
            if (lowerBound != null && t.compareTo(lowerBound) < 0) return true;
            if (upperBound != null && t.compareTo(upperBound) >= 0) return true;
            return false;
        }

        // Быстродействие: худшее - O(m*n) + O(k*k), среднее - O(m*log(n)) + O(k*log(k))
        //                 Инициализации итератора + перебор значений поддерева (k раз вызов next()), где
        //                 m - число узлов в исходном дереве до нижней границы, k - число узлов поддерева,
        //                 n - число узлов в исходном дереве
        // Ресурсоемкость: O(1)
        private int countSize() {
            Iterator<T> iterator = new SubBinarySearchTreeIterator();
            int size = 0;
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
            return size;
        }

        public class SubBinarySearchTreeIterator extends BinarySearchTreeIterator {

            private boolean ignoreBounds = false;

            // Быстродействие: худшее - O(n) + O(m*n) = O(m*n), среднее O(log(n)) + O(m*log(n)) = O(m*log(n))
            //                 получение минимального узла исходного дерева + вызов next() m раз,
            //                 где m - число узлов в исходном дереве до нижней границы,
            //                 n - число узлов в исходном дереве
            // Ресурсоемкость: O(1)
            public SubBinarySearchTreeIterator() {
                super.next = tree.root;
                if (super.next == null) return;

                while (super.next.left != null)
                    super.next = super.next.left;

                if (lowerBound != null) {
                    while (super.next.value.compareTo(lowerBound) < 0) {
                        ignoreBounds = true;
                        next();
                        if (!hasNext()) {
                            super.next = null;
                            break;
                        }
                    }
                    ignoreBounds = false;
                }
            }

            @Override
            public boolean hasNext() {
                if (super.next == null) return false;
                return !outOfBounds(super.next.value) || ignoreBounds;
            }
        }

        // Быстродействие: худшее - O(n) + O(m), среднее - O(log(n)) + O(log(m)), n - число узлов исходного дерева
        //                 O(find()) + O(next())                                  m - число узлов поддерева
        // Ресурсоемкость: O(1)
        @Override
        public T first() {
            if (lowerBound == null) {
                T value = tree.first();
                // Проверка на (upperBound != null) не требуется
                if (value.compareTo(upperBound) < 0) return value; else throw new NoSuchElementException();
            }
            Node<T> closest = tree.find(lowerBound);
            T value;
            if (closest == null) throw new NoSuchElementException();
            System.out.println("we found 1 " + closest.value);
            if (closest.value.compareTo(lowerBound) < 0) {
                BinarySearchTreeIterator it = new BinarySearchTreeIterator();
                it.next = closest;
                it.next();
                value = it.next();
                if (outOfBounds(value)) throw new NoSuchElementException();
                return value;
            }
            if (upperBound != null && closest.value.compareTo(upperBound) >= 0) throw new NoSuchElementException();
            System.out.println(closest.value);
            return closest.value;
        }

        // Быстродействие:  худшее - O(n) (все узлы в одной ветке), среднее - O(log(n))
        //                  поиск ближайшего к верхней границе узла O(n) + поиск предыдущего узла в исходном дереве O(n)
        //                  n - число узлов в исходном дереве
        // Ресурсоемкость:  O(1)
        @Override
        public T last() {
            if (upperBound == null) {
                T value = tree.last();
                // Проверка на (lowerBound != null) не требуется
                if (value.compareTo(lowerBound) >= 0) return value; else throw new NoSuchElementException();
            }
            Node<T> closest = tree.find(upperBound);
            if (closest == null) throw new NoSuchElementException();
            if (closest.value.compareTo(upperBound) < 0) {
                if (!outOfBounds(closest.value)) return closest.value;
                else throw new NoSuchElementException();
            }

            if (closest.left != null) {
                closest = closest.left;
                while (closest.right != null) {
                    closest = closest.right;
                }
            } else {
                while (closest.parent != null) {
                    if (closest.parent.right == closest) {
                        closest = closest.parent;
                        break;
                    }
                    closest = closest.parent;
                }
            }
            if (outOfBounds(closest.value)) throw new NoSuchElementException();
            return closest.value;
        }
        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            //System.out.println("root is:" + root.value);
            SubBinarySearchTreeIterator iterator = new SubBinarySearchTreeIterator();
            while (iterator.hasNext()) {
                T v = iterator.next();
                //System.out.println("appending " + v + " next is " + iterator.hasNext());
                s.append(v);
                s.append("; ");
            }
            return s.toString();
        }
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        //System.out.println("root is:" + root.value);
        BinarySearchTreeIterator iterator = new BinarySearchTreeIterator();
        while (iterator.hasNext()) {
            T v = iterator.next();
            //System.out.println("appending " + v + " next is " + iterator.hasNext());
            s.append(v);
            s.append("; ");
        }
        return s.toString();
    }
}