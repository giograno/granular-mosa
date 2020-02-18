package org.evosuite.testcase.factories;

import org.evosuite.Properties;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.utils.Randomness;

public class NoEagerTestFactory extends RandomLengthTestFactory{

    @Override
    public TestChromosome getChromosome() {
        TestChromosome c = new TestChromosome();
        c.setTestCase(getRandomTestCase(Properties.CHROMOSOME_LENGTH));
        return c;
    }

    private TestCase getRandomTestCase(int size) {
        boolean tracerEnabled = ExecutionTracer.isEnabled();
        if (tracerEnabled)
            ExecutionTracer.disable();

        TestCase test = getNewTestCase();
        int num = 0;

        // Choose a random length in 0 - size
        int length = Randomness.nextInt(size);
        while (length == 0)
            length = Randomness.nextInt(size);

        TestFactory testFactory = TestFactory.getInstance();

        // Then add random stuff
        while (test.size() < length && num < Properties.MAX_ATTEMPTS) {
            testFactory.insertNotReallyRandomStatement(test, test.size() - 1);
            num++;
        }
        if (logger.isDebugEnabled())
            logger.debug("Randomized test case:" + test.toCode());

        if (tracerEnabled)
            ExecutionTracer.enable();

        return test;
    }

}
