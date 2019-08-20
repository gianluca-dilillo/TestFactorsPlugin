package processor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import data.ClassTestSmellsInfo;
import it.unisa.testSmellDiffusion.beans.ClassBean;
import it.unisa.testSmellDiffusion.beans.MethodBean;
import it.unisa.testSmellDiffusion.beans.PackageBean;
import it.unisa.testSmellDiffusion.metrics.CKMetrics;
import it.unisa.testSmellDiffusion.testMutation.TestMutationUtilities;
import it.unisa.testSmellDiffusion.testSmellRules.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class SmellynessProcessor {
    private static final Logger LOGGER = Logger.getInstance("global");

    public static ArrayList<ClassTestSmellsInfo> calculate(File root, Vector<PackageBean> packages, Vector<PackageBean> testPackages, Project proj) {
        try {
            ArrayList<ClassTestSmellsInfo> classTestSmellsInfos = new ArrayList<>();
            TestMutationUtilities utilities = new TestMutationUtilities();
            ArrayList<ClassBean> classes = utilities.getClasses(packages);
            String mainBuildPath = root.getAbsolutePath() + "\\out\\production\\" + proj.getName();
            String testBuildPath = root.getAbsolutePath() + "\\out\\test\\" + proj.getName();
            String mainPath = root.getAbsolutePath() + "\\src\\main\\java";
            String testPath = root.getAbsolutePath() + "\\src\\test\\java";
            Collection<MethodBean> methodsInTheProject = IndirectTesting.findInvocations(packages);
            File suiteTest = new File(root.getAbsolutePath() + "\\src\\test\\java");
            AssertionRoulette assertionRoulette = new AssertionRoulette();
            EagerTest eagerTest = new EagerTest();
            LazyTest lazyTest = new LazyTest();
            MysteryGuest mysteryGuest = new MysteryGuest();
            SensitiveEquality sensitiveEquality = new SensitiveEquality();
            ResourceOptimistism resourceOptimism = new ResourceOptimistism();
            ForTestersOnly forTestersOnly = new ForTestersOnly();
            IndirectTesting indirectTesting = new IndirectTesting();
            DuplicateCode duplicateCode = new DuplicateCode();
            for (ClassBean productionClass : classes) {
                ClassBean testSuite = TestMutationUtilities.getTestClassBy(productionClass.getName(), testPackages);
                ClassTestSmellsInfo classTestSmellsInfo = new ClassTestSmellsInfo();
                classTestSmellsInfo.setBelongingPackage(productionClass.getBelongingPackage());
                classTestSmellsInfo.setName(productionClass.getName());
                double isAssertionRoulette = Double.NaN;
                double isEagerTest = Double.NaN;
                double isLazyTest = Double.NaN;
                double isMysteryGuest = Double.NaN;
                double isSensitiveEquality = Double.NaN;
                double isResourceOptimistism = Double.NaN;
                double isForTestersOnly = Double.NaN;
                double isIndirectTesting = Double.NaN;
                double isDuplicateCode = Double.NaN;
                double assertionDensity = Double.NaN;
                String testSuiteName = "NO-TEST";
                if (testSuite != null) {

                    isAssertionRoulette = assertionRoulette.isAssertionRoulette(testSuite) ? 1 : 0;
                    if (isAssertionRoulette == 1) {
                        classTestSmellsInfo.setAssertionRoulette(1);
                    }

                    isEagerTest = eagerTest.isEagerTest(testSuite, productionClass) ? 1 : 0;
                    if (isEagerTest == 1) {
                        classTestSmellsInfo.setEagerTest(1);
                    }

                    isLazyTest = lazyTest.isLazyTest(testSuite, productionClass) ? 1 : 0;
                    if (isLazyTest == 1) {
                        classTestSmellsInfo.setLazyTest(1);
                    }
                    isMysteryGuest = mysteryGuest.isMysteryGuest(testSuite) ? 1 : 0;
                    if (isMysteryGuest == 1) {
                        classTestSmellsInfo.setMysteryGuest(1);
                    }

                    isSensitiveEquality = sensitiveEquality.isSensitiveEquality(testSuite) ? 1 : 0;
                    if (isSensitiveEquality == 1) {
                        classTestSmellsInfo.setMysteryGuest(1);
                    }

                    isResourceOptimistism = resourceOptimism.isResourceOptimistism(testSuite) ? 1 : 0;
                    if (isResourceOptimistism == 1) {
                        classTestSmellsInfo.setResourceOptimism(1);
                    }

                    isForTestersOnly = forTestersOnly.isForTestersOnly(testSuite, productionClass, methodsInTheProject) ? 1 : 0;
                    if (isForTestersOnly == 1) {
                        classTestSmellsInfo.setForTestersOnly(1);
                    }

                    isIndirectTesting = indirectTesting.isIndirectTesting(testSuite, productionClass, methodsInTheProject) ? 1 : 0;
                    if (isIndirectTesting == 1) {
                        classTestSmellsInfo.setIndirectTesting(1);
                    }

                    isDuplicateCode = duplicateCode.isCodeDuplication(testSuite, suiteTest.getAbsolutePath()) ? 1 : 0;
                    if (isDuplicateCode == 1) {
                        classTestSmellsInfo.setDuplicateCode(1);
                    }
                }
                LOGGER.info(classTestSmellsInfo.toString());
                classTestSmellsInfos.add(classTestSmellsInfo);

            }
            return classTestSmellsInfos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}