import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * run as java Importer
 **/
public class Importer {

    private int importedCount = 0;

    private int missedCount = 0;

    public Importer() {
        super();
    }

    public void go() {
        try {
            File dir = new File(PropertyUtil.getProperty("maven.repository"));

            doDir(dir);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void doDir(File dir) throws IOException, InterruptedException {
        File[] listFiles = dir.listFiles(new PomFilter());
        if (listFiles != null) {
            for (File pom : listFiles) {
                doPom(pom);
            }
        }

        File[] listDirs = dir.listFiles(new DirFilter());
        if (listDirs != null) {
            for (File subdir : listDirs) {
                doDir(subdir);
            }
        }
    }

    private void doPom(File pom) throws IOException, InterruptedException {
        File base = pom.getParentFile();
        String fileName = pom.getName();
        String jarName = fileName.substring(0, fileName.length() - 3) + "jar";
        File jar = new File(base.getAbsolutePath() + "/" + jarName);
        String [] pomTagValues = DomParser.getGroupAndArtifactId(pom);
        String alternativeDeploy = PropertyUtil.getProperty("maven.alternative.deploy.plugin");

        String exec = PropertyUtil.getProperty("maven.runnable") + " " +
                (alternativeDeploy != null ? alternativeDeploy : "deploy") + ":deploy-file " +
                "-Durl=" + PropertyUtil.getProperty("nexus.url") + " " +
                "-DrepositoryId=" + PropertyUtil.getProperty("nexus.repoId") + " " +
                "-DgroupId=" + pomTagValues[0] + " " +
                "-DartifactId=" + pomTagValues[1] + " " +
                "-Dversion=" + pomTagValues[2] + " " +
                "-Dpackaging=" + PropertyUtil.getProperty("deploy.packaging") + " " +
                "-DgeneratePom=" + PropertyUtil.getProperty("deploy.generatePom") + " " +
                "-DrepositoryUrl=" + PropertyUtil.getProperty("nexus.url") + " " +
                "-Duser=" + PropertyUtil.getProperty("nexus.user") + " " +
                "-Dpassword=" + PropertyUtil.getProperty("nexus.password") + " ";
        if (jar.exists()) {
            exec += " -Dfile=\"" + jar.getAbsolutePath() + "\"";
        }
        else {
            exec += " -Dfile=\"" + pom.getAbsolutePath() + "\"";
        }
        exec += " -DpomFile=\"" + pom.getAbsolutePath() + "\"";
        exec(exec);

    }

    private void exec(String exec) throws InterruptedException, IOException {
        System.out.println(exec);
        Process p = Runtime.getRuntime().exec(exec);
        String line;
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            System.out.println(line);
            if (line.equals(PropertyUtil.getProperty("maven.fail.message"))) {
                missedCount++;
            } else if (line.equals(PropertyUtil.getProperty("maven.success.message"))) {
                importedCount++;
            }
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
        p.waitFor();
        System.out.println("Done.");
    }

    private class PomFilter implements java.io.FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".pom");
        }
    }

    private class DirFilter implements java.io.FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }

    public int getImportedCount() {
        return importedCount;
    }

    public int getMissedCount() {
        return missedCount;
    }
}