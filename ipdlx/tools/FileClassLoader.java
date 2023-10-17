package ipdlx.tools;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Loads class bytes from a file (probably inherited from elsewhere). */
public class FileClassLoader extends MultiClassLoader {

//---------- Private Fields ------------------------------
private String    filePrefix;

//---------- Initialization ------------------------------
/**
 * Attempts to
 * load from a local file using the relative "filePrefix",
 * ie starting at the current directory. For example
 * @param filePrefix could be "webSiteClasses\\site1\\".
 */
public FileClassLoader(String filePrefix) {
    this.filePrefix = filePrefix;
}
//---------- Abstract Implementation ---------------------
protected byte[] loadClassBytes(String className) {

    className = formatClassName(className);
    if (sourceMonitorOn) {
        print(">> from file: " + className);
    }
    byte result[];
    String fileName = filePrefix + className;
    try {
	System.out.println("FileClassLoad: "+fileName);

        FileInputStream inStream = new FileInputStream(fileName);
        // *** Is available() reliable for large files?
        result = new byte[inStream.available()];
        inStream.read(result);
        inStream.close();
        return result;

    } catch (Exception e) {
        // If we caught an exception, either the class
        // wasn't found or it was unreadable by our process.
        print("### File '" + fileName + "' not found.");
        return null;
    }
}

} // End class
