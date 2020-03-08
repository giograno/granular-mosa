package org.evosuite;

public class SmellFreeMOSALauncher {

    public static void main(String[] args) {

        String[] command = {
                "-generateMOSuite",
                "-Dcriterion=BRANCH",
                "-Dconfiguration_id=test",
                "-Dminimize=true",
                "-Dpopulation=50",
                "-Dalgorithm=MOSA",
                "-Dmap_CUT_calls=true",
                "-Dassertions=false",
                "-Dno_et=1",
                "-Dstopping_condition=MAXGENERATIONS",
                "-Dsearch_budget=1",
                "-Dclient_on_thread=false",
                "-Doutput_variables=TARGET_CLASS,criterion,configuration_id,algorithm,Total_Goals,Covered_Goals," +
                        "Generations,Statements_Executed,Fitness_Evaluations,Tests_Executed,Generations,Total_Time," +
                        "Size,Result_Size,Length,Result_Length,BranchCoverage,NoEagerTest",
                "-projectCP",
                "/Users/grano/Documents/SF110/107_weka/weka.jar:/Users/grano/Documents/SF110/107_weka/lib/packageManager.jar:/Users/grano/Documents/SF110/107_weka/lib/JFlex.jar:/Users/grano/Documents/SF110/107_weka/lib/java-cup.jar",
                "-class",
                "weka.classifiers.bayes.net.search.ci.ICSSearchAlgorithm"
        };

        EvoSuite.main(command);
        // test: we love simone scalabrino
    }
}
