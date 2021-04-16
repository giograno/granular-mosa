package org.evosuite.testsuite;

import org.evosuite.Properties;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.rmi.ClientServices;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.statistics.StatisticsSender;
import org.evosuite.testcase.TestFitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handles the minimization in case of a boosting approach
 */
public class BoostedTestSuiteMinimizer extends TestSuiteMinimizer {
    /**
     * Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(BoostedTestSuiteMinimizer.class);

    public BoostedTestSuiteMinimizer(List<TestFitnessFactory<? extends TestFitnessFunction>> factories) {
        super(factories);
    }

    /**
     * Performs the minimization on a single part of the boosting approach
     *
     * @param flag  first or second approach
     * @param suite the suite to minimize
     */
    public void minimize(int flag, TestSuiteChromosome suite) {
        startTime = System.currentTimeMillis();

        Properties.SecondaryObjective strategy = Properties.SECONDARY_OBJECTIVE[0];

        trackSteps(flag, suite, true);

        logger.info("Minimization Strategy for step " + flag + ": " + strategy + ", " + suite.size() + " tests");
        suite.clearMutationHistory();

        StatisticsSender.computeEagerTestInformation(suite, flag, true);
        minimizeTests(suite);
        StatisticsSender.computeEagerTestInformation(suite, flag, false);

        trackSteps(flag, suite, false);
    }

    /**
     * Tracks the results of the minimization (test suite size - that should be the same) for the two different steps
     *
     * @param step  0 is the first step, 1 is the second step
     * @param suite the suite to track
     * @param preMinimization flag for pre or post minimization
     */
    private void trackSteps(int step, TestSuiteChromosome suite, boolean preMinimization) {
        if (step == 0) {
            if (preMinimization) {
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.FirstStep_Result_Size,
                        suite.size());
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.FirstStep_Result_Length,
                        suite.totalLengthOfTestCases());
            } else {
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.FirstStep_Size,
                        suite.size());
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.FirstStep_Length,
                        suite.totalLengthOfTestCases());
            }
        } else {
            if (preMinimization) {
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SecondStep_Result_Size,
                        suite.size());
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.FirstStep_Result_Length,
                        suite.totalLengthOfTestCases());
            } else {
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SecondStep_Size,
                        suite.size());
                ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SecondStep_Length,
                        suite.totalLengthOfTestCases());
            }
        }
    }
}
