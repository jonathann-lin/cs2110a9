package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test suite for `MinPQueue`.
 */
class MinPQueueTest {

    @DisplayName("WHEN a new `MinPQueue` is constructed, THEN it should be empty with size 0.")
    @Test
    void testEmptyAtConstruction() {
        MinPQueue<String> pq = new MinPQueue<>();
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());
    }

    @DisplayName("WHEN an element is added, THEN size increases and `peek()` returns it.")
    @Test
    void testAddSingleElement() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 10.0);

        assertFalse(pq.isEmpty());
        assertEquals(1, pq.size());
        assertEquals("A", pq.peek());
        assertEquals(10.0, pq.minPriority());
    }

    @DisplayName("WHEN multiple elements are added, THEN `peek()` returns the one with smallest priority.")
    @Test
    void testAddMultipleElements() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 10.0);
        pq.addOrUpdate("B", 5.0);
        pq.addOrUpdate("C", 7.0);

        assertEquals("B", pq.peek());
        assertEquals(5.0, pq.minPriority());
        assertEquals(3, pq.size());
    }

    @DisplayName("WHEN elements are removed in priority order, THEN they come out sorted by priority.")
    @Test
    void testRemoveOrder() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 10.0);
        pq.addOrUpdate("B", 3.0);
        pq.addOrUpdate("C", 7.0);

        assertEquals("B", pq.remove());
        assertEquals("C", pq.remove());
        assertEquals("A", pq.remove());
        assertTrue(pq.isEmpty());
    }

    @DisplayName("WHEN `remove()` is called on an empty queue, THEN it throws NoSuchElementException.")
    @Test
    void testRemoveEmpty() {
        MinPQueue<String> pq = new MinPQueue<>();
        assertThrows(NoSuchElementException.class, pq::remove);
    }

    @DisplayName("WHEN `peek()` or `minPriority()` is called on empty queue, THEN it throws NoSuchElementException.")
    @Test
    void testPeekEmptyThrows() {
        MinPQueue<String> pq = new MinPQueue<>();
        assertThrows(NoSuchElementException.class, pq::peek);
        assertThrows(NoSuchElementException.class, pq::minPriority);
    }

    @DisplayName("WHEN a single element is removed, THEN queue becomes empty.")
    @Test
    void testRemoveSingleElement() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 10.0);

        String removed = pq.remove();
        assertEquals("A", removed);
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());
    }

    @DisplayName("WHEN multiple elements have equal priorities, THEN any of them may be removed first.")
    @Test
    void testEqualPriorities() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 5.0);
        pq.addOrUpdate("B", 5.0);
        pq.addOrUpdate("C", 5.0);

        String first = pq.remove();
        assertTrue(first.equals("A") || first.equals("B") || first.equals("C"));
        assertEquals(2, pq.size());
    }

    @DisplayName("WHEN an existing element’s priority is decreased, THEN it bubbles up correctly.")
    @Test
    void testPriorityDecrease() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 10.0);
        pq.addOrUpdate("B", 20.0);
        pq.addOrUpdate("C", 30.0);

        pq.addOrUpdate("C", 5.0); // decrease C’s priority
        assertEquals("C", pq.peek());
        assertEquals(5.0, pq.minPriority());
    }

    @DisplayName("WHEN an existing element’s priority is increased, THEN it bubbles down correctly.")
    @Test
    void testPriorityIncrease() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 1.0);
        pq.addOrUpdate("B", 2.0);
        pq.addOrUpdate("C", 3.0);

        pq.addOrUpdate("A", 10.0); // increase A’s priority
        assertEquals("B", pq.peek());
        assertEquals(2.0, pq.minPriority());
    }

    @DisplayName("WHEN repeatedly removing all elements, THEN queue should always maintain heap order and end empty.")
    @Test
    void testRepeatedRemoveUntilEmpty() {
        MinPQueue<Integer> pq = new MinPQueue<>();
        for (int i = 10; i >= 1; i--) {
            pq.addOrUpdate(i, i);
        }

        double lastPriority = -Double.MAX_VALUE;
        while (!pq.isEmpty()) {
            double currPriority = pq.minPriority();
            assertTrue(currPriority >= lastPriority);
            pq.remove();
            lastPriority = currPriority;
        }

        assertTrue(pq.isEmpty());
    }

    @DisplayName("WHEN updating an element’s priority to the same value, THEN nothing changes.")
    @Test
    void testPriorityNoChange() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 10.0);
        pq.addOrUpdate("A", 10.0); // same priority, no reordering

        assertEquals(1, pq.size());
        assertEquals("A", pq.peek());
    }

    @DisplayName("WHEN many elements are inserted and removed, THEN size and order remain consistent.")
    @Test
    void testStressInsertRemove() {
        MinPQueue<Integer> pq = new MinPQueue<>();
        int n = 1000;

        for (int i = 0; i < n; i++) {
            pq.addOrUpdate(i, Math.random() * 1000);
        }

        assertEquals(n, pq.size());

        double prev = -Double.MAX_VALUE;
        while (!pq.isEmpty()) {
            double curr = pq.minPriority();
            assertTrue(curr >= prev);
            prev = curr;
            pq.remove();
        }
        assertEquals(0, pq.size());
    }

    @DisplayName("WHEN the smallest element is removed, THEN the next smallest becomes the new min.")
    @Test
    void testHeapReorderAfterRemove() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("A", 1.0);
        pq.addOrUpdate("B", 2.0);
        pq.addOrUpdate("C", 3.0);

        pq.remove(); // remove "A"
        assertEquals("B", pq.peek());
        assertEquals(2.0, pq.minPriority());
    }

    @DisplayName("WHEN updating a deep node’s priority, THEN it bubbles correctly even at lower levels.")
    @Test
    void testUpdateDeepNode() {
        MinPQueue<Integer> pq = new MinPQueue<>();
        for (int i = 0; i < 15; i++) {
            pq.addOrUpdate(i, i + 10);
        }
        pq.addOrUpdate(14, 0.5);
        assertEquals(14, pq.peek());
    }

    @DisplayName("WHEN a large heap is used, THEN no structural errors (IndexOutOfBounds, etc.) occur.")
    @Test
    void testLargeHeapSafety() {
        MinPQueue<Integer> pq = new MinPQueue<>();
        int n = 10000;

        for (int i = 0; i < n; i++) pq.addOrUpdate(i, Math.random() * n);
        for (int i = 0; i < n / 2; i++) pq.remove();

        assertTrue(pq.size() > 0);
        assertDoesNotThrow(() -> pq.addOrUpdate(-1, 0.1));
    }

    @DisplayName("WHEN a single element is added, THEN peek and minPriority return it")
    @Test
    void testSingleAdd() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 5);
        assertEquals("a", pq.peek());
        assertEquals(5, pq.minPriority());
        assertEquals(1, pq.size());
    }

    @DisplayName("WHEN multiple elements are added, THEN peek returns the smallest priority")
    @Test
    void testMultipleAdd() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 10);
        pq.addOrUpdate("b", 5);
        pq.addOrUpdate("c", 8);

        assertEquals("b", pq.peek());
        assertEquals(5, pq.minPriority());
        assertEquals(3, pq.size());
    }

    @DisplayName("WHEN remove is called repeatedly, THEN elements come out in priority order")
    @Test
    void testRepeatedRemove() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("x", 5);
        pq.addOrUpdate("y", 2);
        pq.addOrUpdate("z", 3);

        assertEquals("y", pq.remove());
        assertEquals("z", pq.remove());
        assertEquals("x", pq.remove());
        assertTrue(pq.isEmpty());
    }

    @DisplayName("WHEN only two elements exist, THEN remove still works correctly")
    @Test
    void testTwoElements() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 1);
        pq.addOrUpdate("b", 2);
        assertEquals("a", pq.remove());
        assertEquals("b", pq.remove());
    }


    @DisplayName("WHEN update moves an element up, THEN heap is maintained")
    @Test
    void testUpdateBubbleUp() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 10);
        pq.addOrUpdate("b", 5);
        pq.addOrUpdate("c", 8);

        pq.addOrUpdate("a", 1);  // a should bubble to top
        assertEquals("a", pq.remove());
    }

    @DisplayName("WHEN update moves an element down, THEN heap is maintained")
    @Test
    void testUpdateBubbleDown() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 5);
        pq.addOrUpdate("b", 10);
        pq.addOrUpdate("c", 8);

        pq.addOrUpdate("a", 15); // a should bubble down
        assertEquals("c", pq.remove());
        assertEquals("b", pq.remove());
        assertEquals("a", pq.remove());
    }



    @DisplayName("WHEN keys are reused after removal, THEN queue accepts them again")
    @Test
    void testReuseKey() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 5);
        pq.remove();
        pq.addOrUpdate("a", 10);
        assertEquals("a", pq.peek());
        assertEquals(10, pq.minPriority());
    }

    @DisplayName("WHEN large number of elements are added and removed, THEN heap remains valid")
    @Test
    void testLargeHeap() {
        MinPQueue<String> pq = new MinPQueue<>();
        int N = 1000;
        for (int i = N; i >= 1; i--) pq.addOrUpdate("k"+i, i);
        for (int i = 1; i <= N; i++) assertEquals("k"+i, pq.remove());
    }

    @DisplayName("WHEN update priority equals current priority, THEN no bubble occurs")
    @Test
    void testUpdateSamePriority() {
        MinPQueue<String> pq = new MinPQueue<>();
        pq.addOrUpdate("a", 5);
        pq.addOrUpdate("a", 5);  // should do nothing
        assertEquals("a", pq.remove());
    }

    @DisplayName("WHEN remove is called on empty queue, THEN exception is thrown")
    @Test
    void testRemoveEmptyQueue() {
        MinPQueue<String> pq = new MinPQueue<>();
        assertThrows(NoSuchElementException.class, pq::remove);
    }

    @DisplayName("WHEN peek is called on empty queue, THEN exception is thrown")
    @Test
    void testPeekEmptyQueue() {
        MinPQueue<String> pq = new MinPQueue<>();
        assertThrows(NoSuchElementException.class, pq::peek);
    }

    @DisplayName("WHEN minPriority is called on empty queue, THEN exception is thrown")
    @Test
    void testMinPriorityEmptyQueue() {
        MinPQueue<String> pq = new MinPQueue<>();
        assertThrows(NoSuchElementException.class, pq::minPriority);
    }
}
