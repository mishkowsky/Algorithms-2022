package lesson2;

import kotlin.NotImplementedError;
import kotlin.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class JavaAlgorithms {
    /**
     * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
     * Простая
     *
     * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
     * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
     *
     * 201
     * 196
     * 190
     * 198
     * 187
     * 194
     * 193
     * 185
     *
     * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
     * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
     * Вернуть пару из двух моментов.
     * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
     * Например, для приведённого выше файла результат должен быть Pair(3, 4)
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public Pair<Integer, Integer> optimizeBuyAndSell(String inputName) {
        throw new NotImplementedError();
    }

    /**
     * Задача Иосифа Флафия.
     * Простая
     *
     * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
     *
     * 1 2 3
     * 8   4
     * 7 6 5
     *
     * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
     * Человек, на котором остановился счёт, выбывает.
     *
     * 1 2 3
     * 8   4
     * 7 6 х
     *
     * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
     * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
     *
     * 1 х 3
     * 8   4
     * 7 6 Х
     *
     * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
     *
     * 1 Х 3
     * х   4
     * 7 6 Х
     *
     * 1 Х 3
     * Х   4
     * х 6 Х
     *
     * х Х 3
     * Х   4
     * Х 6 Х
     *
     * Х Х 3
     * Х   х
     * Х 6 Х
     *
     * Х Х 3
     * Х   Х
     * Х х Х
     *
     * Общий комментарий: решение из Википедии для этой задачи принимается,
     * но приветствуется попытка решить её самостоятельно.
     */
    static public int josephTask(int menNumber, int choiceInterval) {
        throw new NotImplementedError();
    }

    /**
     * Наибольшая общая подстрока.
     * Средняя
     *
     * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
     * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
     * Если общих подстрок нет, вернуть пустую строку.
     * При сравнении подстрок, регистр символов *имеет* значение.
     * Если имеется несколько самых длинных общих подстрок одной длины,
     * вернуть ту из них, которая встречается раньше в строке first.
     */
    // Пусть n - длина первой строки, m - длина второй строки.
    // Наихудшее быстродействие: O((n + m) * min(n,m)) * log(min(n,m)))
    //                           (Заполнение set'a * substring + перебор подстрок первой строки * substring) *
    //                           * (бинарный поиск)
    //                           Наибольшее значение substring в худшем случае = 0,5p + 0,25p + 0,125p + ... = p,
    //                           Начальное = 0,5p.
    //                           То есть среднее значение substring в худшем случае 3p/4 = p = min(n,m).
    // Наихудшая ресурсоемкость: При заполнении set'a подстроками длиной m/2 из строки длиной m,
    //                           тогда число символов которое хранит set = m/2 * (m - m/2 + 1) => O(m^2)
    //                           Улучшить ресурсоемкость можно, сохраняя не сами подстроки, а их хэши, но такой
    //                           подход требует защиты от коллизий.

    static public String longestCommonSubstring(String first, String second) {
        boolean flag = false;
        int end = Math.min(first.length(), second.length());
        int start = 1;
        int result = (end + start) / 2;
        String maxMatch = "";

        Set<String> set = new HashSet<>();

        boolean condition;
        while (true) {
            set.clear();
            createHashSet(set, second, result);
            condition = false;
            for (int j = 0; j <= first.length() - result; j++) {
                String substring = first.substring(j, j + result);
                if (set.contains(substring)) {
                    maxMatch = substring;
                    condition = true;
                    break;
                }
            }

            if (end == start || flag) break;
            if (condition) start = result; else end = result;

            if (end - start == 1) {
                if (condition) result++; else result--;
                flag = true;
            }
            else result = (end + start) / 2;
        }
        return maxMatch;
    }

    static public void createHashSet(Set<String> set, String string, int length) {
        for (int i = 0; i <= string.length() - length; i++) {
            set.add(string.substring(i, i + length));
        }
    }

    /**
     * Число простых чисел в интервале
     * Простая
     *
     * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
     * Если limit <= 1, вернуть результат 0.
     *
     * Справка: простым считается число, которое делится нацело только на 1 и на себя.
     * Единица простым числом не считается.
     */

    // Быстродействие: O(nlog(log(n))
    // Ресурсоемкость: O(n)

    static public int calcPrimesNumber(int limit) {
        int counter = 0;
        if (limit <= 1) return 0;
        boolean[] primes = new boolean[limit - 1];
        Arrays.fill(primes, true);

        for (int i = 2; i <= limit; i++) {
            if (primes[i - 2]) {
                int p = 2;
                while (i * p <= limit) {
                    primes[i * p - 2] = false;
                    p++;
                }
                counter++;
            }
        }
        return counter;
    }
}
