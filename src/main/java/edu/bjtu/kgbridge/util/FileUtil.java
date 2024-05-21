package edu.bjtu.kgbridge.util;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: FileUtil
 * Package: edu.bjtu.kgbridge.util
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/20 17:53
 */
public class FileUtil {

    private Path baseDir;

    /**
     * 构造器
     * @param baseDir 基础文件目录
     */
    public FileUtil(Path baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 设置baseDir
     * @param baseDir 基础文件目录
     */
    public void setBaseDir(Path baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 列举BASE_DIR目录中的所有文件
     *
     * @return 文件名列表
     */
    public Result<List<String>> listFiles(String endsWith) {
        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir)) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, "Directory not found");
        }

        try (Stream<Path> paths = Files.list(baseDir)) {
            List<String> fileNames = paths
                    .filter(path -> path.toString().toLowerCase().endsWith(endsWith))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            return Result.success(fileNames);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not list files");
        }
    }

    /**
     * 读取指定文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    public Result<List<String>> readFile(String fileName) {
        Path filePath = baseDir.resolve(fileName);
        if (!Files.exists(filePath)) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, "File not found: " + fileName);
        }
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return Result.success(lines);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not read file: " + fileName);
        }
    }

    /**
     * 修改文件内容
     *
     * @param lineNumbers 行号列表
     * @param elementIndexes 元素索引列表
     * @param newValues 新值列表
     * @throws IOException 异常
     */
    public void modifyFile(List<Integer> lineNumbers, List<Integer> elementIndexes, List<String> newValues) throws IOException {

        Path filePath = Paths.get(
                System.getProperty("user.dir"),
                "python-service",
                "input_data",
                "ner_label"
        );

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (int i = 0; i < lineNumbers.size(); i++) {
            int lineNumber = lineNumbers.get(i);
            int elementIndex = elementIndexes.get(i);
            String newValue = newValues.get(i);

            if (lineNumber < 1 || lineNumber > lines.size()) {
                throw new IllegalArgumentException("行号超出范围: " + lineNumber);
            }

            String[] elements = lines.get(lineNumber - 1).split("\\s+");
            if (elementIndex < 0 || elementIndex >= elements.length) {
                throw new IllegalArgumentException("元素索引超出范围: " + elementIndex);
            }

            elements[elementIndex] = newValue;
            lines.set(lineNumber - 1, String.join(" ", elements));
        }

        Files.write(filePath, lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * 将四元组字符串写入指定文件
     *
     * @param content 四元组字符串，中间以"#"隔开
     * @param fileName 文件名
     * @return 结果
     */
    public Result<String> writeTuplesToFile(String content, String fileName) {
        Path filePath = baseDir.resolve(fileName);

        String[] tuples = content.split("#");
        List<String> lines = Stream.of(tuples)
                .map(tuple -> tuple.replaceAll("[(),]", ""))
                .collect(Collectors.toList());

        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return Result.success(content);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not write to file: " + fileName);
        }
    }

}