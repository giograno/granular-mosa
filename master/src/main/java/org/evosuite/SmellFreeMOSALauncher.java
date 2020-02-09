package org.evosuite;

public class SmellFreeMOSALauncher {

    public static void main(String[] args) {

        String[] command = {
                "-generateMOSuite",
                "-Dcriterion=BRANCH",
                "-Dconfiguration_id=test",
                "-Dminimize=FALSE",
                "-Dpopulation=50",
                "-Dalgorithm=SMOSA",
                "-Dassertions=FALSE",
                "-Dsearch_budget=30",
                "-Dclient_on_thread=false",
                "-Dminimize=false",
                "-Doutput_variables=TARGET_CLASS,criterion,configuration_id,algorithm,Total_Goals,Covered_Goals," +
                        "Generations,Statements_Executed,Fitness_Evaluations,Tests_Executed,Generations,Total_Time," +
                        "Size,Result_Size,Length,Result_Length,BranchCoverage",
                "-projectCP",
                "/Users/grano/Documents/SF110/19_jmca/jmca.jar",
                "-class",
                "com.soops.CEN4010.JMCA.JParser.JavaCharStream"
        };

        EvoSuite.main(command);
        // test: we love simone scalabrino
    }
}
