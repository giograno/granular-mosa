package org.evosuite;

public class SmellFreeMOSALauncher {

    public static void main(String[] args) {

        String[] command = {
//                "-generateMOSuite",
                "-boosted",
                "-Dcriterion=BRANCH",
                "-Dconfiguration_id=test",
                "-Dminimize=true",
                "-Dpopulation=50",
                "-Dalgorithm=SMOSA",
                "-Dstrategy=BOOSTED",
                "-Dsort_calls=true",
                "-Dmap_CUT_calls=true",
                "-Dassertions=false",
                "-Dno_et=1",
//                "-Dstopping_condition=MAXGENERATIONS",
                "-Dsearch_budget=15",
                "-Dtools_jar_location=/Users/giograno/.sdkman/candidates/java/8.0.265.hs-adpt/lib/tools.jar",
//                "-Dclient_on_thread=true",
                "-Doutput_variables=TARGET_CLASS,criterion,configuration_id,algorithm,Total_Goals,Covered_Goals," +
                        "Generations,Statements_Executed,Fitness_Evaluations,Tests_Executed,Generations,Total_Time," +
                        "Size," +
                        "Result_Size," +
                        "Length," +
                        "Result_Length," +
                        "BranchCoverage," +
                        "NoEagerTests," +
                        "Result_NoEagerTests," +
                        "FirstStep_Goals," +
                        "SecondStep_Goals," +
                        "FirstStep_Covered_Goals," +
                        "SecondStep_Covered_Goals," +
                        "FirstStep_Size," +
                        "SecondStep_Size," +
                        "FirstStep_Length," +
                        "FirstStep_Result_Length," +
                        "SecondStep_Length," +
                        "SecondStep_Result_Length," +
                        "SecondStep_Size," +
                        "FirstStep_NoEager," +
                        "FirstStep_Result_NoEager," +
                        "SecondStep_NoEager," +
                        "SecondStep_Result_NoEager",
                "-projectCP",
                "/Users/giograno/Documents/SF110/19_jmca/jmca.jar",
                "-class",
                "com.soops.CEN4010.JMCA.JParser.SimpleNode"
        };

        EvoSuite.main(command);
        // test: we love simone scalabrino
    }
}
