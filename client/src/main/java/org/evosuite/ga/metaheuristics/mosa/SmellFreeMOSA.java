package org.evosuite.ga.metaheuristics.mosa;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.statements.Statement;
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
    protected List<T> breedNextGeneration() {
        List<T> offspringPopulation = new ArrayList<T>(Properties.POPULATION);
        for (int i=0; i < Properties.POPULATION/2 && !isFinished(); i++){
            T parent1 = selectionFunction.select(population);
            T parent2 = selectionFunction.select(population);
            T offspring1 = (T) parent1.clone();
            T offspring2 = (T) parent2.clone();

            mutate(offspring1, parent1);
            if (offspring1.isChanged()) {
                clearCachedResults(offspring1);
                offspring1.updateAge(currentIteration);
                calculateFitness(offspring1);
                offspringPopulation.add(offspring1);
            }
            mutate(offspring2, parent2);
            if (offspring2.isChanged()) {
                clearCachedResults(offspring2);
                offspring2.updateAge(currentIteration);
                calculateFitness(offspring2);
                offspringPopulation.add(offspring2);
            }
        }

        for (int i = 0; i<Properties.POPULATION * Properties.P_TEST_INSERTION; i++){
            T tch;
            if (this.getCoveredGoals().size() == 0 || Randomness.nextBoolean()){
                tch = this.chromosomeFactory.getChromosome();
                tch.setChanged(true);
            } else {
                tch = (T) Randomness.choice(getArchive()).clone();
                ((TestChromosome)tch).mutateET();
                ((TestChromosome)tch).mutateET();
            }
            if (tch.isChanged()) {
                tch.updateAge(currentIteration);
                calculateFitness(tch);
                offspringPopulation.add(tch);
            }
        }
        logger.info("Number of offsprings = {}", offspringPopulation.size());
        return offspringPopulation;
    }

    protected void mutate(T offspring, T parent){
        TestChromosome tch = (TestChromosome) offspring;
        tch.mutateET();
        if (!offspring.isChanged()) {
            ((TestChromosome) offspring).mutateET();
        }
        if (!hasMethodCall(offspring)) {
            tch.setTestCase(((TestChromosome) parent).getTestCase().clone());
            boolean changed = tch.mutationInsert();
            if (changed){
                for (Statement s : tch.getTestCase())
                    s.isValid();
            }
            offspring.setChanged(changed);
        }
        notifyMutation(offspring);
    }

    @Override
    protected void calculateFitness(T c) {
        super.calculateFitness(c);
    }
}
