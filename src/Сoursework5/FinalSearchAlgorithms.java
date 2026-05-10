package Сoursework5;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
public final class FinalSearchAlgorithms {

    // ==================== КОНСТАНТЫ ====================

    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double INV_PHI = 1 / PHI;
    private static final int HYBRID_THRESHOLD = 100;

    // ==================== 1. БИНАРНЫЙ ПОИСК ====================

    /**
     * Итеративный бинарный поиск
     */
    public static Result binarySearch(int[] arr, int target) {
        long start = System.nanoTime();
        int left = 0, right = arr.length - 1;
        int iterations = 0;

        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) {
                return new Result("Бинарный (итер)", mid, iterations, System.nanoTime() - start);
            }
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return new Result("Бинарный (итер)", -1, iterations, System.nanoTime() - start);
    }

    /**
     * Рекурсивный бинарный поиск
     */
    public static Result binarySearchRecursive(int[] arr, int target) {
        long start = System.nanoTime();
        Counter counter = new Counter();
        int idx = binSearchRec(arr, target, 0, arr.length - 1, counter);
        return new Result("Бинарный (рек)", idx, counter.get(), System.nanoTime() - start);
    }

    private static int binSearchRec(int[] arr, int target, int l, int r, Counter c) {
        c.inc();
        if (l > r) return -1;
        int mid = l + (r - l) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) return binSearchRec(arr, target, mid + 1, r, c);
        return binSearchRec(arr, target, l, mid - 1, c);
    }

    // ==================== 2. ИНТЕРПОЛЯЦИОННЫЙ ПОИСК (ГИБРИДНЫЙ) ====================

    /**
     * Гибридный интерполяционный поиск с переключением на бинарный
     */
    public static Result interpolationSearch(int[] arr, int target) {
        long start = System.nanoTime();
        int left = 0, right = arr.length - 1;
        int iterations = 0;

        while (left <= right && target >= arr[left] && target <= arr[right]) {
            iterations++;

            // При малом интервале переключаемся на бинарный поиск
            if (right - left < HYBRID_THRESHOLD) {
                Result r = binarySearchSegment(arr, target, left, right);
                return new Result("Интерполяц.(гибрид)", r.index, iterations + r.iterations,
                        System.nanoTime() - start);
            }

            if (arr[left] == arr[right]) break;

            int pos = left + ((target - arr[left]) * (right - left)) / (arr[right] - arr[left]);
            if (pos < left || pos > right) break;

            if (arr[pos] == target) {
                return new Result("Интерполяц.(гибрид)", pos, iterations, System.nanoTime() - start);
            }
            if (arr[pos] < target) left = pos + 1;
            else right = pos - 1;
        }
        return new Result("Интерполяц.(гибрид)", -1, iterations, System.nanoTime() - start);
    }

    private static Result binarySearchSegment(int[] arr, int target, int left, int right) {
        int iterations = 0;
        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return new Result("", mid, iterations, 0);
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return new Result("", -1, iterations, 0);
    }

    // ==================== 3. МЕТОД ЗОЛОТОГО СЕЧЕНИЯ ====================

    public static OptimizationResult goldenSection(DoubleUnaryOperator f, double a, double b, double eps) {
        long start = System.nanoTime();
        int iter = 0;
        double x1 = b - (b - a) * INV_PHI;
        double x2 = a + (b - a) * INV_PHI;
        double f1 = f.applyAsDouble(x1);
        double f2 = f.applyAsDouble(x2);

        while ((b - a) > eps) {
            iter++;
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
        double minPoint = (a + b) / 2;
        return new OptimizationResult("Золотое сечение", minPoint, f.applyAsDouble(minPoint),
                iter, System.nanoTime() - start);
    }

    // ==================== 4. МЕТОД ФИБОНАЧЧИ ====================

    public static OptimizationResult fibonacci(DoubleUnaryOperator f, double a, double b, int n) {
        long start = System.nanoTime();
        long[] fib = new long[n + 2];
        fib[0] = 1; fib[1] = 1;
        for (int i = 2; i <= n + 1; i++) fib[i] = fib[i-1] + fib[i-2];

        double x1 = a + (double) fib[n-1] / fib[n+1] * (b - a);
        double x2 = a + (double) fib[n] / fib[n+1] * (b - a);
        double f1 = f.applyAsDouble(x1);
        double f2 = f.applyAsDouble(x2);

        for (int k = 1; k <= n; k++) {
            if (f1 < f2) {
                b = x2;
                x2 = x1;
                f2 = f1;
                if (k <= n-1) {
                    x1 = a + (double) fib[n-k-1] / fib[n-k+1] * (b - a);
                    f1 = f.applyAsDouble(x1);
                }
            } else {
                a = x1;
                x1 = x2;
                f1 = f2;
                if (k <= n-1) {
                    x2 = a + (double) fib[n-k] / fib[n-k+1] * (b - a);
                    f2 = f.applyAsDouble(x2);
                }
            }
        }
        double minPoint = (a + b) / 2;
        return new OptimizationResult("Фибоначчи", minPoint, f.applyAsDouble(minPoint),
                n, System.nanoTime() - start);
    }

    // ==================== 5. ТЕРНАРНЫЙ ПОИСК ====================

    public static OptimizationResult ternary(DoubleUnaryOperator f, double a, double b, double eps) {
        long start = System.nanoTime();
        int iter = 0;
        while ((b - a) > eps) {
            iter++;
            double m1 = a + (b - a) / 3;
            double m2 = b - (b - a) / 3;
            double f1 = f.applyAsDouble(m1);
            double f2 = f.applyAsDouble(m2);
            if (f1 < f2) b = m2;
            else a = m1;
        }
        double minPoint = (a + b) / 2;
        return new OptimizationResult("Тернарный", minPoint, f.applyAsDouble(minPoint),
                iter, System.nanoTime() - start);
    }

    // ==================== ДОПОЛНИТЕЛЬНО: ПОИСК В МАТРИЦЕ ====================

    /**
     * Поиск элемента в отсортированной по строкам и столбцам матрице
     * Алгоритм "лестницы" - O(m+n)
     */
    public static int[] searchInMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) return new int[]{-1, -1};
        int rows = matrix.length, cols = matrix[0].length;
        int row = 0, col = cols - 1;
        int iterations = 0;

        while (row < rows && col >= 0) {
            iterations++;
            if (matrix[row][col] == target) {
                return new int[]{row, col, iterations};
            } else if (matrix[row][col] < target) {
                row++;
            } else {
                col--;
            }
        }
        return new int[]{-1, -1, iterations};
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ====================

    static class Counter {
        private int val = 0;
        void inc() { val++; }
        int get() { return val; }
        void reset() { val = 0; }
    }

    static class Result {
        String name; int index; int iterations; long timeNanos;
        Result(String n, int idx, int iter, long t) {
            name = n; index = idx; iterations = iter; timeNanos = t;
        }
        @Override
        public String toString() {
            return String.format("%-25s | индекс: %-6d | итераций: %-6d | время: %8d нс",
                    name, index, iterations, timeNanos);
        }
    }

    static class OptimizationResult {
        String name; double point; double value; int iterations; long timeNanos;
        OptimizationResult(String n, double p, double v, int iter, long t) {
            name = n; point = p; value = v; iterations = iter; timeNanos = t;
        }
        @Override
        public String toString() {
            return String.format("%-15s | точка: %12.10f | значение: %12.10f | итераций: %4d | время: %8d нс",
                    name, point, value, iterations, timeNanos);
        }
    }

    // ==================== ТЕСТОВЫЕ ФУНКЦИИ ====================

    static class QuadFunc implements DoubleUnaryOperator {
        public double applyAsDouble(double x) { return (x-3)*(x-3) + 2; }
        public String toString() { return "f(x) = (x-3)²+2"; }
    }

    static class CubicFunc implements DoubleUnaryOperator {
        public double applyAsDouble(double x) { return x*x*x - 6*x*x + 9*x + 2; }
        public String toString() { return "f(x) = x³-6x²+9x+2"; }
    }

    // ==================== ОСНОВНОЙ МЕТОД ДЛЯ ДЕМОНСТРАЦИИ ====================

    public static void main(String[] args) {
        System.out.println("=".repeat(90));
        System.out.println("КУРСОВАЯ РАБОТА");
        System.out.println("Сравнительный анализ алгоритмов поиска");
        System.out.println("=".repeat(90));

        // 1. Поиск в массиве
        System.out.println("\n【1】ПОИСК В МАССИВЕ");
        int[] uniform = new int[1_000_000];
        for (int i = 0; i < uniform.length; i++) uniform[i] = i;

        System.out.println(binarySearch(uniform, 500_000));
        System.out.println(binarySearchRecursive(uniform, 500_000));
        System.out.println(interpolationSearch(uniform, 500_000));

        // 2. Поиск минимума функции
        System.out.println("\n【2】ПОИСК МИНИМУМА ФУНКЦИИ");
        QuadFunc f = new QuadFunc();
        System.out.println(goldenSection(f, 0, 6, 1e-8));
        System.out.println(fibonacci(f, 0, 6, 40));
        System.out.println(ternary(f, 0, 6, 1e-8));

        // 3. Поиск в матрице
        System.out.println("\n【3】ПОИСК В ОТСОРТИРОВАННОЙ МАТРИЦЕ");
        int[][] matrix = {
                {1, 4, 7, 11, 15},
                {2, 5, 8, 12, 19},
                {3, 6, 9, 16, 22},
                {10, 13, 14, 17, 24},
                {18, 21, 23, 26, 30}
        };
        int[] res = searchInMatrix(matrix, 14);
        System.out.printf("Элемент 14 найден на позиции [%d, %d] за %d итераций\n", res[0], res[1], res[2]);

        // 4. Сравнение производительности (1000 запусков)
        System.out.println("\n【4】СРАВНЕНИЕ ПРОИЗВОДИТЕЛЬНОСТИ (1000 запусков)");
        int runs = 1000;
        long totalBin = 0, totalInterp = 0, totalGolden = 0, totalFib = 0;
        for (int i = 0; i < runs; i++) {
            totalBin += binarySearch(uniform, 500_000).timeNanos;
            totalInterp += interpolationSearch(uniform, 500_000).timeNanos;
            totalGolden += goldenSection(f, 0, 6, 1e-6).timeNanos;
            totalFib += fibonacci(f, 0, 6, 30).timeNanos;
        }
        System.out.printf("Бинарный поиск:          %8.2f нс (среднее)\n", totalBin / (double) runs);
        System.out.printf("Интерполяционный поиск:  %8.2f нс (среднее)\n", totalInterp / (double) runs);
        System.out.printf("Золотое сечение:         %8.2f нс (среднее)\n", totalGolden / (double) runs);
        System.out.printf("Фибоначчи:               %8.2f нс (среднее)\n", totalFib / (double) runs);

        // 5. Анализ сходимости
        System.out.println("\n【5】АНАЛИЗ СХОДИМОСТИ (уменьшение интервала)");
        System.out.println("Итерация | Золотое сечение | Фибоначчи | Тернарный");
        double len = 6.0;
        for (int i = 0; i <= 10; i++) {
            double g = len * Math.pow(INV_PHI, i);
            double t = len * Math.pow(2.0/3.0, i);
            double fLen = len / Math.pow(PHI, i);
            System.out.printf("%8d | %14.6f | %9.6f | %9.6f\n", i, g, fLen, t);
        }

        System.out.println("\n=== ВЫВОДЫ ПО РЕЗУЛЬТАТАМ ===");
        System.out.println("1. Интерполяционный поиск на равномерных данных значительно быстрее бинарного.");
        System.out.println("2. Метод золотого сечения и Фибоначчи дают близкие результаты, но Фибоначчи оптимален при фиксированном числе итераций.");
        System.out.println("3. Тернарный поиск уступает методу золотого сечения по скорости сходимости.");
        System.out.println("4. Поиск в матрице алгоритмом 'лестницы' имеет сложность O(m+n).");
    }
}
