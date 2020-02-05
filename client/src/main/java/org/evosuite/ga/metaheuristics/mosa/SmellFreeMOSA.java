package org.evosuite.ga.metaheuristics.mosa;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestChromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /**
     * todo: this needs to be overridden such that we keep doing the crossover operations since we get a smell free
     * individual
     */
    protected List<T> breedNextGeneration() {
        return super.breedNextGeneration();
    }

    @Override
    public void initializePopulation() {
        notifySearchStarted();
        currentIteration = 0;

        generateSmellyFreeInitialPopulation(Properties.POPULATION);
        // Determine fitness
        calculateFitness();
        this.notifyIteration();
    }

    private void generateSmellyFreeInitialPopulation(int sizePopulation) {
        int counter = 0;
        for (int i = 0; i < sizePopulation; i++) {

            T individual = null;

            do {
                individual = this.chromosomeFactory.getChromosome();
                calculateFitness(individual);
                ((TestChromosome)individual).computeEagerTest();
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
}
