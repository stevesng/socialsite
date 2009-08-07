/*
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.socialsite.installer.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
 

/**
 * An optional ant Task which emulates the Command Line Interface pack200(1).
 * This is based on the original ant task to pack, but this version takes
 * a directory as the argument and looks for the jar files to pack based on
 * the extension.
 *
 * @author Ana Lindstrom-Tamer
 */
public class Pack200DirTask extends Task {

    private   static final String ERRMSG_ZF="zipfile attribute must end";
    protected static final String COM_PREFIX="com.sun.java.util.jar.pack.";


    private boolean doRepack = false;
    private boolean doGZIP = false;
    private File p200ConfigFile = null;

    protected String ext = ".jar";
    private final String PACKED_EXT = ".pack.gz";

    protected String outputExt = PACKED_EXT;

    protected File dir;
    protected File outputDir = null;
    protected String excludes = null;
    protected Vector<String> excludeFileList = new Vector<String>();


    // Storage for the properties used by the setters.
    private HashMap<String, String> propMap;

    public Pack200DirTask() {
        // Initialize our fields
        doRepack = false;
        doGZIP   = false;
        propMap  = new HashMap<String, String>();
    }

    /**
     * The directory to look at for jar files to pack; required.
     * @param dir directory which contains jar files to pack
     */
    public void setDir(File dir) {
        this.dir = dir;
    }


    /**
     * The extension to give the jar files when they are packed; optional.
     * @param ext extension which the jar files will end with when packed
     */
    public void setOutputExt(String ext) {
        this.outputExt = ext;
    }

    /**
     * The directory to put the packed jar files; optional.
     * @param dir directory to put the expanded jar files
     */
    public void setOutputDir(File dir) {
        this.outputDir = dir;
    }

    /**
     * The list of files to exclude when packing; optional.
     * @param exclude files which should be excluded from packing.  The list is separated by commas.
     */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }




    /**
     * validation routine
     * @throws BuildException if anything is invalid
     */
    private void validate() throws BuildException {

        if (dir == null) {
            throw new BuildException("dir attribute is required", getLocation());
        }

        if (!dir.exists()) {
            throw new BuildException("dir doesn't exist", getLocation());
        }


        if (outputDir == null) {
            outputDir = dir;
        }

        if (!outputDir.exists()) {
            throw new BuildException("Outputdir doesn't exist", getLocation());
        }

        if (outputExt == null) {
            throw new BuildException("Outputext can't be null", getLocation());
        }

        if (excludes != null && excludes.trim().length() > 0) {
            // remove spaces which could be surrounding comma delimeter
            String normalizedExcludes = excludes.replaceAll(" ", "");
            String[] excludeFiles = normalizedExcludes.split(",");

            for (int i = 0; i < excludeFiles.length; i++) {
                String excl = excludeFiles[i];
                excludeFileList.addElement(excl);

                log("Excluding from packing: " + excl);
            }

        }


        if (p200ConfigFile != null &&
                (!p200ConfigFile.exists() || p200ConfigFile.isDirectory())) {
            throw new BuildException("Pack200 property file attribute must "
                                + "exist and not represent a directory!",
                                getLocation());
        }

    }


    /**
     * validation routine of file to compressed
     * @throws BuildException if anything is invalid
     */
    private void validateOutputFile(File zipFile) throws BuildException {
        if (doGZIP) {
            if (!zipFile.toString().toLowerCase().endsWith(".gz")) {
                throw new BuildException(ERRMSG_ZF + " with .gz extension",
                                getLocation());
            }
        } else if (doRepack) {
            if (!zipFile.toString().toLowerCase().endsWith(".jar")) {
                throw new BuildException(ERRMSG_ZF + " with .jar extension",
                                getLocation());
            }
        } else {
            if (!zipFile.toString().toLowerCase().endsWith(".pack") &&
                        !zipFile.toString().toLowerCase().endsWith(".pac")) {

                throw new BuildException(ERRMSG_ZF +
                                        "with .pack or .pac extension",
                                        getLocation());
            }
        }
    }



    /**
     * validate, then hand off to the subclass
     * @throws BuildException
     */
    public void execute() throws BuildException {
        validate();

        final String[] fileList = dir.list();

        int numUnpackedFiles = 0;

        for (int i = 0 ; i < fileList.length; i++) {
            String fileName = fileList[i];

            if (fileName.endsWith(ext) && !(excludeFileList.contains(fileName))) {

                String jarSrc = dir + File.separator + fileName;
                File jarFile = new File(jarSrc);

                String packedSrc = outputDir + File.separator + fileName + outputExt;
                File packedFile = new File(packedSrc);

                if (packedFile.lastModified() < jarFile.lastModified()) {
                    log("Building: " + packedFile.getAbsolutePath());


                    pack(jarFile, packedFile);
                    numUnpackedFiles++;
                } else {
                    log("Nothing to do: " + packedFile.getAbsolutePath()
                         + " is up to date.");
                }
            }
        }

        if (numUnpackedFiles == 0) {
            log("No files to pack", Project.MSG_WARN);
        } 

    }


    /**
     * zip a stream to an output stream
     * @param in
     * @param zOut
     * @throws IOException
     */
    private void zipFile(InputStream in, OutputStream zOut)
        throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int count = 0;
        do {
            zOut.write(buffer, 0, count);
            count = in.read(buffer, 0, buffer.length);
        } while (count != -1);
    }


    /**
     * zip a file to an output stream
     * @param file
     * @param zOut
     * @throws IOException
     */
    protected void zipFile(File file, OutputStream zOut)
        throws IOException {
        FileInputStream fIn = new FileInputStream(file);
        try {
            zipFile(fIn, zOut);
        } finally {
            fIn.close();
        }
    }


    /**
     * Sets the repack option, ie the jar will be packed and repacked.
     */

    public void setRepack(boolean value) {
        doRepack = value;
    }

    /**
     * Sets whether the pack archive is additionally  deflated with gzip.
     */
    public void setGZIPOutput(boolean value) {
        doGZIP = value;
    }

    /**
     * Sets whether the java debug attributes should be stripped
     */
    public void setStripDebug(String value) {
        propMap.put(COM_PREFIX+"strip.debug",value);
    }

    /**
     * Sets the modification time for the archive
     */
    public void setModificationTime(String value) {
        propMap.put(Packer.MODIFICATION_TIME, value);
    }

    /**
     * Sets the deflate hint for the archive
     */
    public void setDeflateHint(String value) {
        propMap.put(Packer.DEFLATE_HINT, value);
    }

    /**
     * Sets the file ordering.
     */
    public void setKeepFileOrder(String value) {
        propMap.put(Packer.KEEP_FILE_ORDER,value);
    }

    /**
     *  Sets the segment limit.
     */
    public void setSegmentLimit(String value) {
        propMap.put(Packer.SEGMENT_LIMIT, value);
    }

    /**
     *  Sets the effort.
     */
    public void setEffort(String value) {
        propMap.put(Packer.EFFORT, value);
    }

    /**
     *  Sets the action to be taken if an unknown attribute is encountered.
     */
    public void setUnknownAttribute(String value) {
        propMap.put(Packer.UNKNOWN_ATTRIBUTE, value);
    }


    /**
     * Useful to set those Pack200 attributes which are not
     * commonly used.
     */
    public void setConfigFile(File packConfig) {
        p200ConfigFile = packConfig;
    }


    /**
     * Set the verbosity level.
     */
    public void setVerbose(String value) {
        propMap.put(COM_PREFIX + "verbose",value);
    }


    protected void pack(File source, File zipFile) {
        String statusStr = doRepack
            ? "Repack with Pack200"
            : "Packing with Pack200";

        log(statusStr);
        log("Source File :" + source);
        log("Dest.  File :" + zipFile);
        if (p200ConfigFile != null) {
           log("Config file :" + p200ConfigFile);
        }


        validateOutputFile(zipFile);

        File packFile = zipFile;

        try {
            Pack200.Packer pkr = Pack200.newPacker();
            pkr.properties().putAll(propMap);
            // The config file overrides all.
            if (p200ConfigFile != null) {
                InputStream is = new BufferedInputStream(
                                new FileInputStream(p200ConfigFile));
                Properties pFile = new Properties();
                pFile.load(is);
                is.close();
                for (Map.Entry me : pFile.entrySet())
                   pkr.properties().put((String) me.getKey(),
                                (String) me.getValue());
            }

            if (doRepack) {
                doGZIP = false;
                packFile = new File(zipFile.toString() + ".tmp");
            }

            JarFile jarFile = new JarFile(source);
            FileOutputStream fos = new FileOutputStream(packFile);

            OutputStream os = (doGZIP)
                ? new BufferedOutputStream(new GZIPOutputStream(fos))
                : new BufferedOutputStream(fos);

            pkr.pack(jarFile, os);
            os.close();
            jarFile.close();

            if (doRepack) {
                InputStream is = new BufferedInputStream(
                                    new FileInputStream(packFile));

                JarOutputStream jout = new JarOutputStream(
                                          new BufferedOutputStream(
                                          new FileOutputStream(zipFile)));

                Pack200.Unpacker unpkr = Pack200.newUnpacker();
                unpkr.properties().putAll(propMap);

                // The config file overrides all.
                if (p200ConfigFile != null) {
                    InputStream pc_is = new BufferedInputStream(
                                        new FileInputStream(p200ConfigFile));
                    Properties pFile = new Properties();
                    pFile.load(pc_is);
                    pc_is.close();
                    for (Map.Entry me : pFile.entrySet()) {
                        unpkr.properties().put((String)me.getKey(), (String)me.getValue());
                    }
                }

                unpkr.unpack(is, jout);
                is.close();
                jout.close();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new BuildException("Error in pack200");
        } finally {
            if (doRepack) {
                packFile.delete();
            }
        }
    }
}

