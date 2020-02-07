package org.evosuite.ga.metaheuristics.mosa;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SmellFreeMOSA <T extends Chromosome> extends MOSA<T> {

    private static final Logger logger = LoggerFactory.getLogger(SmellFreeMOSA.class);

    /**
     * Constructor based on the abstract class {@link AbstractMOSA}
     *
     * @param factory
     */
    public SmellFreeMOSA(ChromosomeFactory<T> factory) {
        super(factory);
    }

    @Override
    @SuppressWarnings("Duplicates")
    /**
     * Modified version of the breeding.
     * The crossover is repeated till at least one of the two generated offsprings are smell free.
     * The same is done for the newly randomly generated tests.
     */
    protected List<T> breedNextGeneration() {
        List<T> offspringPopulation = new ArrayList<>(Properties.POPULATION);

        for (int i=0; i < Properties.POPULATION/2 && !isFinished(); i++) {
            T parent1 = selectionFunction.select(population);
            T parent2 = selectionFunction.select(population);
            T offspring1 = (T) parent1.clone();
            T offspring2 = (T) parent2.clone();

            /* at least one offspring needs to not be smelly */
            boolean isSmelly = true;

            while (isSmelly) {
                try {
                    if (Randomness.nextDouble() <= Properties.CROSSOVER_RATE) {
                        crossoverFunction.crossOver(offspring1, offspring2);
                    }
                } catch (ConstructionFailedException e) {
                    logger.debug("CrossOver failed.");
                    continue;
                }

                removeUnusedVariables(offspring1);
                removeUnusedVariables(offspring2);

                mutate(offspring1, parent1);
                if (offspring1.isChanged()) {
                    clearCachedResults(offspring1);
                    offspring1.updateAge(currentIteration);
                    calculateFitness(offspring1);
                    if (offspring1.isSmellFree()) {
                        offspringPopulation.add(offspring1);
                        isSmelly = false;
                    }
                }

                mutate(offspring2, parent2);
                if (offspring2.isChanged()) {
                    clearCachedResults(offspring2);
                    offspring2.updateAge(currentIteration);
                    calculateFitness(offspring2);
                    if (offspring2.isSmellFree()) {
                        offspringPopulation.add(offspring2);
                        isSmelly = false;
                    }
                }
            }
        }
        // Add new randomly generate tests
        for (int i = 0; i<Properties.POPULATION * Properties.P_TEST_INSERTION; i++){
            T tch;
            do {
                if (this.getCoveredGoals().size() == 0 || Randomness.nextBoolean()) {
                    tch = this.chromosomeFactory.getChromosome();
                    tch.setChanged(true);
                } else {
                    tch = (T) Randomness.choice(getArchive()).clone();
                    tch.mutate();
                    tch.mutate();
                }
                if (tch.isChanged()) {
                    tch.updateAge(currentIteration);
                    calculateFitness(tch);
                    if (tch.isSmellFree())
                        offspringPopulation.add(tch);
                }
            } while (!tch.isSmellFree());
        }
        logger.info("Number of offsprings = {}", offspringPopulation.size());
        return offspringPopulation;
    }

    @Override
    public void initializePopulation() {
        notifySearchStarted();
        currentIteration = 0;

        generateSmellyFreeInitialPopulation(Properties.POPULATION);
        calculateFitness();
        this.notifyIteration();
    }

    /**
     * Generates an initial population that is free of eager test smell
     * @param sizePopulation the size of the population
     */
    private void generateSmellyFreeInitialPopulation(int sizePopulation) {
        int counter = 0;
        for (int i = 0; i < sizePopulation; i++) {

            T individual;

            do {
                individual = this.chromosomeFactory.getChromosome();
                calculateFitness(individual);
                counter++;
            } while (!individual.isSmellFree());

            for (FitnessFunction<?> fitnessFunction : this.fitnessFunctions)
                individual.addFitness(fitnessFunction);

            this.population.add(individual);
            if (isFinished())
                break;
        }

        logger.debug("Size Archive = " + this.getArchive().size());
        logger.debug("generated " + counter + " for a population of size " + sizePopulation);
    }

    @Override
    protected void calculateFitness(T c) {
        super.calculateFitness(c);
        ((TestChromosome)c).computeEagerTest();
    }
}
