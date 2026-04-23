# 分治基础：随机快排与随机选择算法

这一篇整理两个非常相关的算法：

- `随机快排`：对整个数组排序
- `随机选择算法`：也叫 `QuickSelect`，只找某个排名的元素

它们的共同基础都是：

```text
随机选 pivot + 三路划分（< = >）
```

区别在于：

```text
随机快排：左右两边都递归
随机选择：只进入目标下标所在的一边
```

---

## 1. 随机快排代码

下面是你给的代码，按笔记形式整理后保留：

```java
class Solution {

    // 随机快排
    public int[] sortArray(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return nums;
        }
        quickSort(nums, 0, nums.length - 1);
        return nums;
    }

    public void quickSort(int[] a, int l, int r) {
        if (l >= r) {
            return;
        }

        // 随机选取索引
        int x = l + (int) (Math.random() * (r - l + 1));
        int[] pos = partition(a, l, r, x);
        quickSort(a, l, pos[0] - 1);
        quickSort(a, pos[1] + 1, r);
    }

    // x 表示选取的索引
    private int[] partition(int[] arr, int l, int r, int x) {
        int less = l - 1;
        int more = r + 1;
        int pivot = arr[x];

        for (int i = l; i < more; ) {
            if (arr[i] < pivot) {
                swap(arr, i++, ++less);
            } else if (arr[i] == pivot) {
                i++;
            } else {
                swap(arr, i, --more);
            }
        }
        return new int[]{less + 1, more - 1};
    }

    public static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
```

---

## 2. 随机快排的核心思路

快速排序的核心不是“找中点”，而是：

```text
选一个基准值 pivot，把数组按 pivot 划分
```

划分后数组会变成三部分：

```text
< pivot
= pivot
> pivot
```

然后：

- 左边继续排
- 右边继续排
- 中间这一段已经不用再管

所以递归结构是：

```text
quickSort(l, r):
    if l >= r:
        return
    随机选一个 pivot
    partition 后得到等于区间 [equalL, equalR]
    quickSort(l, equalL - 1)
    quickSort(equalR + 1, r)
```

---

## 3. 为什么要随机选 pivot

如果总是固定选：

- 最左边
- 最右边
- 或者某个固定位置

那么在某些特殊数据下，快速排序会退化成：

```text
O(n^2)
```

随机选 pivot 的目的就是尽量避免这种退化，让期望复杂度保持在：

```text
O(n log n)
```

代码里这一句就是随机选 pivot：

```java
int x = l + (int) (Math.random() * (r - l + 1));
```

注意这里选的是 **索引**，不是值。

---

## 4. 三路划分怎么理解

这一段代码本质上是 **荷兰国旗问题**：

```java
private int[] partition(int[] arr, int l, int r, int x)
```

定义三个区域：

- `[l, less]`：小于 pivot
- `[less + 1, i - 1]`：等于 pivot
- `[more, r]`：大于 pivot

当前正在检查的位置是 `i`。

循环过程中：

### 1. `arr[i] < pivot`

说明当前数应该放到左边“小于区”：

```java
swap(arr, i++, ++less);
```

### 2. `arr[i] == pivot`

说明它属于中间“等于区”：

```java
i++;
```

### 3. `arr[i] > pivot`

说明它应该放到右边“大于区”：

```java
swap(arr, i, --more);
```

这里 **不能立刻 `i++`**，因为换过来的数还没检查。

最后返回：

```java
new int[]{less + 1, more - 1}
```

表示“等于 pivot”这段区间。

---

## 5. 随机快排的易错点

### 1. 递归边界必须写对

```java
if (l >= r) {
    return;
}
```

否则递归不会停。

### 2. `partition` 返回的是等于区间

不是返回 pivot 最终的单个位置，而是：

```text
[equalL, equalR]
```

因为数组里可能有多个元素都等于 pivot。

### 3. 大于区交换后不能 `i++`

```java
swap(arr, i, --more);
```

交换过来的值还没判断，所以 `i` 不能前进。

### 4. 空数组和单元素数组最好直接返回

```java
if (nums == null || nums.length <= 1) {
    return nums;
}
```

虽然很多在线评测不会传 `null`，但整理成模板时加上更稳。

---

## 6. 随机快排复杂度

```text
期望时间复杂度：O(n log n)
最坏时间复杂度：O(n^2)
空间复杂度：O(log n) ~ O(n)
```

说明：

- 期望 `O(n log n)` 是因为随机 pivot 通常能把问题划分得比较均匀。
- 最坏仍可能退化。
- 空间复杂度主要来自递归栈。

---

## 7. 随机选择算法是什么

随机选择算法就是：

```text
不把整个数组排好序，只找第 k 小 / 第 k 大
```

最常见题型：

- 求第 `k` 小元素
- 求第 `k` 大元素
- 求数组中某个排名位置的值

它和随机快排几乎是同一个框架，只是递归策略不同：

- 快排：左右两边都处理
- 随机选择：只去目标下标所在的一边

---

## 8. 随机选择算法 Java 模板

下面给出一个常见模板：求第 `k` 大元素。

```java
class Solution {

    public int findKthLargest(int[] nums, int k) {
        int target = nums.length - k;
        return quickSelect(nums, 0, nums.length - 1, target);
    }

    private int quickSelect(int[] arr, int l, int r, int index) {
        while (l < r) {
            int x = l + (int) (Math.random() * (r - l + 1));
            int[] range = partition(arr, l, r, x);

            if (index < range[0]) {
                r = range[0] - 1;
            } else if (index > range[1]) {
                l = range[1] + 1;
            } else {
                return arr[index];
            }
        }
        return arr[l];
    }

    private int[] partition(int[] arr, int l, int r, int x) {
        int less = l - 1;
        int more = r + 1;
        int pivot = arr[x];

        for (int i = l; i < more; ) {
            if (arr[i] < pivot) {
                swap(arr, i++, ++less);
            } else if (arr[i] == pivot) {
                i++;
            } else {
                swap(arr, i, --more);
            }
        }
        return new int[]{less + 1, more - 1};
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```

---

## 9. 随机选择算法怎么理解

假设目标下标是 `index`。

做完一次 `partition` 后，会得到：

```text
[l ... equalL - 1]   < pivot
[equalL ... equalR]  = pivot
[equalR + 1 ... r]   > pivot
```

这时分三种情况：

### 1. `index < equalL`

目标在左边，只去左边继续找。

### 2. `index > equalR`

目标在右边，只去右边继续找。

### 3. `equalL <= index <= equalR`

目标就在等于区，直接返回。

这就是它比完整排序更快的原因：

```text
每轮只进入一边
```

---

## 10. 第 k 大和第 k 小怎么转换

如果数组升序排序后：

- 第 `1` 小的下标是 `0`
- 第 `k` 小的下标是 `k - 1`

而第 `k` 大的下标是：

```java
nums.length - k
```

所以在代码里：

```java
int target = nums.length - k;
```

然后去找排序后下标为 `target` 的元素即可。

---

## 11. 随机选择的易错点

### 1. 不要真的先排序

随机选择的意义就是：

```text
不用完整排序，直接找答案
```

如果先排完再取第 `k` 个，就失去这个算法的意义了。

### 2. `partition` 可以和快排共用

随机快排和随机选择最大的复用点就是：

```java
partition(arr, l, r, x)
```

这也是为什么这两个算法很适合一起记。

### 3. 大量重复元素时三路划分更稳

如果只写普通双路 partition，在重复元素很多时效果会差很多。  
三路划分会更稳，也更容易和随机选择结合。

---

## 12. 随机快排 vs 随机选择

| 算法 | 目标 | 是否完整排序 | 期望复杂度 |
| :--- | :--- | :--- | :--- |
| 随机快排 | 整体排序 | 是 | `O(n log n)` |
| 随机选择 | 找某个排名位置 | 否 | `O(n)` |

一句话记忆：

```text
快排是“都要”，左右都递归。
选择是“只要一个”，只进目标那边。
```

---

## 13. 复习口诀

```text
随机快排看三路划分，小于左边，大于右边，等于中间。
随机选择不全排，只去目标下标所在区间。
```
