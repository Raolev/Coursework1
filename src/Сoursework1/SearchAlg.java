package Сoursework1;

import java.util.*;
import java.util.function.DoubleUnaryOperator;


public class SearchAlg {

    // ==================== ЧАСТЬ 1. ПОИСК В МАССИВЕ ====================

    /**
     * 1. КЛАССИЧЕСКИЙ БИНАРНЫЙ ПОИСК
     *
     * Временная сложность: O(log n)
     * Пространственная сложность: O(1)
     *
     * @param arr    отсортированный массив
     * @param target искомое значение
     * @return индекс элемента или -1
     */
    public static SearchResult binarySearch(int[] arr, int target) {
        long startTime = System.nanoTime();
        int left = 0;
        int right = arr.length - 1;
        int iterations = 0;

        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2; // Защита от переполнения

            if (arr[mid] == target) {
                long endTime = System.nanoTime();
                return new SearchResult("Бинарный поиск", mid, iterations, endTime - startTime);
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        long endTime = System.nanoTime();
        return new SearchResult("Бинарный поиск", -1, iterations, endTime - startTime);
    }

    /**
     * 2. ИНТЕРПОЛЯЦИОННЫЙ ПОИСК
     *
     * Временная сложность: O(log log n) в среднем, O(n) в худшем
     *
     * @param arr    отсортированный массив (лучше для равномерного распределения)
     * @param target искомое значение
     * @return индекс элемента или -1
     */
    public static SearchResult interpolationSearch(int[] arr, int target) {
        long startTime = System.nanoTime();
        int left = 0;
        int right = arr.length - 1;
        int iterations = 0;

        while (left <= right && target >= arr[left] && target <= arr[right]) {
            iterations++;

            // Формула интерполяции
            int pos = left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left]);

            // Защита от выхода за границы
            if (pos < left || pos > right) {
                break;
            }

            if (arr[pos] == target) {
                long endTime = System.nanoTime();
                return new SearchResult("Интерполяционный поиск", pos, iterations, endTime - startTime);
            }

            if (arr[pos] < target) {
                left = pos + 1;
            } else {
                right = pos - 1;
            }
        }

        long endTime = System.nanoTime();
        return new SearchResult("Интерполяционный поиск", -1, iterations, endTime - startTime);
    }

    /**
     * 3. МЕТОД ЗОЛОТОГО СЕЧЕНИЯ (поиск минимума функции)
     *
     * Временная сложность: O(log_φ n)
     *
     * @param f   унимодальная функция
     * @param a   левая граница интервала
     * @param b   правая граница интервала
     * @param eps точность
     * @return точка минимума
     */
    public static GoldenSectionResult goldenSectionSearch(DoubleUnaryOperator f, double a, double b, double eps) {
        long startTime = System.nanoTime();
        final double PHI = (1 + Math.sqrt(5)) / 2;
        final double INV_PHI = 1 / PHI;

        int iterations = 0;
        double x1 = b - (b - a) * INV_PHI;
        double x2 = a + (b - a) * INV_PHI;
        double f1 = f.applyAsDouble(x1);
        double f2 = f.applyAsDouble(x2);

        while ((b - a) > eps) {
            iterations++;

            if (f1 < f2) {
                b = x2;
                x2 = x1;
                f2 = f1;
                x1 = b - (b - a) * INV_PHI;
                f1 = f.applyAsDouble(x1);
            } else {
                a = x1;
                x1 = x2;
                f1 = f2;
                x2 = a + (b - a) * INV_PHI;
                f2 = f.applyAsDouble(x2);
            }
        }

        long endTime = System.nanoTime();
        double minPoint = (a + b) / 2;
        double minValue = f.applyAsDouble(minPoint);

        return new GoldenSectionResult(minPoint, minValue, iterations, endTime - startTime);
    }

    /**
     * 4. РЕКУРСИВНАЯ ВЕРСИЯ БИНАРНОГО ПОИСКА
     */
    public static int binarySearchRecursive(int[] arr, int target, int left, int right, Counter counter) {
        counter.increment();
        if (left > right) return -1;

        int mid = left + (right - left) / 2;

        if (arr[mid] == target) return mid;
        else if (arr[mid] < target)
            return binarySearchRecursive(arr, target, mid + 1, right, counter);
        else
            return binarySearchRecursive(arr, target, left, mid - 1, counter);
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================

    static class Counter {
        private int count = 0;
        public void increment() { count++; }
        public int getCount() { return count; }
        public void reset() { count = 0; }
    }

    static class SearchResult {
        String algorithmName;
        int index;
        int iterations;
        long timeNanos;

        SearchResult(String name, int idx, int iter, long time) {
            this.algorithmName = name;
            this.index = idx;
            this.iterations = iter;
            this.timeNanos = time;
        }

        @Override
        public String toString() {
            return String.format("%-25s | индекс: %-6d | итераций: %-6d | время: %8d нс",
                    algorithmName, index, iterations, timeNanos);
        }
    }

    static class GoldenSectionResult {
        double minPoint;
        double minValue;
        int iterations;
        long timeNanos;

        GoldenSectionResult(double point, double value, int iter, long time) {
            this.minPoint = point;
            this.minValue = value;
            this.iterations = iter;
            this.timeNanos = time;
        }

        @Override
        public String toString() {
            return String.format("Точка минимума: %.10f\nЗначение функции: %.10f\nИтераций: %d\nВремя: %d нс",
                    minPoint, minValue, iterations, timeNanos);
        }
    }

    // ==================== ТЕСТОВЫЕ ФУНКЦИИ ====================

    /**
     * Квадратичная функция f(x) = (x - 3)^2 + 2
     * Минимум в точке x = 3
     */
    static class QuadraticFunction implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {
            return (x - 3) * (x - 3) + 2;
        }

        @Override
        public String toString() {
            return "f(x) = (x - 3)² + 2";
        }
    }

    /**
     * Функция f(x) = x^4 - 4x^2 + 4
     * Минимум в точках x = -√2 и x = √2
     */
    static class QuarticFunction implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {
            return Math.pow(x, 4) - 4 * x * x + 4;
        }

        @Override
        public String toString() {
            return "f(x) = x⁴ - 4x² + 4";
        }
    }

    /**
     * Тригонометрическая функция f(x) = sin(x) + cos(2x)
     */
    static class TrigonometricFunction implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {
            return Math.sin(x) + Math.cos(2 * x);
        }

        @Override
        public String toString() {
            return "f(x) = sin(x) + cos(2x)";
        }
    }

    // ==================== ТЕСТИРОВАНИЕ ====================

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("КУРСОВАЯ РАБОТА: Сравнительный анализ алгоритмов поиска");
        System.out.println("=".repeat(80));

        // ===== ТЕСТ 1: Бинарный и интерполяционный поиск =====
        System.out.println("\n【ТЕСТ 1】Поиск в отсортированном массиве");
        System.out.println("-".repeat(60));

        // Создание тестовых массивов
        int[] uniformArray = createUniformArray(100000);      // Равномерное распределение
        int[] exponentialArray = createExponentialArray(20);  // Экспоненциальное распределение

        System.out.println("Размер равномерного массива: " + uniformArray.length);
        System.out.println("Размер экспоненциального массива: " + exponentialArray.length);

        // Поиск существующего элемента
        System.out.println("\n--- Поиск существующего элемента (50000) в равномерном массиве ---");
        SearchResult bs1 = binarySearch(uniformArray, 50000);
        SearchResult is1 = interpolationSearch(uniformArray, 50000);
        System.out.println(bs1);
        System.out.println(is1);

        // Поиск несуществующего элемента
        System.out.println("\n--- Поиск несуществующего элемента (50001) в равномерном массиве ---");
        SearchResult bs2 = binarySearch(uniformArray, 50001);
        SearchResult is2 = interpolationSearch(uniformArray, 50001);
        System.out.println(bs2);
        System.out.println(is2);

        // Поиск в экспоненциальном массиве (худший случай для интерполяции)
        System.out.println("\n--- Поиск в экспоненциальном массиве (x = 524288) ---");
        SearchResult bs3 = binarySearch(exponentialArray, 524288);
        SearchResult is3 = interpolationSearch(exponentialArray, 524288);
        System.out.println(bs3);
        System.out.println(is3);

        // ===== ТЕСТ 2: Метод золотого сечения =====
        System.out.println("\n【ТЕСТ 2】Поиск минимума функции методом золотого сечения");
        System.out.println("-".repeat(60));

        QuadraticFunction f1 = new QuadraticFunction();
        System.out.println("Функция: " + f1);
        System.out.println("Интервал: [0, 6]");
        System.out.println("Точность: 1e-8");

        GoldenSectionResult gs1 = goldenSectionSearch(f1, 0, 6, 1e-8);
        System.out.println("\nРезультат:");
        System.out.println(gs1);

        // Сравнение с точным значением
        double exactMin = 3.0;
        double error = Math.abs(gs1.minPoint - exactMin);
        System.out.printf("Погрешность: %.2e\n", error);

        // ===== ТЕСТ 3: Сравнение производительности =====
        System.out.println("\n【ТЕСТ 3】Сравнение производительности на 10000 запросах");
        System.out.println("-".repeat(60));

        performanceTest(uniformArray);

        // ===== ТЕСТ 4: Рекурсивная версия =====
        System.out.println("\n【ТЕСТ 4】Рекурсивный бинарный поиск");
        System.out.println("-".repeat(60));

        Counter counter = new Counter();
        int recResult = binarySearchRecursive(uniformArray, 50000, 0, uniformArray.length - 1, counter);
        System.out.println("Рекурсивный поиск: элемент найден на индексе " + recResult);
        System.out.println("Количество рекурсивных вызовов: " + counter.getCount());
    }

    /**
     * Создание равномерно распределенного массива
     */
    private static int[] createUniformArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        return arr;
    }

    /**
     * Создание экспоненциально растущего массива (степени двойки)
     */
    private static int[] createExponentialArray(int power) {
        int size = (int) Math.pow(2, power);
        int[] arr = new int[size];
        arr[0] = 1;
        for (int i = 1; i < size; i++) {
            arr[i] = arr[i-1] * 2;
        }
        return arr;
    }

    /**
     * Тест производительности
     */
    private static void performanceTest(int[] arr) {
        Random random = new Random(42);
        int testCount = 10000;

        long totalBinary = 0;
        long totalInterp = 0;
        int totalBinaryIter = 0;
        int totalInterpIter = 0;

        for (int i = 0; i < testCount; i++) {
            int target = random.nextInt(arr.length * 2);

            SearchResult bs = binarySearch(arr, target);
            SearchResult is = interpolationSearch(arr, target);

            totalBinary += bs.timeNanos;
            totalInterp += is.timeNanos;
            totalBinaryIter += bs.iterations;
            totalInterpIter += is.iterations;
        }

        System.out.printf("Среднее время (бинарный):      %8.2f нс\n", totalBinary / (double) testCount);
        System.out.printf("Среднее время (интерполяц.):   %8.2f нс\n", totalInterp / (double) testCount);
        System.out.printf("Среднее итераций (бинарный):   %8.2f\n", totalBinaryIter / (double) testCount);
        System.out.printf("Среднее итераций (интерпол.):  %8.2f\n", totalInterpIter / (double) testCount);

        // Ускорение интерполяционного поиска
        double speedup = (double) totalBinary / totalInterp;
        System.out.printf("\nИнтерполяционный поиск быстрее бинарного в %.2f раза\n", speedup);
    }
}