import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        TreeSet<Integer> st = new TreeSet<>();
        long res = 0;
        
        // First person
        int x = sc.nextInt();
        st.add(0);
        st.add(x);
        res += 2L * x;
        System.out.println(res);
        
        // Rest of the people
        for (int i = 1; i < n; i++) {
            x = sc.nextInt();
            
            // Find neighbors
            List<Integer> affected = new ArrayList<>();
            Integer it = st.ceiling(x);  // First element >= x
            if (it != null) affected.add(it);
            
            it = st.lower(x);  // Last element < x
            if (it != null) affected.add(it);
            
            // Subtract old distances
            for (int pos : affected) {
                res -= getNearest(pos, st);
            }
            
            // Insert new position
            st.add(x);
            affected.add(x);
            
            // Add new distances
            for (int pos : affected) {
                res += getNearest(pos, st);
            }
            
            System.out.println(res);
        }
    }
    
    static int getNearest(int x, TreeSet<Integer> st) {
        int res = Integer.MAX_VALUE;
        
        Integer left = st.lower(x);
        if (left != null) {
            res = Math.min(res, x - left);
        }
        
        Integer right = st.higher(x);
        if (right != null) {
            res = Math.min(res, right - x);
        }
        
        return res;
    }
}
/*
1. Duplicate Position Issue 🚨 CRITICAL
java// PROBLEM: What if x equals an existing position?
st.add(x);  // TreeSet won't add duplicates, but logic assumes it does!
From constraints: "Xi ≠ Xj if i ≠ j" - so this shouldn't happen, but it's good to be aware.
2. Integer Overflow in getNearest() 🚨
java// POTENTIAL ISSUE:
static int getNearest(int x, TreeSet<Integer> st) {
    int res = Integer.MAX_VALUE;  // This is fine
    res = Math.min(res, x - left);  // But what if this calculation overflows?
}
Fix: Since coordinates can be up to 10⁹, differences are safe in int.
3. Wrong TreeSet Method Usage 🚨
java// YOUR CODE:
Integer it = st.ceiling(x);  // First element >= x (includes x itself!)

// BETTER:
Integer it = st.higher(x);   // First element > x (excludes x)
Issue: ceiling(x) will return x itself after you insert it, which is wrong for neighbor finding.
4. Order of Operations Bug 🚨 MAJOR
java// YOUR CODE:
affected.add(x);  // Adding x to affected list
// But x isn't in the TreeSet yet when calculating old distances!

// This could cause issues in getNearest() for the new position
5. Edge Case: Position 0 🚨
java// POTENTIAL ISSUE: What if first person arrives at position 0?
int x = sc.nextInt();  // What if x = 0?
st.add(0);
st.add(x);  // If x = 0, only one element added!
res += 2L * x;  // Would be 0, but should handle this case
Corrected Version:
javaimport java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        TreeSet<Integer> st = new TreeSet<>();
        long res = 0;
        
        // First person
        int x = sc.nextInt();
        st.add(0);
        
        // Handle edge case: first person at position 0
        if (x == 0) {
            System.out.println(0);  // Only one person effectively
        } else {
            st.add(x);
            res = 2L * x;
            System.out.println(res);
        }
        
        // Rest of the people
        for (int i = 1; i < n; i++) {
            x = sc.nextInt();
            
            // Find neighbors BEFORE insertion
            List<Integer> affected = new ArrayList<>();
            Integer right = st.higher(x);  // Use higher(), not ceiling()
            if (right != null) affected.add(right);
            
            Integer left = st.lower(x);
            if (left != null) affected.add(left);
            
            // Subtract old distances
            for (int pos : affected) {
                res -= getNearest(pos, st);
            }
            
            // Insert new position
            st.add(x);
            
            // Add new distances (affected neighbors + new person)
            for (int pos : affected) {
                res += getNearest(pos, st);
            }
            res += getNearest(x, st);  // Add new person's distance separately
            
            System.out.println(res);
        }
    }
    
    static int getNearest(int x, TreeSet<Integer> st) {
        int minDist = Integer.MAX_VALUE;
        
        Integer left = st.lower(x);
        if (left != null) {
            minDist = Math.min(minDist, x - left);
        }
        
        Integer right = st.higher(x);
        if (right != null) {
            minDist = Math.min(minDist, right - x);
        }
        
        return minDist;
    }
}
Key Changes Made:

✅ Use higher(x) instead of ceiling(x)
✅ Handle edge case where first person is at position 0
✅ Don't add new person to affected list prematurely
✅ Calculate new person's distance separately

Additional Pitfalls to Watch:
6. Input Size for Competitive Programming 🚨
java// For very large inputs, Scanner can be slow
// Consider BufferedReader for better performance
7. TreeSet Import 🚨
javaimport java.util.*;  // ✅ Correct - includes TreeSet
// Don't forget this import!
8. Method Name Conflict 🚨
java// Don't name variables same as methods
int res = 0;           // Variable
static int getNearest() // Method - good naming
The biggest issue in your original code was using ceiling() instead of higher() and the affected list handling. Otherwise, your approach is solid! 🎯*/
