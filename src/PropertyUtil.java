import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class PropertyUtil {

    private static final Properties prop = new Properties();

    static {
        String path = new Main().getClass().getProtectionDomain().getCodeSource().getLocation()
                .toString().substring(6);

        try (InputStream input = new FileInputStream(path + "resource/config.properties")) {

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
