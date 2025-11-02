import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        TreeSet<Integer> positions = new TreeSet<>();
        long total = 0;
        
        // Person 0 starts at position 0
        positions.add(0);
        
        for (int i = 0; i < n; i++) {
            int x = sc.nextInt();
            
            // Find who gets affected
            Integer left = positions.lower(x);   // Person to the left
            Integer right = positions.higher(x); // Person to the right
            
            // Remove old distances for affected people
            if (left != null) {
                total -= getDistance(left, positions);
            }
            if (right != null) {
                total -= getDistance(right, positions);
            }
            
            // Add the new person
            positions.add(x);
            
            // Add new distances for affected people
            if (left != null) {
                total += getDistance(left, positions);
            }
            total += getDistance(x, positions);  // New person
            if (right != null) {
                total += getDistance(right, positions);
            }
            
            System.out.println(total);
        }
    }
    
    // Find distance to nearest neighbor for person at position 'pos'
    static int getDistance(int pos, TreeSet<Integer> positions) {
        Integer left = positions.lower(pos);
        Integer right = positions.higher(pos);
        
        int minDist = Integer.MAX_VALUE;
        if (left != null) minDist = Math.min(minDist, pos - left);
        if (right != null) minDist = Math.min(minDist, right - pos);
        
        return minDist;
    }
}
/*
Great question! Here are the **key pitfalls** you might face when coding this problem:

## 1. **Integer Overflow** 🚨
```java
// WRONG - int will overflow
int total = 0;  // Can exceed 2.1 billion

// CORRECT - use long
long total = 0;  // Can handle much larger sums
```
**Why**: Sum of distances can be very large with N=500,000 and coordinates up to 10⁹.

## 2. **Null Pointer Exceptions** 🚨
```java
// WRONG - will crash when left/right is null
Integer left = positions.lower(x);
total -= getDistance(left, positions);  // NPE if left is null!

// CORRECT - check for null
if (left != null) {
    total -= getDistance(left, positions);
}
```
**Why**: `lower()` and `higher()` return `null` when no such element exists.

## 3. **Edge Case: First Person** 🚨
```java
// WRONG - treating first person same as others
for (int i = 0; i < n; i++) {
    // Same logic for all...
}

// CORRECT - handle first person specially
if (i == 0) {
    // Only two people: at 0 and x
    total = 2L * x;
} else {
    // Normal differential update logic
}
```
**Why**: When first person arrives, there's no "old distance" to subtract.

## 4. **Forgetting Person 0** 🚨
```java
// WRONG - not adding person 0 initially
TreeSet<Integer> positions = new TreeSet<>();

// CORRECT - person 0 starts at position 0
TreeSet<Integer> positions = new TreeSet<>();
positions.add(0);
```

## 5. **Wrong Distance Calculation** 🚨
```java
// WRONG - not handling when no neighbors exist
static int getDistance(int pos, TreeSet<Integer> positions) {
    Integer left = positions.lower(pos);
    Integer right = positions.higher(pos);
    return Math.min(pos - left, right - pos);  // NPE!
}

// CORRECT - handle null neighbors
static int getDistance(int pos, TreeSet<Integer> positions) {
    Integer left = positions.lower(pos);
    Integer right = positions.higher(pos);
    
    int minDist = Integer.MAX_VALUE;
    if (left != null) minDist = Math.min(minDist, pos - left);
    if (right != null) minDist = Math.min(minDist, right - pos);
    return minDist;
}
```

## 6. **Double Counting in Updates** 🚨
```java
// WRONG - adding new person's distance twice
total += getDistance(x, positions);
// ... later in code ...
total += getDistance(x, positions);  // Added twice!

// CORRECT - add each distance exactly once
```

## 7. **Wrong TreeSet Methods** 🚨
```java
// WRONG - these don't exist or work differently
Integer left = positions.lowerBound(x);   // No such method
Integer right = positions.upperBound(x);  // No such method

// CORRECT - use proper TreeSet methods
Integer left = positions.lower(x);    // < x
Integer right = positions.higher(x);  // > x
```

## 8. **Input/Output Issues** 🚨
```java
// WRONG - not handling large inputs efficiently
Scanner sc = new Scanner(System.in);  // Can be slow for large inputs

// BETTER - for competitive programming
BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
StringTokenizer st = new StringTokenizer(br.readLine());
```

## 9. **Off-by-One in Logic** 🚨
```java
// WRONG - updating wrong people
// When person X arrives, update X's neighbors
// But forgetting that X itself also needs distance calculated

// CORRECT - update left neighbor, X, and right neighbor
```

## 10. **Not Maintaining Invariants** 🚨
```java
// WRONG - forgetting to remove before adding
positions.add(x);  // Add first
total -= oldDistance;  // Then try to remove - but neighbors changed!

// CORRECT - remove old contributions before structural changes
total -= oldDistance;
positions.add(x);
total += newDistance;
```

## **Safe Template to Avoid Pitfalls:**

```java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        TreeSet<Integer> positions = new TreeSet<>();
        long total = 0;  // PITFALL 1: Use long
        
        positions.add(0);  // PITFALL 4: Don't forget person 0
        
        for (int i = 0; i < n; i++) {
            int x = sc.nextInt();
            
            if (i == 0) {  // PITFALL 3: Handle first person
                positions.add(x);
                total = 2L * x;
            } else {
                Integer left = positions.lower(x);   // PITFALL 7: Correct methods
                Integer right = positions.higher(x);
                
                // PITFALL 2: Check for null
                if (left != null) total -= getDistance(left, positions);
                if (right != null) total -= getDistance(right, positions);
                
                positions.add(x);  // PITFALL 10: Add after removing old
                
                if (left != null) total += getDistance(left, positions);
                total += getDistance(x, positions);  // PITFALL 6: Add once
                if (right != null) total += getDistance(right, positions);
            }
            
            System.out.println(total);
        }
    }
    
    static int getDistance(int pos, TreeSet<Integer> positions) {
        Integer left = positions.lower(pos);
        Integer right = positions.higher(pos);
        
        int minDist = Integer.MAX_VALUE;  // PITFALL 5: Handle no neighbors
        if (left != null) minDist = Math.min(minDist, pos - left);
        if (right != null) minDist = Math.min(minDist, right - pos);
        return minDist;
    }
}
```

**Most common mistake**: Forgetting null checks and using `int` instead of `long`! 🎯*/
