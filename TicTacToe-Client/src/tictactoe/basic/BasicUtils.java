package tictactoe.basic;

import java.net.URL;

public class BasicUtils {

    public static String getResourceUrl(Class loaderClass, String resourceName) {
        URL resourceUrl = loaderClass.getClassLoader().getResource("assets/"+resourceName);
        if (resourceUrl == null) {
            throw new NullPointerException("Can't find resource " + resourceName + " in package " + loaderClass.getPackage().toString());
        }
        return resourceUrl.toExternalForm();
    }
}
