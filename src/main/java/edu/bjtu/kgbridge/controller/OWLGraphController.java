package edu.bjtu.kgbridge.controller;

import edu.bjtu.kgbridge.enums.ResultCodeEnum;
import edu.bjtu.kgbridge.model.Result;
import edu.bjtu.kgbridge.service.OWLGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName: OWLGraphController
 * Package: edu.bjtu.kgbridge.controller
 * Description:
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 17:11
 */
@RestController
@RequestMapping("/owl")
@Tag(name = "OWLGraphController", description = "OWL图谱生成接口")
public class OWLGraphController {

    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir"), "python-service");
    private static final Path OWL_DIR = BASE_DIR.resolve("ontology_files");
    private final OWLGraphService owlGraphService;

    public OWLGraphController(OWLGraphService owlGraphService) {
        this.owlGraphService = owlGraphService;
    }

    @Operation(summary = "生成并返回OWL图谱", description = "根据上传的OWL文件名生成并返回OWL图谱")
    @PostMapping("/generateGraph")
    public ResponseEntity<byte[]> generateGraph(
            @Parameter(description = "IFC模型文件名称", required = true) @RequestParam("fileName") String fileName
    ) {
        Path filePath = OWL_DIR.resolve(fileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(404).body(null);
        }
        try {
            byte[] graph = owlGraphService.generateGraph(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).body(graph);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 列出服务器上已经生成的owl图谱文件
     *
     * @return 文件名列表
     */
    @Operation(summary = "列出owl图谱文件", description = "列出服务器上已经生成的owl图谱文件")
    @GetMapping("/list")
    public Result<List<String>> listFiles() {
        if (!Files.exists(OWL_DIR) || !Files.isDirectory(OWL_DIR)) {
            return Result.fail(ResultCodeEnum.NOT_FOUND, "Directory not found");
        }

        try (Stream<Path> paths = Files.list(OWL_DIR)) {
            List<String> fileNames = paths
                    .filter(path -> path.toString().toLowerCase().endsWith(".owl"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            return Result.success(fileNames);
        } catch (IOException e) {
            return Result.fail(ResultCodeEnum.SERVER_ERROR, "Could not list files");
        }
    }
}