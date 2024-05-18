package edu.bjtu.kgbridge.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName: PythonCaller
 * Package: edu.bjtu.kgbridge.util
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/18 17:31
 */
public class PythonCaller {

    private static String pythonPath = "python";

    /**
     * 设置Python解释器的路径
     *
     * @param path Python解释器的路径
     */
    public static void setPythonPath(String path) {
        pythonPath = path;
    }

    /**
     * 调用Python脚本
     *
     * @param scriptPath 脚本路径
     * @param args 参数
     * @return 执行结果
     */
    public static String callPythonScript(String scriptPath, String... args) {
        StringBuilder result = new StringBuilder();
        List<String> command = new ArrayList<>();
        command.add(pythonPath);
        command.add(scriptPath);
        Collections.addAll(command, args);

        Process process = null;
        try {
            // 启动进程
            ProcessBuilder pb = new ProcessBuilder(command);
            process = pb.start();

            // 捕获标准输出
            try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String s;
                while ((s = stdInput.readLine()) != null) {
                    result.append(s).append("\n");
                }
                while ((s = stdError.readLine()) != null) {
                    result.append("ERROR: ").append(s).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                result.append("Process exited with code ").append(exitCode);
            }

        } catch (IOException e) {
            result.append("IOException: ").append(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.append("InterruptedException: ").append(e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return result.toString();
    }
}