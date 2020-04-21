package org.evosuite.strategy;

import org.evosuite.Properties;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.result.TestGenerationResultBuilder;
import org.evosuite.rmi.ClientServices;
import org.evosuite.rmi.service.ClientState;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.factories.RandomLengthTestFactory;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests generation combining SMOSA with MOSA (two-step approach)
 */
public class BoostedTestingStrategy extends TestGenerationStrategy {

    /**
     * The result of a generation should be the obtained test suites and the yet to cover targets
     */
    private class GenerationResults {
        public TestSuiteChromosome bestSuite;
        public List<TestFitnessFunction> yetToCoverFitnessFunctions;
        public boolean fullCoverage = false;
    }

    @Override
    public TestSuiteChromosome generateTests() {
        GenerationResults firstStep = generateTestsPerStep(Properties.Algorithm.SMOSA, null);
        if (firstStep.fullCoverage)
            return firstStep.bestSuite;
        GenerationResults secondStep = generateTestsPerStep(Properties.Algorithm.MOSA, firstStep);
        firstStep.bestSuite.addTests(secondStep.bestSuite.getTestChromosomes());
        return firstStep.bestSuite;
    }

    private GenerationResults generateTestsPerStep(Properties.Algorithm chosen,
                                                   GenerationResults previousStepResults) {
        GenerationResults generationResults = new GenerationResults();
        Properties.ALGORITHM = chosen;
        PropertiesSuiteGAFactory algorithmFactory = new PropertiesSuiteGAFactory();
        GeneticAlgorithm<TestSuiteChromosome> algorithm = algorithmFactory.getSearchAlgorithm();

        ChromosomeFactory factory = new RandomLengthTestFactory();
        algorithm.setChromosomeFactory(factory);

        if(Properties.SERIALIZE_GA || Properties.CLIENT_ON_THREAD)
            TestGenerationResultBuilder.getInstance().setGeneticAlgorithm(algorithm);

        long startTime = System.currentTimeMillis() / 1000;

        List<TestFitnessFunction> fitnessFunctions = new ArrayList<>();
        if (previousStepResults == null) {
            List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
            for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories)
                fitnessFunctions.addAll(goalFactory.getCoverageGoals());
        } else
            /** add previously uncovered goals */
            fitnessFunctions = ((List)previousStepResults.yetToCoverFitnessFunctions);

        algorithm.addFitnessFunctions((List)fitnessFunctions);
        algorithm.addListener(progressMonitor);

        enableTestCalls();

        algorithm.resetStoppingConditions();

        TestSuiteChromosome testSuite;

        if (!(Properties.STOP_ZERO && fitnessFunctions.isEmpty()) || ArrayUtil.contains(Properties.CRITERION, Properties.Criterion.EXCEPTION)) {
            LoggingUtils.getEvoLogger().info("* Using seed {}", Randomness.getSeed());
            LoggingUtils.getEvoLogger().info("* Starting evolution");
            ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

            algorithm.generateSolution();
            List<TestSuiteChromosome> bestSuites = algorithm.getBestIndividuals();
            if (bestSuites.isEmpty()) {
                LoggingUtils.getEvoLogger().warn("Could not find any suitable chromosome");
                return null;
            } else {
                testSuite = bestSuites.get(0);
                generationResults.bestSuite = testSuite;
            }
        } else {
            zeroFitness.setFinished();
            testSuite = new TestSuiteChromosome();
            for (FitnessFunction<?> ff : testSuite.getFitnessValues().keySet())
                testSuite.setCoverage(ff, 1.0);
            generationResults.bestSuite = testSuite;
            generationResults.fullCoverage = true;
        }

        long endTime = System.currentTimeMillis() / 1000;

        if (Properties.SHOW_PROGRESS)
            LoggingUtils.getEvoLogger().info("");

        String text = " statements, best individual has fitness: ";
        LoggingUtils.getEvoLogger().info("* Search finished after "
                + (endTime - startTime)
                + "s and "
                + algorithm.getAge()
                + " generations, "
                + MaxStatementsStoppingCondition.getNumExecutedStatements()
                + text
                + testSuite.getFitness());
        // Search is finished, send statistics
        sendExecutionStatistics();

        // We send the info about the total number of coverage goals/targets only after
        // the end of the search. This is because the number of coverage targets may vary
        // when the criterion Properties.Criterion.EXCEPTION is used (exception coverage
        // goal are dynamically added when the generated tests trigger some exceptions
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, algorithm.getFitnessFunctions().size());

        return generationResults;
    }
}
