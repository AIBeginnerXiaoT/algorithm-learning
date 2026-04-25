# 优先队列经典题：减半数组、会议室、合并 K 个链表

优先队列（PriorityQueue）本质上是堆。

在 Java 里：

```text
PriorityQueue 默认是小根堆。
如果想要大根堆，需要自定义比较器。
```

这一篇整理三个典型题：

- `2208. 将数组和减半的最少操作次数`
- `会议室 / 最大同时进行会议数`
- `23. 合并 K 个升序链表`

它们的共同点是：

```text
每一步都需要快速拿到当前最大值或最小值。
```

---

## 1. PriorityQueue 基础

### 小根堆

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
```

每次 `poll()` 拿到当前最小值。

### 大根堆

```java
PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> Integer.compare(b, a));
```

每次 `poll()` 拿到当前最大值。

注意不要写：

```java
(a, b) -> b - a
```

因为当数值范围很大时可能溢出。更推荐：

```java
Integer.compare(b, a)
```

---

## 2. 题目一：将数组和减半的最少操作次数

### 题意

给定数组 `nums`，每次可以选择一个数，把它变成原来的一半。  
要求数组总和至少减少一半，返回最少操作次数。

核心策略：

```text
每次都减当前最大的数。
```

因为把最大值减半，当前这一步能减少最多的和。

### 思路

1. 计算原数组总和 `sum`
2. 把所有数放入大根堆
3. 每次弹出最大值 `cur`
4. 把 `cur / 2` 作为减少量累加到 `reduce`
5. 再把 `cur / 2` 放回堆
6. 直到 `reduce >= sum / 2`

### Java 代码

```java
import java.util.PriorityQueue;

class Solution {
    public int halveArray(int[] nums) {
        PriorityQueue<Double> pq = new PriorityQueue<>((a, b) -> b.compareTo(a));
        double sum = 0;

        for (int num : nums) {
            sum += num;
            pq.add((double) num);
        }

        int ans = 0;
        double reduce = 0;

        while (reduce < sum / 2) {
            ans++;
            double cur = pq.poll();
            double half = cur / 2;
            reduce += half;
            pq.add(half);
        }

        return ans;
    }
}
```

### 为什么用大根堆

每次操作都应该选当前最大值。  
如果不用堆，每次都扫描数组找最大值，单次是 `O(n)`。

使用大根堆后：

```text
取最大值：O(log n)
放回一半：O(log n)
```

### 复杂度

假设操作次数是 `m`：

```text
时间复杂度：O(n log n + m log n)
空间复杂度：O(n)
```

---

## 3. 题目二：会议室 / 最大同时进行会议数

### 题意

给定若干会议时间 `[start, end]`，求最少需要多少个会议室。  
等价于求：

```text
同一时刻最多有多少场会议同时进行。
```

### 思路

先按会议开始时间排序。

然后维护一个小根堆，堆里放的是当前还没结束的会议的结束时间。

遍历每个会议：

1. 如果堆顶会议已经结束，就弹出
2. 把当前会议的结束时间加入堆
3. 堆大小就是当前正在进行的会议数
4. 用最大堆大小更新答案

### Java 代码

下面代码按常见的半开区间 `[start, end)` 处理。  
如果 `end <= start`，说明上一场会议结束后，当前会议可以复用会议室。

```java
import java.io.*;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));

        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());

        int[][] meetings = new int[n][2];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            meetings[i][0] = Integer.parseInt(st.nextToken());
            meetings[i][1] = Integer.parseInt(st.nextToken());
        }

        Arrays.sort(meetings, (a, b) -> Integer.compare(a[0], b[0]));

        int ans = 0;
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int i = 0; i < n; i++) {
            while (!pq.isEmpty() && pq.peek() <= meetings[i][0]) {
                pq.poll();
            }
            pq.add(meetings[i][1]);
            ans = Math.max(ans, pq.size());
        }

        out.println(ans);
        out.flush();
    }
}
```

### 为什么用小根堆

我们最关心的是：

```text
当前最早结束的会议什么时候结束？
```

所以堆顶应该是最小结束时间。

如果最早结束的会议都还没结束，那么其他会议更不可能结束。

### 易错点

### 1. 排序比较器不要直接相减

不推荐：

```java
Arrays.sort(meetings, (a, b) -> a[0] - b[0]);
```

推荐：

```java
Arrays.sort(meetings, (a, b) -> Integer.compare(a[0], b[0]));
```

### 2. `ans` 初始值用 `0`

如果会议数量可能是 `0`，`ans = 1` 会出错。  
写成 `0` 更稳。

### 3. 是否使用 `<=` 取决于题意

如果会议是半开区间 `[start, end)`：

```java
pq.peek() <= meetings[i][0]
```

如果题目认为结束时间和开始时间相同也冲突，就要改成：

```java
pq.peek() < meetings[i][0]
```

---

## 4. 题目三：合并 K 个升序链表

### 题意

给定 `k` 个升序链表，把它们合并成一个新的升序链表。

核心策略：

```text
每次从 k 个链表当前头节点中，拿出最小的那个。
```

这正好适合小根堆。

### Java 代码

LeetCode 中会提前提供 `ListNode` 定义。  
下面代码只写核心 `Solution`。

```java
import java.util.PriorityQueue;

class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(a.val, b.val)
        );

        for (ListNode node : lists) {
            if (node != null) {
                pq.add(node);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;

        while (!pq.isEmpty()) {
            ListNode node = pq.poll();
            cur.next = node;
            cur = cur.next;

            if (node.next != null) {
                pq.add(node.next);
            }
        }

        return dummy.next;
    }
}
```

### 为什么用小根堆

每个链表本身都是升序。  
只要把每个链表当前头节点放入堆里，堆顶就是所有候选节点中最小的。

每弹出一个节点，就把它的下一个节点放入堆。

流程：

```text
1. 每个链表头节点入堆
2. 弹出最小节点，接到答案链表后面
3. 如果该节点有 next，就把 next 入堆
4. 重复直到堆为空
```

### 复杂度

设所有链表节点总数为 `N`，链表数量为 `k`：

```text
时间复杂度：O(N log k)
空间复杂度：O(k)
```

因为堆里最多同时放 `k` 个节点。

### 易错点

### 1. 比较器用 `Integer.compare`

不推荐：

```java
(a, b) -> a.val - b.val
```

推荐：

```java
(a, b) -> Integer.compare(a.val, b.val)
```

### 2. 空链表不要入堆

```java
if (node != null) {
    pq.add(node);
}
```

否则会出现空指针问题。

### 3. 使用 dummy 节点能减少特殊判断

你原来的写法需要先取出第一个节点当 `head`。  
使用 `dummy` 后，循环逻辑更统一。

---

## 5. 三道题对比

| 题目 | 使用堆类型 | 堆里存什么 | 每次取什么 |
| :--- | :--- | :--- | :--- |
| 数组和减半 | 大根堆 | 当前数字 | 当前最大值 |
| 会议室 | 小根堆 | 会议结束时间 | 最早结束时间 |
| 合并 K 个链表 | 小根堆 | 链表当前节点 | 当前最小节点 |

---

## 6. 复习口诀

```text
每次要最大，用大根堆。
每次要最小，用小根堆。
会议室看最早结束时间。
合并 K 个链表看当前最小头节点。
数组减半每次砍当前最大值。
```
