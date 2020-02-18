/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.setup;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.evosuite.runtime.RuntimeSettings;
import org.evosuite.utils.generic.GenericAccessibleObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TestClusterGeneratorTest {

	private static final boolean defaultVFS = RuntimeSettings.useVFS;
	
	@After
	public void tearDown(){
		RuntimeSettings.useVFS = defaultVFS;
	}
	
	@Test
	public void test_checkIfCanUse_noVFS(){
		
		RuntimeSettings.useVFS = false;
		boolean canUse = TestClusterUtils.checkIfCanUse(File.class.getCanonicalName());
		Assert.assertTrue(canUse);
	}

	@Test
	public void test_checkIfCanUse_withVFS(){
		
		RuntimeSettings.useVFS = true;
		boolean canUse = TestClusterUtils.checkIfCanUse(File.class.getCanonicalName());
		Assert.assertFalse(canUse);
	}

	@Test
	public void testMap() {
		final class MyEntry implements Map.Entry<String, Integer> {
			private final String key;
			private Integer value;

			public MyEntry(String key, Integer value) {
				this.key = key;
				this.value = value;
			}

			@Override
			public String getKey() {
				return key;
			}

			@Override
			public Integer getValue() {
				return value;
			}

			@Override
			public Integer setValue(Integer value) {
				Integer old = this.value;
				this.value = value;
				return old;
			}
		}
		PriorityQueue<MyEntry> sortedCells = new PriorityQueue<>(1000, (o1, o2) -> {
			if (o1.value < o2.value)
				return -1;
			else if (o1.value > o2.value)
				return 1;
			else
				return 0;
		});
		PriorityQueue<Map.Entry<GenericAccessibleObject<?>, Integer>> sortedCelld =
				new PriorityQueue<>(1000, (o1, o2) -> {
			if (o1.getValue() < o2.getValue())
				return -1;
			else if (o1.getValue() > o2.getValue())
				return 1;
			else
				return 0;
		});
		sortedCells.add(new MyEntry("test_method_4", 4));
		sortedCells.add(new MyEntry("test method_1", 1));
		sortedCells.add(new MyEntry("test method_3", 3));
		MyEntry poll = sortedCells.poll();
		Assert.assertTrue(poll.value == 1);
		MyEntry peek = sortedCells.peek();
		Assert.assertTrue(peek.value == 3);
	}
}
