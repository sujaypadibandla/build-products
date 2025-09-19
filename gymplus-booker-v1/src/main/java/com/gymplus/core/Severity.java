package com.gymplus.core;

public class Severity {

    // NOTE: 
    // It was tempting to use java.util.logging.Level instead, but Level values are not integers, 
    // and even if we use Level.intValue() each time (which is slower and ugly), the values are 100, 200, etc. 
    // so they cannot be used as indexers in some metadata arrays (e.g. when defining 'E|W|I|V' icons or max line lengths)
    
    /** Internal. A message containing raw data for verbose reporting. */
    public static final int VERBOSE = 0;

    /** An informative message. */
    public static final int INFO = 1;

    /** A warning message. */
    public static final int WARNING = 2;

    /** An error message. */
    public static final int ERROR = 3;
    
    /** Severity is OFF, i.e. it cannot be used (e.g. to specify severities which turn OFF certain logging mechanisms. */
    public static final int OFF = 1000;
}
