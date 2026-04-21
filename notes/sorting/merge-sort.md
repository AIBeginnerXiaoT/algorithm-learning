# 归并排序：递归版与非递归版

归并排序（Merge Sort）是非常经典的 `O(n log n)` 排序算法。它的核心思想是：

```text
先把数组拆小，再把有序的小数组合并成更大的有序数组。
```

它有两种常见写法：

- **递归版**：自顶向下，把大区间不断拆成小区间。
- **非递归版**：自底向上，先合并长度为 `1` 的小段，再合并长度为 `2`、`4`、`8` 的小段。

---

## 1. 算法核心思想

假设要排序区间 `[l, r]`：

1. 找到中点 `mid`。
2. 先让 `[l, mid]` 有序。
3. 再让 `[mid + 1, r]` 有序。
4. 最后把两个有序区间合并成一个有序区间。

合并两个有序区间时，用两个指针分别指向左右两部分的开头：

```text
左半区间：[l, mid]
右半区间：[mid + 1, r]
```

每次把较小的数放进辅助数组 `tmp`，直到某一边耗尽，再把另一边剩余元素全部拷贝过去。

---

## 2. 递归版归并排序

递归版就是“先拆，再合并”。

```text
mergeSort(l, r):
    如果 l >= r，说明区间只有 0 或 1 个数，天然有序，直接返回
    mid = l + ((r - l) >> 1)
    mergeSort(l, mid)
    mergeSort(mid + 1, r)
    merge(l, mid, r)
```

递归版的关键是：

- `l >= r` 是递归出口。
- `mid = l + ((r - l) >> 1)` 可以避免 `l + r` 溢出。
- 左右两边都排好序之后，才能调用 `merge`。

---

## 3. 非递归版归并排序

非递归版也叫 **自底向上归并排序**。

它不使用递归，而是用 `step` 表示当前正在合并的有序段长度：

```text
step = 1：每 1 个数一组，两两合并
step = 2：每 2 个数一组，两两合并
step = 4：每 4 个数一组，两两合并
step = 8：每 8 个数一组，两两合并
...
```

每一轮合并完成后，`step` 翻倍。

例如数组长度为 `8`：

```text
step = 1: [0] [1] 合并，[2] [3] 合并，[4] [5] 合并，[6] [7] 合并
step = 2: [0..1] [2..3] 合并，[4..5] [6..7] 合并
step = 4: [0..3] [4..7] 合并
```

非递归版的关键是边界：

- `left` 是当前左组开头。
- `mid = left + step - 1` 是左组结尾。
- `right = min(left + 2 * step - 1, n - 1)` 是右组结尾。
- 如果 `mid + 1 >= n`，说明右组不存在，不需要合并。

---

## 4. Java 模板代码

下面代码包含递归版和非递归版，后续复习直接看这一份即可。

```java
import java.util.Arrays;

public class MergeSort {
    private int[] arr;
    private int[] help;

    public int[] sortArrayRecursive(int[] nums) {
        if (nums == null || nums.length < 2) {
            return nums;
        }
        arr = nums;
        help = new int[nums.length];
        mergeSort(0, nums.length - 1);
        return nums;
    }

    public int[] sortArrayIterative(int[] nums) {
        if (nums == null || nums.length < 2) {
            return nums;
        }
        arr = nums;
        help = new int[nums.length];
        mergeSortIterative(nums.length);
        return nums;
    }

    private void mergeSort(int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = left + ((right - left) >> 1);
        mergeSort(left, mid);
        mergeSort(mid + 1, right);
        merge(left, mid, right);
    }

    private void mergeSortIterative(int n) {
        for (int step = 1; step < n; ) {
            int left = 0;
            while (left < n) {
                int mid = left + step - 1;
                if (mid + 1 >= n) {
                    break;
                }
                int right = Math.min(left + 2 * step - 1, n - 1);
                merge(left, mid, right);
                left = right + 1;
            }

            if (step > n / 2) {
                break;
            }
            step <<= 1;
        }
    }

    private void merge(int left, int mid, int right) {
        int p1 = left;
        int p2 = mid + 1;
        int index = left;

        while (p1 <= mid && p2 <= right) {
            help[index++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= mid) {
            help[index++] = arr[p1++];
        }
        while (p2 <= right) {
            help[index++] = arr[p2++];
        }
        for (int i = left; i <= right; i++) {
            arr[i] = help[i];
        }
    }

    public static void main(String[] args) {
        int[] nums1 = {5, 2, 3, 1, 4};
        int[] nums2 = nums1.clone();

        MergeSort sorter = new MergeSort();
        System.out.println(Arrays.toString(sorter.sortArrayRecursive(nums1)));
        System.out.println(Arrays.toString(sorter.sortArrayIterative(nums2)));
    }
}
```

---

## 5. 复杂度

### 时间复杂度

```text
O(n log n)
```

原因：

- 每一层合并的总代价是 `O(n)`。
- 一共有 `log n` 层。

### 空间复杂度

```text
O(n)
```

原因是需要一个辅助数组 `help` 来完成合并。

---

## 6. 稳定性

归并排序是稳定排序，但前提是合并时遇到相等元素，优先拷贝左边：

```java
help[index++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
```

这里使用 `<=`，所以相等元素会保持原来的相对顺序。

如果写成 `<`，相等时右边元素会先进入辅助数组，稳定性就被破坏了。

---

## 7. 易错点总结

### 1. `mid` 不要写成 `(left + right) / 2`

推荐写法：

```java
int mid = left + ((right - left) >> 1);
```

这样可以避免 `left + right` 过大导致整数溢出。

### 2. 非递归版要判断右组是否存在

如果：

```java
mid + 1 >= n
```

说明只有左组，没有右组，不需要合并。

### 3. `right` 不能越界

非递归版最后一组可能不完整，所以要写：

```java
int right = Math.min(left + 2 * step - 1, n - 1);
```

### 4. 辅助数组要按原区间拷贝回去

归并的是 `[left, right]`，最后也只需要把这个区间拷贝回原数组：

```java
for (int i = left; i <= right; i++) {
    arr[i] = help[i];
}
```

### 5. `step` 翻倍要防止溢出

非递归版里 `step` 每轮翻倍。为了避免极端情况下 `step <<= 1` 溢出，模板里加了：

```java
if (step > n / 2) {
    break;
}
```

---

## 8. 递归版和非递归版怎么选

- 平时刷题：递归版更直观，更容易写对。
- 要避免递归栈：使用非递归版。
- 两者时间复杂度一样，都是 `O(n log n)`。
- 两者都需要辅助数组，空间复杂度都是 `O(n)`。

复习时只要记住一句话：

```text
递归版是从大到小拆，非递归版是从小到大合。
```
