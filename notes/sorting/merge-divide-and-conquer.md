# 归并分治统计题：逆序对、小和、翻转对

这一类题的共同点是：

```text
不是单纯排序，而是在归并排序的 merge 过程中顺手统计答案。
```

经典题目：

- `LCR 170. 交易逆序对的总数`
- `计算数组的小和`
- `LeetCode 493. 翻转对`

它们都可以归到 **归并分治统计题** 这一类。

---

## 1. 总套路

归并分治统计题通常分三步：

```text
1. 左半部分内部产生的答案
2. 右半部分内部产生的答案
3. 左半部分和右半部分之间产生的答案
```

递归结构固定：

```text
process(l, r):
    如果 l == r，返回 0
    mid = l + ((r - l) >> 1)
    leftAns = process(l, mid)
    rightAns = process(mid + 1, r)
    mergeAns = merge 或 merge 前统计
    返回 leftAns + rightAns + mergeAns
```

关键点：

- 递归处理完后，左半区间 `[l, mid]` 已经有序。
- 右半区间 `[mid + 1, r]` 也已经有序。
- 利用“左右两边有序”这个性质，可以快速统计跨左右区间的答案。

---

## 2. 题型总览

| 题目 | 统计目标 | 核心判断 | 统计时机 |
| :--- | :--- | :--- | :--- |
| LCR 170. 交易逆序对的总数 | `i < j` 且 `arr[i] > arr[j]` 的数量 | 左边当前数 `>` 右边当前数 | merge 时统计 |
| 计算数组的小和 | 每个数左边比它小的数之和 | 左边当前数 `<` 右边当前数 | merge 时统计 |
| 493. 翻转对 | `i < j` 且 `arr[i] > 2 * arr[j]` 的数量 | 左边数 `> 2 * 右边数` | merge 前窗口统计 |

一句话区分：

```text
逆序对：左边大，数数量
小和：左边小，算贡献
翻转对：左边大于右边两倍，先统计再归并
```

---

## 3. LCR 170. 交易逆序对的总数

### 题意

给定一个数组 `record`，统计满足下面条件的二元组数量：

```text
i < j 且 record[i] > record[j]
```

这就是常说的 **逆序对**。

### 为什么能用归并统计

假设左右两边已经有序：

```text
左边：[l, mid]
右边：[mid + 1, r]
```

merge 时，如果：

```text
arr[p1] > arr[p2]
```

因为左半部分已经有序，所以：

```text
arr[p1], arr[p1 + 1], ..., arr[mid]
```

都大于 `arr[p2]`。

于是一次性贡献：

```text
mid - p1 + 1
```

这就是归并统计比暴力枚举快的原因。

### Java 代码

```java
public class LCR170ReversePairs {
    private int[] arr;
    private int[] help;

    public int reversePairs(int[] record) {
        if (record == null || record.length < 2) {
            return 0;
        }
        arr = record;
        help = new int[record.length];
        return (int) process(0, record.length - 1);
    }

    private long process(int left, int right) {
        if (left >= right) {
            return 0;
        }
        int mid = left + ((right - left) >> 1);
        return process(left, mid)
                + process(mid + 1, right)
                + merge(left, mid, right);
    }

    private long merge(int left, int mid, int right) {
        int p1 = left;
        int p2 = mid + 1;
        int index = left;
        long ans = 0;

        while (p1 <= mid && p2 <= right) {
            if (arr[p1] <= arr[p2]) {
                help[index++] = arr[p1++];
            } else {
                ans += mid - p1 + 1;
                help[index++] = arr[p2++];
            }
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
        return ans;
    }
}
```

---

## 4. 计算数组的小和

### 题意

数组小和的定义：

```text
每个数左边所有比它小的数累加起来，得到总和。
```

例如：

```text
arr = [1, 3, 4, 2, 5]
```

每个位置的小和贡献：

```text
1 左边没有数，贡献 0
3 左边比它小的是 1，贡献 1
4 左边比它小的是 1、3，贡献 4
2 左边比它小的是 1，贡献 1
5 左边比它小的是 1、3、4、2，贡献 10
```

总小和：

```text
0 + 1 + 4 + 1 + 10 = 16
```

### 换个角度看贡献

不要站在右边的数角度看：

```text
我左边有哪些数比我小？
```

而是站在左边的数角度看：

```text
我右边有多少个数比我大？
```

如果 merge 时：

```text
arr[p1] < arr[p2]
```

因为右半部分已经有序，所以：

```text
arr[p2], arr[p2 + 1], ..., arr[right]
```

都大于 `arr[p1]`。

于是 `arr[p1]` 会产生：

```text
arr[p1] * (right - p2 + 1)
```

的贡献。

### Java 代码

```java
public class SmallSum {
    private int[] arr;
    private int[] help;

    public long smallSum(int[] nums) {
        if (nums == null || nums.length < 2) {
            return 0;
        }
        arr = nums;
        help = new int[nums.length];
        return process(0, nums.length - 1);
    }

    private long process(int left, int right) {
        if (left >= right) {
            return 0;
        }
        int mid = left + ((right - left) >> 1);
        return process(left, mid)
                + process(mid + 1, right)
                + merge(left, mid, right);
    }

    private long merge(int left, int mid, int right) {
        int p1 = left;
        int p2 = mid + 1;
        int index = left;
        long ans = 0;

        while (p1 <= mid && p2 <= right) {
            if (arr[p1] < arr[p2]) {
                ans += (long) arr[p1] * (right - p2 + 1);
                help[index++] = arr[p1++];
            } else {
                help[index++] = arr[p2++];
            }
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
        return ans;
    }
}
```

### 小和题的易错点

这里必须是严格小于：

```java
arr[p1] < arr[p2]
```

如果两个数相等，左边的数不能给右边的数贡献小和。

---

## 5. LeetCode 493. 翻转对

### 题意

给定数组 `nums`，统计满足下面条件的二元组数量：

```text
i < j 且 nums[i] > 2 * nums[j]
```

注意它不是普通逆序对。  
普通逆序对只要求：

```text
nums[i] > nums[j]
```

翻转对要求：

```text
nums[i] > 2 * nums[j]
```

条件更强。

### 为什么不能直接在 merge 比大小时统计

merge 的比较逻辑是：

```text
arr[p1] <= arr[p2]
```

但翻转对的判断逻辑是：

```text
arr[p1] > 2 * arr[p2]
```

这两个逻辑不是一回事。

所以更稳的写法是：

```text
先统计翻转对，再正常 merge。
```

### 统计方式

左右两边已经有序：

```text
左边：[left, mid]
右边：[mid + 1, right]
```

对左边每个 `i`，在右边找最多有多少个数满足：

```text
arr[i] > 2 * arr[j]
```

因为左右两边有序，右边指针 `windowR` 只会向右移动，不需要回退。

```text
for i in [left, mid]:
    while windowR <= right && arr[i] > 2 * arr[windowR]:
        windowR++
    ans += windowR - (mid + 1)
```

### Java 代码

```java
public class LeetCode493ReversePairs {
    private int[] arr;
    private int[] help;

    public int reversePairs(int[] nums) {
        if (nums == null || nums.length < 2) {
            return 0;
        }
        arr = nums;
        help = new int[nums.length];
        return (int) process(0, nums.length - 1);
    }

    private long process(int left, int right) {
        if (left >= right) {
            return 0;
        }
        int mid = left + ((right - left) >> 1);
        return process(left, mid)
                + process(mid + 1, right)
                + countAndMerge(left, mid, right);
    }

    private long countAndMerge(int left, int mid, int right) {
        long ans = 0;

        int windowR = mid + 1;
        for (int i = left; i <= mid; i++) {
            while (windowR <= right && (long) arr[i] > 2L * arr[windowR]) {
                windowR++;
            }
            ans += windowR - (mid + 1);
        }

        merge(left, mid, right);
        return ans;
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
}
```

### 翻转对的易错点

一定要转 `long`：

```java
(long) arr[i] > 2L * arr[windowR]
```

原因是 `2 * arr[windowR]` 可能溢出 `int`。

---

## 6. 三道题对比记忆

### 逆序对

```text
arr[p1] > arr[p2]
```

右边当前数比左边当前数小，说明左边剩下的数都能和它组成逆序对。

贡献：

```text
mid - p1 + 1
```

### 小和

```text
arr[p1] < arr[p2]
```

左边当前数比右边当前数小，说明它比右边剩下所有数都小。

贡献：

```text
arr[p1] * (right - p2 + 1)
```

### 翻转对

```text
arr[i] > 2 * arr[j]
```

先用窗口统计数量，再正常归并。

贡献：

```text
windowR - (mid + 1)
```

---

## 7. 统一模板思维

归并分治统计题的核心不是背代码，而是记住这个流程：

```text
1. 递归让左边有序
2. 递归让右边有序
3. 利用左右有序统计跨区间答案
4. merge，让当前区间整体有序
```

如果统计逻辑和 merge 的大小比较一致，就可以在 merge 过程中统计。  
如果统计逻辑和 merge 的大小比较不一致，就先统计，再 merge。

对应到三道题：

```text
逆序对：merge 中统计
小和：merge 中统计
翻转对：merge 前统计，merge 只负责排序
```

---

## 8. 复杂度

三道题复杂度相同：

```text
时间复杂度：O(n log n)
空间复杂度：O(n)
```

原因：

- 归并排序一共有 `log n` 层。
- 每一层的统计和 merge 总共都是 `O(n)`。
- 辅助数组需要 `O(n)` 空间。

---

## 9. 复习口诀

```text
归并统计看左右有序。
逆序对：右边小，左边剩下都算。
小和：左边小，右边剩下都贡献。
翻转对：大于两倍，先滑窗口再归并。
```
