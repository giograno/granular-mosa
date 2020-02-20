package org.evosuite;

public class SmellFreeMOSALauncher {

    public static void main(String[] args) {

        String[] command = {
                "-generateMOSuite",
                "-Dcriterion=BRANCH",
                "-Dconfiguration_id=test",
                "-Dminimize=FALSE",
                "-Dpopulation=50",
                "-Dalgorithm=MOSA",
                "-Dmap_CUT_calls=true",
                "-Dassertions=FALSE",
                "-Dsearch_budget=30",
                "-Dclient_on_thread=false",
                "-Doutput_variables=TARGET_CLASS,criterion,configuration_id,algorithm,Total_Goals,Covered_Goals," +
                        "Generations,Statements_Executed,Fitness_Evaluations,Tests_Executed,Generations,Total_Time," +
                        "Size,Result_Size,Length,Result_Length,BranchCoverage,EagerTest",
                "-projectCP",
                "/Users/grano/Documents/SF110/33_javaviewcontrol/javaviewcontrol.jar:/Users/grano/Documents/SF110/33_javaviewcontrol/lib/servlet.jar:/Users/grano/Documents/SF110/33_javaviewcontrol/lib/commons-fileupload-1.2.1.jar:/Users/grano/Documents/SF110/33_javaviewcontrol/lib/log4j-1.2.15.jar:/Users/grano/Documents/SF110/33_javaviewcontrol/lib/servlet-api.jar:/Users/grano/Documents/SF110/33_javaviewcontrol/lib/javacc.jar:/Users/grano/Documents/SF110/33_javaviewcontrol/lib/pmd.jar",
                "-class",
                "com.pmdesigns.jvc.tools.JVCParser"
        };

        EvoSuite.main(command);
        // test: we love simone scalabrino
    }
}
