# 分治基础：归并排序代码补全与随机选择算法

这一篇把两个很常见的分治题型放在一起整理：

- `归并排序`：把用户给出的 `TODO` 代码补全
- `随机选择算法`：也叫 `QuickSelect`，用于在线性期望时间内求第 `k` 小 / 第 `k` 大

它们都属于 **分治思想**，但侧重点不同：

```text
归并排序：两边都递归，最后 merge
随机选择：划分后只进入一边，不做完整排序
```

---

## 1. 归并排序代码补全

### 完整代码

下面是把你给出的代码补全后的版本：

```java
class Solution {
    public int[] sortArray(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return nums;
        }

        int[] temp = new int[nums.length];
        mergeSort(nums, 0, nums.length - 1, temp);
        return nums;
    }

    private void mergeSort(int[] nums, int left, int right, int[] temp) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;

        mergeSort(nums, left, mid, temp);
        mergeSort(nums, mid + 1, right, temp);
        merge(nums, left, mid, right, temp);
    }

    private void merge(int[] nums, int left, int mid, int right, int[] temp) {
        int p1 = left;
        int p2 = mid + 1;
        int index = left;

        while (p1 <= mid && p2 <= right) {
            if (nums[p1] <= nums[p2]) {
                temp[index++] = nums[p1++];
            } else {
                temp[index++] = nums[p2++];
            }
        }

        while (p1 <= mid) {
            temp[index++] = nums[p1++];
        }

        while (p2 <= right) {
            temp[index++] = nums[p2++];
        }

        for (int i = left; i <= right; i++) {
            nums[i] = temp[i];
        }
    }
}
```

---

## 2. 归并排序核心思路

归并排序的核心是：

```text
先分，再治，最后合并
```

对区间 `[left, right]`：

1. 找到中点 `mid`
2. 递归让 `[left, mid]` 有序
3. 递归让 `[mid + 1, right]` 有序
4. 把两个有序区间合并

递归结构：

```text
mergeSort(left, right):
    if left >= right:
        return
    mid = left + (right - left) / 2
    mergeSort(left, mid)
    mergeSort(mid + 1, right)
    merge(left, mid, right)
```

### 为什么 `merge` 一定要最后调用

因为 `merge` 的前提是：

```text
左半部分已经有序
右半部分已经有序
```

如果左右两部分还没排好序，直接 merge 就没有意义。

---

## 3. merge 函数怎么写

合并两个有序区间：

```text
[left, mid]
[mid + 1, right]
```

常用两个指针：

- `p1` 指向左半区间当前元素
- `p2` 指向右半区间当前元素

每次把较小的元素放入 `temp`。

```text
如果 nums[p1] <= nums[p2]，拷贝左边
否则拷贝右边
```

一边耗尽后，把另一边剩余元素全部拷贝进去。

最后再把 `[left, right]` 这一段从 `temp` 复制回 `nums`。

---

## 4. 归并排序的易错点

### 1. 递归出口

```java
if (left >= right) {
    return;
}
```

这是必须的。  
否则递归不会停。

### 2. `mid` 的写法

```java
int mid = left + (right - left) / 2;
```

这样写比 `(left + right) / 2` 更稳，能避免整数溢出风险。

### 3. `temp` 不要在递归里重复创建

正确做法是在最外层创建一次：

```java
int[] temp = new int[nums.length];
```

然后一路传下去复用。  
否则会有很多不必要的空间开销。

### 4. 回写时只回写当前区间

```java
for (int i = left; i <= right; i++) {
    nums[i] = temp[i];
}
```

不是整个数组都复制，只复制当前 merge 的这一段。

---

## 5. 归并排序复杂度

```text
时间复杂度：O(n log n)
空间复杂度：O(n)
稳定性：稳定
```

稳定的原因是：

```java
if (nums[p1] <= nums[p2])
```

相等时优先拷贝左边元素，保持了原来的相对顺序。

---

## 6. 随机选择算法是什么

随机选择算法通常指 **QuickSelect**。

它解决的问题是：

```text
在无序数组中，快速找到第 k 小元素
或者第 k 大元素
```

例如：

- 第 `1` 小：最小值
- 第 `k` 小：排序后下标 `k - 1`
- 第 `k` 大：排序后下标 `n - k`

它和快速排序很像，都是基于 `partition`：

- 快速排序：左右两边都继续处理
- 随机选择：只进入包含目标下标的那一边

所以它平均更快。

---

## 7. 随机选择算法核心思路

假设要找排序后下标为 `index` 的数：

1. 随机选一个 `pivot`
2. 按 `pivot` 把数组划分成三段：

```text
< pivot
= pivot
> pivot
```

3. 看 `index` 落在哪一段

- 如果在左边，继续去左边找
- 如果在中间，直接返回
- 如果在右边，继续去右边找

因为每次只进入一边，所以平均复杂度是：

```text
O(n)
```

---

## 8. 随机选择算法 Java 模板

下面给一个常用写法：求数组中的第 `k` 大元素。

### 完整代码

```java
import java.util.Random;

class Solution {
    private final Random random = new Random();

    public int findKthLargest(int[] nums, int k) {
        int target = nums.length - k;
        return quickSelect(nums, 0, nums.length - 1, target);
    }

    private int quickSelect(int[] nums, int left, int right, int index) {
        while (left < right) {
            int pivot = nums[left + random.nextInt(right - left + 1)];
            int[] range = partition(nums, left, right, pivot);

            if (index < range[0]) {
                right = range[0] - 1;
            } else if (index > range[1]) {
                left = range[1] + 1;
            } else {
                return nums[index];
            }
        }
        return nums[left];
    }

    private int[] partition(int[] nums, int left, int right, int pivot) {
        int less = left - 1;
        int more = right + 1;
        int cur = left;

        while (cur < more) {
            if (nums[cur] < pivot) {
                swap(nums, ++less, cur++);
            } else if (nums[cur] > pivot) {
                swap(nums, --more, cur);
            } else {
                cur++;
            }
        }
        return new int[]{less + 1, more - 1};
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
```

---

## 9. 为什么随机选择平均是 O(n)

每一轮 `partition` 都是线性的：

```text
O(right - left + 1)
```

但它不像快排那样递归左右两边，而是：

```text
只递归一边
```

如果随机枢轴分得比较均匀，问题规模会快速缩小，所以平均复杂度是：

```text
O(n)
```

最坏情况下仍然可能退化到：

```text
O(n^2)
```

所以：

- 随机选择：平均 `O(n)`
- BFPRT：最坏 `O(n)`，但实现更复杂

---

## 10. 随机选择算法的易错点

### 1. 第 k 大和第 k 小不要搞混

如果是：

```text
第 k 小
```

目标下标就是：

```java
k - 1
```

如果是：

```text
第 k 大
```

目标下标就是：

```java
nums.length - k
```

### 2. 要用三路划分

推荐直接写成：

```text
< pivot
= pivot
> pivot
```

这样遇到大量重复元素也更稳。

### 3. `more` 指针交换后不要立刻 `cur++`

```java
swap(nums, --more, cur);
```

这里不能 `cur++`，因为换过来的元素还没检查。

### 4. 随机 pivot 才是“随机选择”

```java
int pivot = nums[left + random.nextInt(right - left + 1)];
```

如果固定选某个位置，在特定数据下容易退化。

---

## 11. 归并排序 vs 随机选择

| 算法 | 目标 | 是否完整排序 | 平均复杂度 | 空间复杂度 |
| :--- | :--- | :--- | :--- | :--- |
| 归并排序 | 把整个数组排好序 | 是 | `O(n log n)` | `O(n)` |
| 随机选择 | 找某个排名的元素 | 否 | `O(n)` | `O(1)` |

记忆方式：

```text
归并排序：为了整体有序
随机选择：为了只找一个位置上的答案
```

---

## 12. 复习口诀

```text
归并排序先分后合，两边都排好再 merge。
随机选择先划分，只去目标下标所在的一边。
```
