// 快速测试用的示例代码集合
// 界面上的 Java / Python / JavaScript 三个按钮分别对应前三个样本
export const samples = {
  // Java 正确示例 — HelloWorld 标准输出
  javaCorrect: `public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`,

  // Java 空指针 bug 示例 — 对 null 调用 .length() 会抛 NullPointerException
  javaBug: `public class Test {
    public static void main(String[] args) {
        String str = null;
        System.out.println(str.length());
    }
}`,

  // Python 冒泡排序实现
  pythonSort: `def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(0, n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
    return arr`,

  // JavaScript 异步 fetch 请求
  jsAsync: `async function fetchUserData(userId) {
    const response = await fetch(
        \`https://api.example.com/users/\${userId}\`
    );
    const data = await response.json();
    return data;
}`,

  // JS 无限递归 bug — 缺少递归终止条件
  logicBug: `function factorial(n) {
    return n * factorial(n - 1);
}`
}
