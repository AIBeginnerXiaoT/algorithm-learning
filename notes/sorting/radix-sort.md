# 基数排序：按位计数与负数处理

基数排序（Radix Sort）是一种非比较排序。

它的核心思想是：

```text
从低位到高位，按每一位进行稳定的计数排序。
```

例如十进制整数，可以依次按照：

```text
个位 -> 十位 -> 百位 -> 千位
```

进行排序。

---

## 1. 完整代码

下面是根据你给的代码整理后的版本：

```java
public class RadixSort {

    // 可以设置进制，不一定是 10 进制
    public static int BASE = 10;

    public static int MAXN = 50001;

    public static int[] help = new int[MAXN];

    public static int[] cnts = new int[BASE];

    public static int[] sortArray(int[] arr) {
        if (arr == null || arr.length < 2) {
            return arr;
        }

        int n = arr.length;

        // 找到数组中的最小值
        int min = arr[0];
        for (int i = 1; i < n; i++) {
            min = Math.min(min, arr[i]);
        }

        // 全部减去最小值，把所有数变成非负数
        // 同时找到变换后的最大值
        int max = 0;
        for (int i = 0; i < n; i++) {
            arr[i] -= min;
            max = Math.max(max, arr[i]);
        }

        // 根据最大值决定需要排几轮
        radixSort(arr, n, bits(max));

        // 排完之后再加回最小值
        for (int i = 0; i < n; i++) {
            arr[i] += min;
        }

        return arr;
    }

    // 计算最大值有几位
    public static int bits(int number) {
        int ans = 0;
        while (number > 0) {
            ans++;
            number /= BASE;
        }
        return ans;
    }

    // 基数排序核心
    public static void radixSort(int[] arr, int n, int bits) {
        int offset = 1;

        for (int b = 1; b <= bits; b++, offset *= BASE) {

            // 清空计数数组
            for (int i = 0; i < BASE; i++) {
                cnts[i] = 0;
            }

            // 统计当前位的词频
            for (int i = 0; i < n; i++) {
                cnts[(arr[i] / offset) % BASE]++;
            }

            // 前缀和
            for (int i = 1; i < BASE; i++) {
                cnts[i] += cnts[i - 1];
            }

            // 从右往左遍历，保证稳定性
            for (int i = n - 1; i >= 0; i--) {
                int num = (arr[i] / offset) % BASE;
                help[--cnts[num]] = arr[i];
            }

            // 拷贝回原数组
            for (int i = 0; i < n; i++) {
                arr[i] = help[i];
            }
        }
    }
}
```

---

## 2. 基数排序适合什么场景

基数排序适合排序整数，并且值域位数不太大。

它不通过比较两个数大小来排序，而是根据每一位的数字进行排序。

以十进制为例：

```text
170, 45, 75, 90, 802, 24, 2, 66
```

先按个位排序，再按十位排序，再按百位排序。  
每一轮都必须保持稳定性，最后整体就有序。

---

## 3. 为什么要处理负数

普通基数排序通常只处理非负整数。  
因为取某一位时使用：

```java
(arr[i] / offset) % BASE
```

如果 `arr[i]` 是负数，得到的下标可能也是负数，不能直接用来访问 `cnts`。

所以你的代码先找到最小值：

```java
int min = arr[0];
for (int i = 1; i < n; i++) {
    min = Math.min(min, arr[i]);
}
```

然后把所有数都减去 `min`：

```java
arr[i] -= min;
```

这样所有数都会变成非负数。

排序完成后，再加回去：

```java
arr[i] += min;
```

这个操作不会改变原始大小关系。  
例如：

```text
原数组：[-5, -1, 3]
减去 min=-5 后：[0, 4, 8]
```

相对大小仍然一致。

---

## 4. bits 函数：决定排几轮

`bits(max)` 用来计算最大值在当前进制下有几位。

```java
public static int bits(int number) {
    int ans = 0;
    while (number > 0) {
        ans++;
        number /= BASE;
    }
    return ans;
}
```

如果最大值是 `802`，`BASE = 10`：

```text
802 -> 80 -> 8 -> 0
```

所以需要排 `3` 轮：

```text
个位、十位、百位
```

如果所有数变换后都等于 `0`，那么 `bits(0) = 0`，不需要进入排序循环，数组本来就全相等。

---

## 5. radixSort 核心过程

每一轮排序只看当前位。

`offset` 表示当前位：

```text
offset = 1    看个位
offset = 10   看十位
offset = 100  看百位
```

当前位数字通过下面公式取得：

```java
(arr[i] / offset) % BASE
```

例如 `arr[i] = 802`：

```text
offset = 1:   802 / 1   % 10 = 2
offset = 10:  802 / 10  % 10 = 0
offset = 100: 802 / 100 % 10 = 8
```

---

## 6. 计数数组 cnts 怎么用

### 1. 统计词频

```java
for (int i = 0; i < n; i++) {
    cnts[(arr[i] / offset) % BASE]++;
}
```

`cnts[x]` 表示当前位等于 `x` 的数字有多少个。

### 2. 做前缀和

```java
for (int i = 1; i < BASE; i++) {
    cnts[i] += cnts[i - 1];
}
```

前缀和之后：

```text
cnts[x] 表示当前位 <= x 的数字有多少个
```

也就是说，当前位等于 `x` 的数，应该放到：

```text
cnts[x] - 1
```

这个位置附近。

### 3. 从右往左放入 help

```java
for (int i = n - 1; i >= 0; i--) {
    int num = (arr[i] / offset) % BASE;
    help[--cnts[num]] = arr[i];
}
```

这里先 `--cnts[num]`，再放入 `help`。

因为数组下标从 `0` 开始，前缀和表示的是“个数”，要转成最后一个合法位置就需要减一。

---

## 7. 为什么要从右往左遍历

从右往左是为了保证稳定性。

稳定性含义：

```text
如果两个数当前位相同，它们在这一轮排序后的相对顺序不变。
```

基数排序必须依赖稳定性。  
因为低位排完后，高位排序时不能破坏低位已经建立好的顺序。

所以代码写成：

```java
for (int i = n - 1; i >= 0; i--) {
    int num = (arr[i] / offset) % BASE;
    help[--cnts[num]] = arr[i];
}
```

---

## 8. 复杂度

设：

- `n` 是数组长度
- `d` 是最大值的位数
- `BASE` 是进制

则：

```text
时间复杂度：O(d * (n + BASE))
空间复杂度：O(n + BASE)
稳定性：稳定
```

如果 `BASE = 10`，并且整数位数有限，那么可以近似看作线性排序。

---

## 9. 易错点

### 1. 负数不能直接取位当下标

必须先转成非负数，或者单独处理负数。  
这份代码使用的是整体平移：

```java
arr[i] -= min;
```

### 2. `help` 数组容量要够

代码里：

```java
public static int MAXN = 50001;
public static int[] help = new int[MAXN];
```

这要求输入数组长度不能超过 `50001`。  
如果不确定数组长度，工程写法可以改成在 `sortArray` 中按需创建：

```java
int[] help = new int[arr.length];
```

### 3. `cnts` 每一轮都要清空

```java
for (int i = 0; i < BASE; i++) {
    cnts[i] = 0;
}
```

否则上一轮的计数会影响下一轮。

### 4. `offset *= BASE` 可能溢出

如果数字非常大，`offset *= BASE` 可能超过 `int` 范围。  
一般刷题数据受控时问题不大，更稳的写法可以用 `long offset`。

### 5. 类名建议大写

Java 类名通常使用大驼峰：

```java
public class RadixSort
```

你原代码里的 `radixSort` 能表达意思，但不符合 Java 命名习惯。

---

## 10. 复习口诀

```text
基数排序按位排，低位到高位。
每一位用计数排序。
前缀和定位置。
从右往左保证稳定。
负数先整体平移成非负数，排完再平移回来。
```
