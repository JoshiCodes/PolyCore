package de.joshicodes.polycore.plugins;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass == null) {
            try {
                loadedClass = findClass(name);
            } catch (ClassNotFoundException e) {
                loadedClass = super.loadClass(name, resolve);
            }
        }

        if (resolve) {
            resolveClass(loadedClass);
        }
        return loadedClass;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url = findResource(name);
        if (url != null) {
            try {
                return url.openStream();
            } catch (Exception e) {
                // Fallback to Parent
            }
        }

        return super.getResourceAsStream(name);
    }

}
