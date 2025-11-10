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
    @DisplayName("WHEN multiple entries with colliding keys are inserted, THEN all values should be retrievable")
    @Test
    void testMultipleCollisions() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        StringBadHash aaa = new StringBadHash("AAA");
        StringBadHash bbb = new StringBadHash("BBB");
        StringBadHash ccc = new StringBadHash("CCC"); // same length → same hash

        map.put(aaa, 1);
        map.put(bbb, 2);
        map.put(ccc, 3);

        assertEquals(1, map.get(aaa));
        assertEquals(2, map.get(bbb));
        assertEquals(3, map.get(ccc));
        assertTrue(map.containsKey(aaa));
        assertTrue(map.containsKey(bbb));
        assertTrue(map.containsKey(ccc));
    }

    @DisplayName("WHEN an entry is removed in the middle of a collision chain, THEN subsequent entries should still be reachable")
    @Test
    void testRemoveMiddleCollision() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        StringBadHash aaa = new StringBadHash("AAA");
        StringBadHash bbb = new StringBadHash("BBB");
        StringBadHash ccc = new StringBadHash("CCC"); // all collide

        map.put(aaa, 1);
        map.put(bbb, 2);
        map.put(ccc, 3);

        map.remove(bbb);

        assertFalse(map.containsKey(bbb));
        assertEquals(1, map.get(aaa));  // still reachable
        assertEquals(3, map.get(ccc));  // still reachable
    }

    @DisplayName("WHEN many entries are inserted to exceed max load factor, THEN the map should resize and all entries should remain accessible")
    @Test
    void testResizeKeepsAllEntries() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        int n = 20; // initial capacity is 16, max load factor 0.5 → triggers resize

        for (int i = 0; i < n; i++) {
            StringBadHash key = new StringBadHash("K" + i);
            map.put(key, i);
        }

        // all entries should be retrievable
        for (int i = 0; i < n; i++) {
            StringBadHash key = new StringBadHash("K" + i);
            assertEquals(i, map.get(key));
        }

        // map size should be correct
        assertEquals(n, map.size());
    }

    @DisplayName("WHEN removing entries after resizing, THEN subsequent collision chains should still resolve correctly")
    @Test
    void testRemoveAfterResize() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        int n = 20;

        // insert many entries to trigger resize
        for (int i = 0; i < n; i++) {
            StringBadHash key = new StringBadHash("K" + i);
            map.put(key, i);
        }

        // remove a few entries
        for (int i = 0; i < 5; i++) {
            StringBadHash key = new StringBadHash("K" + i);
            map.remove(key);
        }

        // removed keys should not exist
        for (int i = 0; i < 5; i++) {
            StringBadHash key = new StringBadHash("K" + i);
            assertFalse(map.containsKey(key));
        }

        // remaining keys should still be retrievable
        for (int i = 5; i < n; i++) {
            StringBadHash key = new StringBadHash("K" + i);
            assertEquals(i, map.get(key));
        }
    }

    @DisplayName("WHEN removing entries, tombstones are not reused immediately")
    @Test
    void testTombstonesNotReused() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        StringBadHash aaa = new StringBadHash("AAA");
        StringBadHash bbb = new StringBadHash("BBB");
        StringBadHash ccc = new StringBadHash("CCC"); // all collide

        map.put(aaa, 1);
        map.put(bbb, 2);

        map.remove(aaa); // tombstone created

        // Inserting ccc should go to next free index, NOT overwrite tombstone
        map.put(ccc, 3);

        assertFalse(map.containsKey(aaa));
        assertEquals(2, map.get(bbb));
        assertEquals(3, map.get(ccc));

        // size should reflect only current entries
        assertEquals(3, map.size());
    }

    @DisplayName("WHEN resizing occurs, THEN tombstones are cleared and all entries are rehashed")
    @Test
    void testResizeClearsTombstones() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        int n = 20; // initial capacity 16, load factor triggers resize

        for (int i = 0; i < n; i++) {
            map.put(new StringBadHash("K" + i), i);
        }

        // remove a few entries (creates tombstones)
        for (int i = 0; i < 5; i++) {
            map.remove(new StringBadHash("K" + i));
        }
        assertEquals(20, map.size());


        // forcibly trigger a resize
        for (int i = 20; i < 40; i++) {
            map.put(new StringBadHash("K" + i), i);
        }
        // All remaining entries should be reachable
        for (int i = 5; i < n; i++) {
            assertEquals(i, map.get(new StringBadHash("K" + i)));
        }

        for (int i = 20; i < 40; i++) {
            assertEquals(i, map.get(new StringBadHash("K" + i)));
        }

        // Removed entries should still not exist
        for (int i = 0; i < 5; i++) {
            assertFalse(map.containsKey(new StringBadHash("K" + i)));
        }

        // size should be correct after resize
        assertEquals(35, map.size());
    }

    @DisplayName("WHEN many collisions exist and some entries are removed, THEN linear probing still works after resize")
    @Test
    void testCollisionChainAfterResize() {
        ProbingPacMap<StringBadHash, Integer> map = new ProbingPacMap<>();
        StringBadHash aaa = new StringBadHash("AAA");
        StringBadHash bbb = new StringBadHash("BBB");
        StringBadHash ccc = new StringBadHash("CCC");

        map.put(aaa, 1);
        map.put(bbb, 2);
        map.put(ccc, 3);

        map.remove(bbb); // middle tombstone

        // insert more entries to trigger resize
        for (int i = 0; i < 20; i++) {
            map.put(new StringBadHash("Key" + i), i);
        }

        // removed entry should remain absent
        assertFalse(map.containsKey(bbb));

        // original entries still retrievable
        assertEquals(1, map.get(aaa));
        assertEquals(3, map.get(ccc));
    }


    // TODO 4: Add additional unit tests to cover the `ProbingPacMap` class.
}