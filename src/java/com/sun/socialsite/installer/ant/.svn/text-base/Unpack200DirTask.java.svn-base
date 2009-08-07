/*
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.socialsite.installer.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;


/**
 * An optional ant Task which emulates the Command Line Interface unpack200(1).
 * This is based on the original ant task to unpack, but this version takes
 * a directory as the argument and looks for the files to unpack based on the
 * extension.
 *
 * @author Ana Lindstrom-Tamer
 */
public class Unpack200DirTask extends Task {

    enum FileType { unknown, gzip, pack200, zip };

    private SortedMap <String, String> propMap;
    private Pack200.Unpacker unpkr;

    protected static final String COM_PREFIX="com.sun.java.util.jar.pack.";


    private final String PACKED_EXT = ".pack.gz";

    protected String ext = PACKED_EXT;
    protected File dir;
    protected File outputDir;


    public Unpack200DirTask() {
	unpkr = Pack200.newUnpacker();
	propMap = unpkr.properties();
    }


    /**
     * The directory to look at for jar files to expand; required.
     * @param dir directory which contains jar files to expand
     */
    public void setDir(File dir) {
        this.dir = dir;
    }

    /**
     * The extension of the jar files to expand; optional.
     * @param ext extension which the jar files end with to expand
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * The directory to put the expanded jar files; optional.
     * @param dir directory to put the expanded jar files
     */
    public void setOutputDir(File dir) {
        this.outputDir = dir;
    }



    private void validate() throws BuildException {
        if (dir == null) {
            throw new BuildException("No dir specified", getLocation());
        }

        if (!dir.exists()) {
            throw new BuildException("Dir doesn't exist", getLocation());
        }

        if (ext == null || ext.length() < 1) {
            throw new BuildException("No extension specified", getLocation());
        }

        if (outputDir == null || outputDir.length() < 1) {
            outputDir = dir;
        }

        if (!outputDir.exists()) {
            throw new BuildException("Outputdir doesn't exist", getLocation());
        }


    }


    public void execute() throws BuildException {
        validate();
        extract();
    }


    public void setVerbose(String value) {
	propMap.put(COM_PREFIX + "verbose", value);
    }


    private FileType getMagic(File in) throws IOException {
	DataInputStream is = new DataInputStream(new FileInputStream(in));
	int i = is.readInt();
	is.close();
	if ( (i & 0xffffff00) == 0x1f8b0800) {
	    return FileType.gzip;
	} else if ( i == 0xcafed00d) {
	    return FileType.pack200;
	} else if ( i == 0x504b0304) {
	    return FileType.zip;
	} else {
	    return FileType.unknown; 
	}
    }


    protected void extract() {

        try {

            final String[] fileList = dir.list();

            log("Unpacking files in: " + dir + " to: " + outputDir);

            int numUnpackedFiles = 0;

            for (int i = 0 ; i < fileList.length; i++) {
                String fileName = fileList[i];

                if (fileName.endsWith(ext)) {
                    numUnpackedFiles++;

                    String packedSrc = dir + File.separator + fileName;
                    File packedFile = new File(packedSrc);

                    // create original filename
                    int dotIndex = fileName.length() - ext.length();

                    String jarName = fileName.substring(0, dotIndex);
                    String jarSrc = outputDir + File.separator + jarName;
                    File jarFile = new File(jarSrc);

                    log("Unpacking: " + fileName);
                    log("Unpacked jar located at: " + packedSrc + " to: " + jarSrc, Project.MSG_VERBOSE);

	            FileInputStream fis = new FileInputStream(packedFile);

        	    InputStream is = (FileType.gzip == getMagic(packedFile))
        		? new BufferedInputStream(new GZIPInputStream(fis))
        		: new BufferedInputStream(fis);
        
        	    FileOutputStream fos = new FileOutputStream(jarFile);
        	    JarOutputStream jout = new JarOutputStream(
					new BufferedOutputStream(fos));
        	    
        	    unpkr.unpack(is, jout);
                    is.close();
          	    jout.close();
                }
            }

            if (numUnpackedFiles == 0) {
                log("No files to unpack", Project.MSG_WARN);
            } 

	} catch (IOException ioe) {
	    throw new BuildException("Error in unpack200");	
        }

    }

}
