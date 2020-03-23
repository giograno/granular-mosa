package org.evosuite;

public class SmellFreeMOSALauncher {

    public static void main(String[] args) {

        String[] command = {
                "-generateMOSuite",
                "-Dcriterion=BRANCH",
                "-Dconfiguration_id=test",
                "-Dminimize=true",
                "-Dpopulation=50",
                "-Dalgorithm=SMOSA",
                "-Dsort_calls=true",
                "-Dmap_CUT_calls=true",
                "-Dassertions=false",
                "-Dno_et=1",
                "-Dstopping_condition=MAXGENERATIONS",
                "-Dsearch_budget=1",
                "-Dclient_on_thread=true",
                "-Doutput_variables=TARGET_CLASS,criterion,configuration_id,algorithm,Total_Goals,Covered_Goals," +
                        "Generations,Statements_Executed,Fitness_Evaluations,Tests_Executed,Generations,Total_Time," +
                        "Size,Result_Size,Length,Result_Length,BranchCoverage,NoEagerTest",
                "-projectCP",
                "/Users/grano/Documents/SF110/27_gangup/gangup.jar:/Users/grano/Documents/SF110/27_gangup/lib/xith-md2.jar:/Users/grano/Documents/SF110/27_gangup/lib/js.jar:/Users/grano/Documents/SF110/27_gangup/lib/xith-obj.jar:/Users/grano/Documents/SF110/27_gangup/lib/joal.jar:/Users/grano/Documents/SF110/27_gangup/lib/vecmath.jar:/Users/grano/Documents/SF110/27_gangup/lib/xith3d.jar:/Users/grano/Documents/SF110/27_gangup/lib/jogl.jar:/Users/grano/Documents/SF110/27_gangup/lib/skinlf.jar",
                "-class",
                "state.Player"
        };

        EvoSuite.main(command);
        // test: we love simone scalabrino
    }
}
