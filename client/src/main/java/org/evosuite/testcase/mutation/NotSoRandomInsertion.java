package org.evosuite.testcase.mutation;

import org.evosuite.Properties;
import org.evosuite.setup.TestCluster;
import org.evosuite.testcase.ConstraintHelper;
import org.evosuite.testcase.ConstraintVerifier;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As the same behavior as {@link RandomInsertion} with the difference that handles the calls to the CUT
 * in such a way no Eager Tests are created (via {@code insertCallNoEager} method)
 */
public class NotSoRandomInsertion extends RandomInsertion {
    private static final Logger logger = LoggerFactory.getLogger(NotSoRandomInsertion.class);

    @Override
    public int insertStatement(TestCase test, int lastPosition) {
        double random = Randomness.nextDouble();
        int oldSize = test.size();

        int position = 0;

        assert Properties.INSERTION_UUT + Properties.INSERTION_ENVIRONMENT + Properties.INSERTION_PARAMETER == 1.0;

        /** UUT should stay for Unit Under Test, i.e., a call to the CUT*/
        boolean insertUUT = Properties.INSERTION_UUT > 0 &&
                random <= Properties.INSERTION_UUT && TestCluster.getInstance().getNumTestCalls() > 0 ;

        boolean insertEnv = !insertUUT && Properties.INSERTION_ENVIRONMENT > 0 &&
                random > Properties.INSERTION_UUT && random  <= Properties.INSERTION_UUT+Properties.INSERTION_ENVIRONMENT &&
                TestCluster.getInstance().getNumOfEnvironmentCalls() > 0;

        boolean insertParam = !insertUUT && !insertEnv;
        boolean success = false;

        if (insertUUT) {
            position = test.size();
            /** todo need to implement this insertCallNoEager method*/
            success = TestFactory.getInstance().insertCallNoEager(test, lastPosition+1);
        } else if (insertEnv) {
            position = TestFactory.getInstance().insertRandomCallOnEnvironment(test,lastPosition);
            success = (position >= 0);
        } else if (insertParam) {
            VariableReference var = selectRandomVariableForCall(test, lastPosition);
            if (var != null) {
                int lastUsage = var.getStPosition();

                for (VariableReference usage: test.getReferences(var)) {
                    if (usage.getStPosition() > lastUsage)
                        lastUsage = usage.getStPosition();
                }

                int boundPosition = ConstraintHelper.getLastPositionOfBounded(var, test);
                if(boundPosition >= 0 ){
                    position = boundPosition + 1;
                } else {
                    if (lastUsage > var.getStPosition() + 1) {
                        position = Randomness.nextInt(var.getStPosition() + 1, // call has to be after the object is created
                                lastUsage                // but before the last usage
                        );
                    } else if(lastUsage == var.getStPosition()) {
                        // The variable isn't used
                        position = lastUsage + 1;
                    } else {
                        // The variable is used at only one position, we insert at exactly that position
                        position = lastUsage;
                    }
                    /** todo: random call on object also need to be checked; maybe these are calls to other
                     * classes, and not to the CUT; in case, this is fine */
                    success = TestFactory.getInstance().insertRandomCallOnObjectAt(test, var, position);
                }
                if (!success && TestCluster.getInstance().getNumTestCalls() > 0) {
                    logger.debug("Adding new call on UUT because var was null");
                    position = test.size();
                }
                success = TestFactory.getInstance().insertRandomCall(test, position);
            }
        }

        if (test.size() - oldSize > 1)
            position += (test.size() - oldSize - 1);

        if (success) {
            assert ConstraintVerifier.verifyTest(test);
            assert ! ConstraintVerifier.hasAnyOnlyForAssertionMethod(test);

            return position;
        } else
            return -1;

    }
}
