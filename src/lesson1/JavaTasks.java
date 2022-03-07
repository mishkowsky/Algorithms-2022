package lesson1;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.*;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     * <p>
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     * <p>
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     * <p>
     * Пример:
     * <p>
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     * <p>
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     * <p>
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     * <p>
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */

    //Оценка:
    //       Ресурсоемкость: O(n)
    //       Быстродействие: Timsort
    //                       худшее: O(n log(n))
    //                       лучшее: O(n)
    static public void sortTimes(String inputName, String outputName) throws IOException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        File file = new File(inputName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<Calendar> dates = new ArrayList<>();
        for (String line; (line = br.readLine()) != null;) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(simpleDateFormat.parse(line));
            cal.set(0, Calendar.JANUARY,1);
            dates.add(cal);
        }
        br.close();
        // Timsort
        Collections.sort(dates);
        FileWriter fw = new FileWriter(outputName);
        for (Calendar cal : dates) {
            fw.write(simpleDateFormat.format(cal.getTime()));
            if (dates.iterator().hasNext()) fw.write(System.getProperty("line.separator"));
        }
        fw.close();
    }

    /**
     * Сортировка адресов
     * <p>
     * Средняя
     * <p>
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     * <p>
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     * <p>
     * Людей в городе может быть до миллиона.
     * <p>
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     * <p>
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     * <p>
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */

    // Поддержка 'ё' и 'Ё' в алфавитном порядке. Исправление тестов addr_out2.txt и addr_out3.txt.
    // Оценка: n - число жителей, m - число адресов
    //         Ресурсоемкость: O(n) + O(m)
    //         Быстродействие: Timsort + BinaryTree
    //                         худшее: O(n log(n)) + O(m log(m))
    //                         лучшее: O(n) + O(m)
    static public void sortAddresses(String inputName, String outputName) throws IOException {

        BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(inputName), StandardCharsets.UTF_8));

        SortedMap<Address, List<String>> addresses = new TreeMap<>();

        // O(n) чтение всех жителей из файла + O(m log(m)) (O(m) в лучшем) сортировка адресов в TreeMap, которая
        // выполняется только при условии нового адреса (строка 122).
        for (String line; (line = br.readLine()) != null;) {

            // \w не поддерживает русские буквы, также имена не могут содержать 0-9
            String letter = "[А-ЯЁа-яёA-Za-z-]";
            String regex = letter + "+ " + letter + "+ - " + letter + "+ \\d+";
            if (!line.matches(regex))
                throw new IllegalArgumentException(line);
            String[] split = line.split(" - ");
            String[] splitAddress = split[1].split(" ");
            Address address = new Address(Integer.parseInt(splitAddress[1]), splitAddress[0]);

            String newName = split[0];

            if (!addresses.containsKey(address))
                addresses.put(address, new ArrayList<>(Collections.singleton(newName)));
            else addresses.get(address).add(newName);
        }
        br.close();

        // Timsort худшее: O(n log(n))
        //         лучшее: O(n)
        for (Address address : addresses.keySet()) {
            addresses.get(address).sort(comparator);
        }

        OutputStreamWriter writer =
                new OutputStreamWriter(new FileOutputStream(outputName), StandardCharsets.UTF_8);

        // O(n) - запись всех жителей в файл.
        for (var entry : addresses.entrySet()) {
            writer.write(entry.getKey() + " - ");
            for (String name : entry.getValue()) {
                writer.write(name);
                if (entry.getValue().indexOf(name) != entry.getValue().size() - 1) writer.write(", ");
            }
            writer.write(System.getProperty("line.separator"));
        }
        writer.close();
    }

    public static class Address implements Comparable<Address> {

        private final int number;
        private final String street;

        public Address(int n, String s) {
            number = n;
            street = s;
        }

        @Override
        public String toString() {
            return street + " " + number;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Address anotherAddress)) return false;
            return (this.number == anotherAddress.number) && (this.street.equals(anotherAddress.street));
        }

        @Override
        public int compareTo(@NotNull Address anotherAddress) {
            int compare;
            if (!this.street.equals(anotherAddress.street))
                return comparator.compare(this.street, anotherAddress.street);
            if (this.number - anotherAddress.number == 0) return 0;
            return (this.number - anotherAddress.number) / abs(this.number - anotherAddress.number);
        }
    }

    private static final StringComparator comparator = new StringComparator();

    public static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int compare;
            if (o1.indexOf('ё') != -1 || o2.indexOf('ё') != -1 ||
                    o1.indexOf('Ё') != -1 || o2.indexOf('Ё') != -1)
                compare = customCompare(o1, o2);
            else compare = o1.compareTo(o2);
            return compare;
        }
    }

    public static int customCompare(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int lim = Math.min(len1, len2);
        for (int k = 0; k < lim; k++) {
            char c1 = s1.charAt(k);
            char c2 = s2.charAt(k);
            if (c1 != c2 && (c1 == 'ё' || c2 == 'ё' || c1 == 'Ё' || c2 == 'Ё')) {
                if (c1 == 'ё' || c1 == 'Ё') return compareWithSpecialChar(c2, c1 == 'Ё');
                else return -compareWithSpecialChar(c1, c2 == 'Ё');
            }
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return len1 - len2;
    }

    public static int compareWithSpecialChar(char c, boolean upCase) {
        char ch;
        if ((c >= 'ж' && c <= 'я') || (c >= 'Ж' && c <= 'Я')) ch = 'е';
        else ch = 'ж';
        if (upCase) ch = Character.toUpperCase(ch);
        return ch - c;
    }

    /**
     * Сортировка температур
     * <p>
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     * <p>
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     * <p>
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     * <p>
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     * <p>
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */

    static public void sortTemperatures(String inputName, String outputName) throws IOException {
        //fastSortTemperatures(inputName, outputName);
        fastestSortTemperatures(inputName, outputName);
    }

    // Оценка: n - число температур
    //         Ресурсоемкость: O(n)
    //         Быстродействие: O(n)
    static public void fastestSortTemperatures(String inputName, String outputName) throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(inputName), StandardCharsets.UTF_8));
        Map<Integer, Integer> range = new HashMap<>();

        for (String line; (line = br.readLine()) != null; ) {
            String[] split = line.split("\\.");
            int i = Integer.parseInt(split[0]);
            int f = Integer.parseInt(split[1]);
            int sign = 1;
            if (line.indexOf('-') != -1) {
                if (line.indexOf('-') == 0) sign = -1;
                else throw new IllegalArgumentException(line);
            }
            if (i < -273 || i > 500 || f < 0 || f > 9) throw new IllegalArgumentException(line);
            int temp = i * 10 + f * sign;
            if (!range.containsKey(temp)) range.put(temp, 1); else range.put(temp, range.get(temp) + 1);
        }

        br.close();

        OutputStreamWriter writer =
                new OutputStreamWriter(new FileOutputStream(outputName), StandardCharsets.UTF_8);

        for (int i = -2739; i < 5001; i++) {
            if (range.get(i) != null) {
                int amount = range.get(i);
                while (amount > 0) {
                    if (i / 10 == 0 && i < 0) writer.write("-");
                    writer.write("" + i / 10 + "." + abs(i % 10));
                    writer.write(System.getProperty("line.separator"));
                    amount--;
                }
            }
        }
        writer.close();
    }

    // Оценка: n - число температур
    //         Ресурсоемкость: O(n)
    //         Быстродействие: Timsort
    //                         худшее: O(n log(n))
    //                         лучшее: O(n)
    static public void fastSortTemperatures(String inputName, String outputName) throws IOException {

        BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(inputName), StandardCharsets.UTF_8));
        Map<Integer, List<Integer>> range = new HashMap<>(773);

        for (String line; (line = br.readLine()) != null; ) {
            String[] split = line.split("\\.");
            int sign = 1;
            if (split.length != 2) throw new IllegalArgumentException(line);
            int i = Integer.parseInt(split[0]);
            int f = Integer.parseInt(split[1]);
            if (i < -273 || i > 500 || f < 0 || f > 9) throw new IllegalArgumentException(line);
            if (line.indexOf('-') != -1) sign = -1;

            if (i == 0 && sign == -1) i = 501; // Диапазон [-0.9..-0.1] помещается в корзину с индексом 501

            List<Integer> bucket = range.get(i);
            if (bucket == null) range.put(i, new ArrayList<>(Collections.singleton(f)));
            else bucket.add(f);
        }
        br.close();

        OutputStreamWriter writer =
                new OutputStreamWriter(new FileOutputStream(outputName), StandardCharsets.UTF_8);

        boolean flag = false;
        int i = -273;
        while (i <= 501) {
            if (range.get(i) != null) {
                // Timsort
                Collections.sort(range.get(i));
                if (i < 0 || i == 501) Collections.reverse(range.get(i));
                for (Integer f : range.get(i)) {
                    if (i != 501) writer.write(i + "." + f);
                    else writer.write("-0." + f);
                    writer.write(System.getProperty("line.separator"));
                }
            }
            i++;
            if (i == 502) {
                i = 0;
                flag = true;
            }
            if (i == 0 && !flag) i = 501;
            if (i == 501 && flag) break;
        }
        writer.close();
    }

    /**
     * Сортировка последовательности
     * <p>
     * Средняя
     * (Задача взята с сайта acmp.ru)
     * <p>
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     * <p>
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     * <p>
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     * <p>
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Соединить два отсортированных массива в один
     * <p>
     * Простая
     * <p>
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     * <p>
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     * <p>
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
