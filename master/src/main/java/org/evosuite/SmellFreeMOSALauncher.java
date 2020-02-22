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
                "-Dmap_CUT_calls=true",
                "-Dassertions=true",
                "-Dsearch_budget=180",
                "-Dclient_on_thread=false",
                "-Doutput_variables=TARGET_CLASS,criterion,configuration_id,algorithm,Total_Goals,Covered_Goals," +
                        "Generations,Statements_Executed,Fitness_Evaluations,Tests_Executed,Generations,Total_Time," +
                        "Size,Result_Size,Length,Result_Length,BranchCoverage,NoEagerTest",
                "-projectCP",
                "/Users/grano/Documents/SF110/24_saxpath/saxpath.jar:/Users/grano/Documents/SF110/24_saxpath/lib/ant-1.3.jar:/Users/grano/Documents/SF110/24_saxpath/lib/xalan.jar:/Users/grano/Documents/SF110/24_saxpath/lib/xerces.jar:/Users/grano/Documents/SF110/24_saxpath/lib/jakarta-ant-1.3-optional.jar",
                "-class",
                "com.werken.saxpath.XPathLexer"
        };

        EvoSuite.main(command);
        // test: we love simone scalabrino
    }
}
