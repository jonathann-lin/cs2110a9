package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Test suite for `ProbingPacMap`. 
 */
class ProbingPacMapTest {

    @DisplayName("WHEN a new `ProbingPacMap` is constructed, THEN it has size 0.")
    @Test
    void testEmptyAtConstruction() {
        ProbingPacMap<String, Integer> map = new ProbingPacMap<>();
        assertEquals(0, map.size());
    }

    @DisplayName("WHEN we `put()` a (key,value) pair into a `ProbingPacMap`, THEN the map should "
            + "report that it contains that key.")
    @Test
    void testContainsKeyAfterPut() {
        ProbingPacMap<String, Integer> map = new ProbingPacMap<>();
        map.put("hello",4);
        assertTrue(map.containsKey("hello"));
    }

    @DisplayName("WHEN we `put()` a (key,value) pair into a `ProbingPacMap`, THEN we should be "
            + "able to `get()` its value by passing in its key.")
    @Test
    void testGetAfterPut() {
        ProbingPacMap<String, Integer> map = new ProbingPacMap<>();
        map.put("hello",4);
        assertEquals(4, map.get("hello"));
    }

    @DisplayName("WHEN we `remove()` an entry from a `ProbingPacMap` by its key, THEN the map "
            + "should report that it no longer contains that key.")
    @Test
    void testContainsKeyAfterRemove() {
        ProbingPacMap<String, Integer> map = new ProbingPacMap<>();
        map.put("hello",4);
        map.remove("hello");
        assertFalse(map.containsKey("hello"));
    }

    /**
     * A class with a poorly-chosen hashCode that can be used to help test that our `ProbingPacMap`
     * correctly handles hash collisions.
     */
    record StringBadHash(String str) {
        @Override
        public int hashCode() {
            return str.length(); // hash this object to the length of its `str` field
        }
    }

    @DisplayName("WHEN we `put()` two entries with colliding keys into a `ProbingPacMap`, THEN the "
            + "map should report that it contains both of those keys.")
    @Test
    void testContainsKeyCollision() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        StringBadHash aaa = new StringBadHash("AAA");
        StringBadHash bbb = new StringBadHash("BBB");

        map.put(aaa, 1);
        map.put(bbb, 2);
        assertTrue(map.containsKey(aaa));
        assertTrue(map.containsKey(bbb));
    }

    // TODO 4: Add additional unit tests to cover the `ProbingPacMap` class.
}