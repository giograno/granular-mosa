package org.evosuite.strategy;

import org.evosuite.Properties;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.mosa.MOSA;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tests generation combining SMOSA with MOSA (two-step approach)
 */
public class BoostedTestingStrategy extends TestGenerationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BoostedTestingStrategy.class);
    /**
     * The result of a generation should be the obtained test suites and the yet to cover targets
     */
    private class GenerationResults {
        // stores the best suite resulting from a step
        public TestSuiteChromosome bestSuite;
        // stores the list of fitness functions that still need to be covered
        public List<TestFitnessFunction> yetToCoverFitnessFunctions;
        // flag for full achieved full coverage
        public boolean fullCoverage = false;
        // size of the last set of fitness functions
        public int fitnessFunctionSize = 0;
    }

    @Override
    public TestSuiteChromosome generateTests() {
        GenerationResults firstStep = generateTestsPerStep(Properties.Algorithm.SMOSA, null);
        if (firstStep.fullCoverage)
            return firstStep.bestSuite;
        GenerationResults secondStep = generateTestsPerStep(Properties.Algorithm.MOSA, firstStep);
        firstStep.bestSuite.addTests(secondStep.bestSuite.getTestChromosomes());
        sendExecutionStatistics();
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals,
                firstStep.fitnessFunctionSize - (secondStep.fitnessFunctionSize-firstStep.fitnessFunctionSize));
        return firstStep.bestSuite;
    }

    private GenerationResults generateTestsPerStep(Properties.Algorithm chosen,
                                                   GenerationResults previousStepResults) {
        GenerationResults generationResults = new GenerationResults();
        Properties.ALGORITHM = chosen;
        logger.debug("Starting step with: " + chosen);
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
                // todo: here it needs to return the set of uncovered goals
                Set uncoveredGoals = ((MOSA) algorithm).getUncoveredGoals();
                logger.debug("Number of goals still to cover: " + uncoveredGoals.size());
                logger.debug("Number of covered goals: " + testSuite.getCoveredGoals().size());
                generationResults.yetToCoverFitnessFunctions = new ArrayList<>(uncoveredGoals);
            }
        } else {
            zeroFitness.setFinished();
            testSuite = new TestSuiteChromosome();
            for (FitnessFunction<?> ff : testSuite.getFitnessValues().keySet())
                testSuite.setCoverage(ff, 1.0);
            generationResults.bestSuite = testSuite;
            generationResults.fullCoverage = true;
        }
        generationResults.fitnessFunctionSize = algorithm.getFitnessFunctions().size();

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

        return generationResults;
    }
}