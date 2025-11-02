//https://atcoder.jp/contests/abc430/tasks/abc430_c
import java.util.*;

public class TruckDriver {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        int N = sc.nextInt();
        int A = sc.nextInt();
        int B = sc.nextInt();
        String S = sc.next();
        
        // Build cumulative sums for 'a' and 'b'
        int[] cumulativeA = new int[N + 1];
        int[] cumulativeB = new int[N + 1];
        
        for (int i = 0; i < N; i++) {
            cumulativeA[i + 1] = cumulativeA[i];
            cumulativeB[i + 1] = cumulativeB[i];
            
            if (S.charAt(i) == 'a') {
                cumulativeA[i + 1]++;
            } else {
                cumulativeB[i + 1]++;
            }
        }
        
        long answer = 0;
        
        // For each starting position l (1-indexed)
        for (int l = 1; l <= N; l++) {
            // Find minimum r such that [l,r] contains >= A 'a's
            int al = findMinR(cumulativeA, l, N, A);
            
            // Find maximum r such that [l,r] contains < B 'b's
            int bl = findMaxR(cumulativeB, l, N, B);
            
            // Count valid r values in range [al, bl]
            if (al != -1 && bl != -1 && al <= bl) {
                answer += (bl - al + 1);
            }
        }
        
        System.out.println(answer);
    }
    
    // Binary search to find minimum r where [l,r] has >= A 'a's
    private static int findMinR(int[] cumulative, int l, int N, int target) {
        int left = l, right = N + 1;  // right = N + 1 for "not found" case
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = cumulative[mid] - cumulative[l - 1];
            
            if (count >= target) {
                right = mid;      // Found valid, try to find smaller
            } else {
                left = mid + 1;   // Need bigger r
            }
        }
        
        return (left <= N) ? left : -1;  // Return -1 if not found
    }
    
    // Binary search to find maximum r where [l,r] has < B 'b's
    private static int findMaxR(int[] cumulative, int l, int N, int limit) {
        int left = l - 1, right = N;  // left = l - 1 for "not found" case
        
        while (left < right) {
            int mid = left + (right - left + 1) / 2;  // +1 for upper bound search
            int count = cumulative[mid] - cumulative[l - 1];
            
            if (count < limit) {
                left = mid;       // Found valid, try to find larger
            } else {
                right = mid - 1;  // Need smaller r
            }
        }
        
        return (left >= l) ? left : -1;  // Return -1 if not found
    }
}
/*
pitfalls for this problem and what we have to take care of 
Great question! Here are the **major pitfalls** you're likely to encounter when solving this problem from scratch:

## 1. **Index Confusion (1-indexed vs 0-indexed)**

**Pitfall**: The problem uses 1-indexed positions, but arrays are 0-indexed in most languages.

```java
// WRONG: Direct mapping
String S = "abbaaabaaba";
// Position 1 in problem = S.charAt(0) in code

// CORRECT: Careful index conversion
// When problem says "l-th character", it means S.charAt(l-1)
int count = cumulative[r] - cumulative[l-1];  // Be careful here!
```

## 2. **Cumulative Sum Array Size**

**Pitfall**: Making cumulative array of size N instead of N+1.

```java
// WRONG
int[] cumulative = new int[N];  // Will cause index out of bounds

// CORRECT  
int[] cumulative = new int[N+1];  // Need extra space for cumulative[0] = 0
```

## 3. **Binary Search Boundary Errors**

**Pitfall**: Setting wrong initial boundaries or using wrong mid calculation.

```java
// WRONG: For upper bound search
int mid = left + (right - left) / 2;  // Missing +1

// CORRECT: For upper bound search  
int mid = left + (right - left + 1) / 2;  // Need +1 to avoid infinite loop
```

## 4. **Off-by-One Errors in Conditions**

**Pitfall**: Confusing ≥ vs > and < vs ≤.

```java
// Problem says: "≥ A" and "< B"
// WRONG
if (countA > A && countB <= B)  

// CORRECT
if (countA >= A && countB < B)
```

## 5. **Integer Overflow**

**Pitfall**: Using `int` for the final answer when it can be large.

```java
// WRONG: Can overflow for large inputs
int answer = 0;

// CORRECT: Use long for answer
long answer = 0;  // N can be 3×10^5, so answer can be ~10^10
```

## 6. **Brute Force Timeout**

**Pitfall**: Trying O(N³) solution for large constraints.

```java
// WRONG: Will timeout for N = 3×10^5
for (int l = 1; l <= N; l++) {
    for (int r = l; r <= N; r++) {
        // Count a's and b's by iterating [l,r]
        for (int i = l; i <= r; i++) { ... }  // O(N³)
    }
}
```

## 7. **Misunderstanding the Problem**

**Pitfall**: Thinking you need exactly A `a`s instead of ≥ A.

```java
// WRONG: Checking for exactly A
if (countA == A && countB < B)

// CORRECT: At least A
if (countA >= A && countB < B)
```

## 8. **Binary Search Infinite Loops**

**Pitfall**: Wrong termination condition or mid calculation causing infinite loops.

```java
// WRONG: Can cause infinite loop in upper bound search
while (left < right) {
    int mid = (left + right) / 2;  // Should be (left + right + 1) / 2
    if (condition) left = mid;     // This can cause left=mid=right-1 forever
    else right = mid - 1;
}
```

## 9. **Not Handling Edge Cases**

**Pitfall**: Not considering when no valid substring exists.

```java
// WRONG: Not checking if al or bl are valid
answer += (bl - al + 1);

// CORRECT: Check validity
if (al != -1 && bl != -1 && al <= bl) {
    answer += (bl - al + 1);
}
```

## 10. **Wrong Input Reading**

**Pitfall**: Reading input in wrong order or wrong data types.

```java
// Problem gives: N A B on first line, S on second line
// WRONG order
int A = sc.nextInt();
int N = sc.nextInt(); 
int B = sc.nextInt();

// CORRECT order
int N = sc.nextInt();
int A = sc.nextInt();
int B = sc.nextInt();
```

## 11. **Forgetting Base Cases**

**Pitfall**: Not handling cases where A > N or B = 0.

```java
// Edge cases to consider:
// - A > N: No substring can have ≥ A 'a's
// - B = 0: No substring can have < 0 'b's  
// - String has only 'a's or only 'b's
```

## 12. **Memory Issues for Large N**

**Pitfall**: Creating unnecessary data structures for large inputs.

```java
// WRONG: Storing all substrings
List<String> allSubstrings = new ArrayList<>();  // Memory explosion

// CORRECT: Use cumulative sums only
int[] cumulativeA = new int[N+1];
int[] cumulativeB = new int[N+1];
```

## Pro Tips to Avoid These:

1. **Always trace through small examples manually first**
2. **Test edge cases: empty strings, all 'a's, all 'b's**
3. **Use 1-based indexing consistently or convert carefully**
4. **Double-check binary search templates**
5. **Use `long` for final answer**
6. **Validate your cumulative sum logic with simple examples**

The most common mistake is probably the **index confusion** between 1-indexed problem and 0-indexed arrays!*/
