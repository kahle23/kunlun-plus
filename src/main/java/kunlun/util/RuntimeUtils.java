/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.util;

import kunlun.io.util.IOUtils;

import java.io.*;

import static kunlun.common.constant.Charsets.STR_DEFAULT_CHARSET;
import static kunlun.common.constant.Symbols.NEWLINE;

@Deprecated
public class RuntimeUtils {
    private static final Runtime runtime = Runtime.getRuntime();

    public static Process exec(String command) throws IOException {
        return runtime.exec(command);
    }

    public static Process exec(String[] cmdarray) throws IOException {
        return runtime.exec(cmdarray);
    }

    public static Process exec(String command, String[] envp) throws IOException {
        return runtime.exec(command, envp);
    }

    public static Process exec(String[] cmdarray, String[] envp) throws IOException {
        return runtime.exec(cmdarray, envp);
    }

    public static Process exec(String command, String[] envp, File dir) throws IOException {
        return runtime.exec(command, envp, dir);
    }

    public static Process exec(String[] cmdarray, String[] envp, File dir) throws IOException {
        return runtime.exec(cmdarray, envp, dir);
    }

    public static String run(Process process)
            throws IOException {
        return run(process, STR_DEFAULT_CHARSET);
    }

    public static String run(Process process, String encoding)
            throws IOException {
        InputStream in = null;
        try {
            in = process.getInputStream();
            return IOUtils.toString(in, encoding);
        }
        finally {
            CloseUtils.closeQuietly(in);
            process.destroy();
        }
    }

    public static String run(Process process, long runtime)
            throws IOException {
        return run(process, runtime, STR_DEFAULT_CHARSET);
    }

    public static String run(Process process, long runtime, String encoding)
            throws IOException {
        BufferedReader reader = null;
        try {
            InputStream i = process.getInputStream();
            InputStreamReader r = new InputStreamReader(i, encoding);
            reader = new BufferedReader(r);

            StringBuilder builder = new StringBuilder();
            long target = System.currentTimeMillis() + runtime;
            long current = System.currentTimeMillis();
            for (String line; (line = reader.readLine()) != null
                    && current < target; ) {
                builder.append(line).append(NEWLINE);
                current = System.currentTimeMillis();
            }
            return builder.toString();
        }
        finally {
            CloseUtils.closeQuietly(reader);
            process.destroy();
        }
    }

    public static String run(String command)
            throws IOException {
        return run(command, STR_DEFAULT_CHARSET);
    }

    public static String run(String command, String encoding)
            throws IOException {
        return run(exec(command), encoding);
    }

    public static String run(String command, long runtime)
            throws IOException {
        return run(command, runtime, STR_DEFAULT_CHARSET);
    }

    public static String run(String command, long runtime, String encoding)
            throws IOException {
        return run(exec(command), runtime, encoding);
    }

}
