# 堆排序：大根堆、向上调整与向下调整

堆排序（Heap Sort）是一种基于 **堆结构** 的排序算法。

如果要把数组升序排列，常用做法是：

```text
先建立大根堆，每次把堆顶最大值放到数组末尾。
```

大根堆满足：

```text
每个父节点的值都 >= 它的子节点
```

---

## 1. 完整代码

下面是根据你给的代码整理后的版本。主要调整点是：在 `heapSort` 开始时重置 `size = 0`，避免多次调用 `sortArray` 时堆大小残留。

```java
class Solution {

    static int size = 0;

    public int[] sortArray(int[] nums) {
        heapSort(nums);
        return nums;
    }

    // 堆排序
    private void heapSort(int[] a) {
        size = 0;

        for (int i = 0; i < a.length; i++) {
            up(a, i);
        }

        for (int i = a.length - 1; i >= 0; i--) {
            swap(a, 0, i);
            size--;
            down(a, 0);
        }
    }

    // 向下调整
    private void down(int[] a, int i) {
        int l = 2 * i + 1;
        while (l < size) {
            int best = l + 1 < size && a[l + 1] > a[l] ? l + 1 : l;
            if (a[best] > a[i]) {
                swap(a, i, best);
            } else {
                break;
            }
            i = best;
            l = 2 * i + 1;
        }
    }

    // 向上调整建堆
    private void up(int[] a, int index) {
        if (index == 0) {
            size++;
            return;
        }
        while (a[(index - 1) / 2] < a[index]) {
            swap(a, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
        size++;
    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
```

---

## 2. 堆结构下标关系

数组可以看成一棵完全二叉树。

如果当前节点下标是 `i`：

```text
左孩子：2 * i + 1
右孩子：2 * i + 2
父节点：(i - 1) / 2
```

代码里的向下调整先找到左孩子：

```java
int l = 2 * i + 1;
```

再判断右孩子是否存在，并选出左右孩子中更大的那个：

```java
int best = l + 1 < size && a[l + 1] > a[l] ? l + 1 : l;
```

因为这是大根堆，所以父节点要和更大的孩子比较。

---

## 3. 建堆过程：向上调整

你的代码使用的是 **向上调整建堆**。

流程是：

```text
从左到右遍历数组
每加入一个新元素，就让它向上和父节点比较
如果它比父节点大，就交换
直到不比父节点大，或者来到堆顶
```

核心代码：

```java
while (a[(index - 1) / 2] < a[index]) {
    swap(a, index, (index - 1) / 2);
    index = (index - 1) / 2;
}
```

这样处理完后，`[0, size - 1]` 范围内始终是一个大根堆。

---

## 4. 排序过程：堆顶出堆

建好大根堆后，数组最大值一定在堆顶：

```text
a[0]
```

排序时，每次做三件事：

```text
1. 把堆顶最大值和当前堆尾交换
2. size--，把最大值移出堆范围
3. 从堆顶开始 down，重新调整成大根堆
```

对应代码：

```java
for (int i = a.length - 1; i >= 0; i--) {
    swap(a, 0, i);
    size--;
    down(a, 0);
}
```

因为最大值会依次放到数组右侧，所以最终数组是升序。

---

## 5. 向下调整怎么理解

`down` 用于修复堆顶。

当堆顶被换成一个较小的数后，它可能不再满足大根堆要求，所以要不断往下沉。

每轮：

1. 找到左右孩子中更大的那个 `best`
2. 如果 `a[best] > a[i]`，说明孩子比父节点大，需要交换
3. 交换后继续向下检查
4. 如果父节点已经不小于最大孩子，调整结束

核心代码：

```java
while (l < size) {
    int best = l + 1 < size && a[l + 1] > a[l] ? l + 1 : l;
    if (a[best] > a[i]) {
        swap(a, i, best);
    } else {
        break;
    }
    i = best;
    l = 2 * i + 1;
}
```

---

## 6. 复杂度

### 时间复杂度

这份代码使用向上调整建堆：

```text
建堆：O(n log n)
排序：O(n log n)
总时间复杂度：O(n log n)
```

说明：

- 每个元素插入堆时，最多向上调整 `log n` 层。
- 每次弹出堆顶后，最多向下调整 `log n` 层。

### 空间复杂度

```text
O(1)
```

堆排序直接在原数组上交换元素，不需要额外数组。

### 稳定性

```text
不稳定
```

因为堆排序会频繁交换远距离元素，相等元素的相对顺序可能改变。

---

## 7. 易错点

### 1. `size` 要初始化

如果 `size` 是静态变量：

```java
static int size = 0;
```

那么每次排序前最好重置：

```java
size = 0;
```

否则同一个 `Solution` 或同一个类多次调用时，`size` 可能保留上一次的值。

### 2. 右孩子要判断是否越界

```java
int best = l + 1 < size && a[l + 1] > a[l] ? l + 1 : l;
```

只有 `l + 1 < size` 时，右孩子才存在。

### 3. `down` 里交换后要更新当前位置

```java
i = best;
l = 2 * i + 1;
```

否则只会调整一层，不能继续向下修复堆。

### 4. `size--` 后再 `down`

```java
swap(a, 0, i);
size--;
down(a, 0);
```

先把最大值换到末尾，再缩小堆范围，这样最大值就不会再参与后续调整。

---

## 8. 更常见的 O(n) 建堆写法

除了逐个插入并 `up`，还有一种更常见的建堆方式：

```text
从最后一个非叶子节点开始，依次向下调整。
```

代码形态是：

```java
size = a.length;
for (int i = a.length / 2 - 1; i >= 0; i--) {
    down(a, i);
}
```

这类建堆方式的时间复杂度是：

```text
O(n)
```

不过你当前这份代码使用的是 `up` 建堆，也完全能正确完成排序，只是建堆复杂度是 `O(n log n)`。

---

## 9. 复习口诀

```text
大根堆排升序。
先建堆，再交换堆顶和堆尾。
size 缩小后，从堆顶 down。
父节点看 (i - 1) / 2，孩子看 2 * i + 1 和 2 * i + 2。
```
