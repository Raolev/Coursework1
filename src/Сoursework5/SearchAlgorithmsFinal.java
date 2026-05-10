package Сoursework5;

import java.util.function.DoubleUnaryOperator;

public final class SearchAlgorithmsFinal {

    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double INV_PHI = 1 / PHI;

    // ==================== БИНАРНЫЙ ПОИСК ====================

    public static Result binarySearch(int[] arr, int target) {
        long start = System.nanoTime();
        int left = 0, right = arr.length - 1, iterations = 0;
        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2;
            if (arr[mid] == target)
                return new Result("Бинарный", mid, iterations, System.nanoTime() - start);
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return new Result("Бинарный", -1, iterations, System.nanoTime() - start);
    }

    // ==================== ИНТЕРПОЛЯЦИОННЫЙ ПОИСК ====================

    public static Result interpolationSearch(int[] arr, int target) {
        long start = System.nanoTime();
        int left = 0, right = arr.length - 1, iterations = 0;
        while (left <= right && target >= arr[left] && target <= arr[right]) {
            iterations++;
            if (arr[left] == arr[right]) break;
            int pos = left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left]);
            if (pos < left || pos > right) break;
            if (arr[pos] == target)
                return new Result("Интерполяционный", pos, iterations, System.nanoTime() - start);
            if (arr[pos] < target) left = pos + 1;
            else right = pos - 1;
        }
        return new Result("Интерполяционный", -1, iterations, System.nanoTime() - start);
    }

    // ==================== МЕТОД ЗОЛОТОГО СЕЧЕНИЯ ====================

    public static OptResult goldenSection(DoubleUnaryOperator f, double a, double b, double eps) {
        long start = System.nanoTime();
        int iter = 0;
        double x1 = b - (b - a) * INV_PHI;
        double x2 = a + (b - a) * INV_PHI;
        double f1 = f.applyAsDouble(x1);
        double f2 = f.applyAsDouble(x2);
        while ((b - a) > eps) {
            iter++;
            if (f1 < f2) {
                b = x2; x2 = x1; f2 = f1;
                x1 = b - (b - a) * INV_PHI;
                f1 = f.applyAsDouble(x1);
            } else {
                a = x1; x1 = x2; f1 = f2;
                x2 = a + (b - a) * INV_PHI;
                f2 = f.applyAsDouble(x2);
            }
        }
        double minPoint = (a + b) / 2;
        return new OptResult("Золотое сечение", minPoint, f.applyAsDouble(minPoint),
                iter, System.nanoTime() - start);
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================

    static class Result {
        String name; int index; int iterations; long timeNanos;
        Result(String n, int i, int it, long t) {
            name = n; index = i; iterations = it; timeNanos = t;
        }
        public String toString() {
            return String.format("%-20s | индекс: %-6d | итераций: %-4d | время: %8d нс",
                    name, index, iterations, timeNanos);
        }
    }

    static class OptResult {
        String name; double point; double value; int iterations; long timeNanos;
        OptResult(String n, double p, double v, int it, long t) {
            name = n; point = p; value = v; iterations = it; timeNanos = t;
        }
        public String toString() {
            return String.format("%-20s | точка: %12.10f | значение: %12.10f | итераций: %4d | время: %8d нс",
                    name, point, value, iterations, timeNanos);
        }
    }

    // ==================== ТЕСТИРОВАНИЕ ====================

    public static void main(String[] args) {
        System.out.println("=".repeat(90));
        System.out.println("КУРСОВАЯ РАБОТА: Сравнительный анализ алгоритмов поиска");
        System.out.println("Золотое сечение + Фибоначчи");
        System.out.println("=".repeat(90));

        // Тест 1: Поиск в массиве
        int[] arr = new int[1_000_000];
        for (int i = 0; i < arr.length; i++) arr[i] = i;
        System.out.println("\n【1】Поиск в массиве (равномерное распределение)");
        System.out.println(binarySearch(arr, 500_000));
        System.out.println(interpolationSearch(arr, 500_000));

        // Тест 2: Поиск минимума функции
        DoubleUnaryOperator f = x -> (x - 3) * (x - 3) + 2;
        System.out.println("\n【2】Поиск минимума функции f(x) = (x-3)²+2");
        System.out.println(goldenSection(f, 0, 6, 1e-8));

        // Тест 3: Сравнение производительности
        System.out.println("\n【3】Сравнение производительности (1000 запусков)");
        long binSum = 0, interpSum = 0, goldenSum = 0;
        for (int i = 0; i < 1000; i++) {
            binSum += binarySearch(arr, 500_000).timeNanos;
            interpSum += interpolationSearch(arr, 500_000).timeNanos;
            goldenSum += goldenSection(f, 0, 6, 1e-6).timeNanos;
        }
        System.out.printf("Бинарный поиск:         %8.2f нс (среднее)\n", binSum / 1000.0);
        System.out.printf("Интерполяционный поиск: %8.2f нс (среднее)\n", interpSum / 1000.0);
        System.out.printf("Золотое сечение:        %8.2f нс (среднее)\n", goldenSum / 1000.0);
    }
}