/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.immutable;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JUnit test for {@link ImmutableArrayList}.
 */
public class ImmutableArrayListTest extends AbstractImmutableListTestCase
{
    @Override
    protected ImmutableList<Integer> classUnderTest()
    {
        return this.newList(1, 2, 3);
    }

    @Test
    public void newWith()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3);
        ImmutableList<Integer> with = list.newWith(4);
        assertNotEquals(list, with);
        assertEquals(FastList.newListWith(1, 2, 3, 4), with);
    }

    @Test
    public void newWithAll()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3);
        ImmutableList<Integer> withAll = list.newWithAll(FastList.newListWith(4, 5));
        assertNotEquals(list, withAll);
        assertEquals(FastList.newListWith(1, 2, 3, 4, 5), withAll);
    }

    @Test
    public void newWithOut()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3, 4);
        ImmutableList<Integer> without4 = list.newWithout(4);
        assertNotEquals(list, without4);
        assertEquals(FastList.newListWith(1, 2, 3), without4);

        ImmutableList<Integer> without1 = list.newWithout(1);
        assertNotEquals(list, without1);
        assertEquals(FastList.newListWith(2, 3, 4), without1);

        ImmutableList<Integer> without0 = list.newWithout(0);
        assertSame(list, without0);

        ImmutableList<Integer> without5 = list.newWithout(5);
        assertSame(list, without5);
    }

    @Test
    public void newWithoutAll()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3, 4, 5);
        ImmutableList<Integer> withoutAll = list.newWithoutAll(FastList.newListWith(4, 5));
        assertNotEquals(list, withoutAll);
        assertEquals(FastList.newListWith(1, 2, 3), withoutAll);
        assertEquals(FastList.newListWith(1, 2, 3), list.newWithoutAll(HashBag.newBagWith(4, 4, 5)));
        ImmutableList<Integer> largeList = this.newList(Interval.oneTo(20).toArray());
        ImmutableList<Integer> largeWithoutAll = largeList.newWithoutAll(FastList.newList(Interval.oneTo(10)));
        assertEquals(FastList.newList(Interval.fromTo(11, 20)), largeWithoutAll);
        ImmutableList<Integer> largeWithoutAll2 = largeWithoutAll.newWithoutAll(Interval.fromTo(11, 15));
        assertEquals(FastList.newList(Interval.fromTo(16, 20)), largeWithoutAll2);
        ImmutableList<Integer> largeWithoutAll3 =
                largeWithoutAll2.newWithoutAll(UnifiedSet.newSet(Interval.fromTo(16, 19)));
        assertEquals(FastList.newListWith(20), largeWithoutAll3);
    }

    private ImmutableArrayList<Integer> newList(Integer... elements)
    {
        return ImmutableArrayList.newListWith(elements);
    }

    private ImmutableList<Integer> newListWith(int... littleElements)
    {
        Integer[] bigElements = new Integer[littleElements.length];
        for (int i = 0; i < littleElements.length; i++)
        {
            bigElements[i] = littleElements[i];
        }
        return ImmutableArrayList.newListWith(bigElements);
    }

    @Test
    public void newListWith()
    {
        ImmutableList<Integer> collection = ImmutableArrayList.newListWith(1);
        assertTrue(collection.notEmpty());
        assertEquals(1, collection.size());
        assertTrue(collection.contains(1));
    }

    @Test
    public void newListWithVarArgs()
    {
        ImmutableList<Integer> collection = this.newListWith(1, 2, 3, 4);
        assertTrue(collection.notEmpty());
        assertEquals(4, collection.size());
        assertTrue(collection.containsAllArguments(1, 2, 3, 4));
        assertTrue(collection.containsAllIterable(Interval.oneTo(4)));
    }

    @Test
    public void toSet()
    {
        ImmutableArrayList<Integer> integers = ImmutableArrayList.newListWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Test
    public void toMap()
    {
        ImmutableArrayList<Integer> integers = ImmutableArrayList.newListWith(1, 2, 3, 4);
        MutableMap<String, String> map =
                integers.toMap(String::valueOf, String::valueOf);
        assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void serialization()
    {
        ImmutableList<Integer> collection = ImmutableArrayList.newListWith(1, 2, 3, 4, 5);
        ImmutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        assertEquals(5, deserializedCollection.size());
        assertTrue(deserializedCollection.containsAllArguments(1, 2, 3, 4, 5));
        Verify.assertEqualsAndHashCode(collection, deserializedCollection);
    }

    @Test
    public void forEachWithIndexIllegalFrom()
    {
        MutableList<Integer> result = Lists.mutable.of();
        assertThrows(IndexOutOfBoundsException.class, () -> this.newList(1, 2).forEachWithIndex(-1, 2, new AddToList(result)));
    }

    @Test
    public void forEachWithIndexIllegalTo()
    {
        MutableList<Integer> result = Lists.mutable.of();
        assertThrows(IndexOutOfBoundsException.class, () -> this.newList(1, 2).forEachWithIndex(1, -2, new AddToList(result)));
    }

    @Test
    @Override
    public void get()
    {
        ImmutableList<Integer> list = this.classUnderTest();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.get(list.size() + 1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    @Override
    public void iteratorRemove()
    {
        try
        {
            this.classUnderTest().iterator().remove();
            fail("Should not reach here! Exception should be thrown on previous line.");
        }
        catch (Exception e)
        {
            assertTrue(e instanceof IllegalStateException || e instanceof UnsupportedOperationException);
        }
    }

    @Test
    public void groupByUniqueKey()
    {
        assertEquals(
                UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3),
                this.classUnderTest().groupByUniqueKey(id -> id));
    }

    @Test
    public void groupByUniqueKey_throws()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest().groupByUniqueKey(Functions.getFixedValue(1)));
    }

    @Test
    public void groupByUniqueKey_target()
    {
        MutableMap<Integer, Integer> integers =
                this.classUnderTest().groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0));
        assertEquals(UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3), integers);
    }

    @Test
    public void groupByUniqueKey_target_throws()
    {
        assertThrows(IllegalStateException.class, () -> this.classUnderTest().groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(2, 2)));
    }

    @Test
    public void getOnly()
    {
        ImmutableList<Integer> list = this.newList(2);
        assertEquals(Integer.valueOf(2), list.getOnly());
    }

    @Test
    public void getOnly_exception_when_empty()
    {
        ImmutableList<Integer> list = this.newList();
        assertThrows(IllegalStateException.class, () -> list.getOnly());
    }

    @Test
    public void getOnly_exception_when_multiple_items()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3);
        assertThrows(IllegalStateException.class, () -> list.getOnly());
    }
}
