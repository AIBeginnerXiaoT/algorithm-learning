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
