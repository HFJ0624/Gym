package com.sau.gym.admin.rag.embedding;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 21:07
 */
public class VolcanoMultimodalEmbeddingModel implements EmbeddingModel {

    /**
     * 火山方舟 base-url。
     *
     * 示例：
     * https://ark.cn-beijing.volces.com/api/v3
     */
    private final String baseUrl;

    /**
     * 火山方舟 API Key。
     */
    private final String apiKey;

    /**
     * 模型名或接入点 ID。
     *
     * 示例：
     * ep-20260430204219-zql5b
     *
     * 也可能是：
     * doubao-embedding-vision-250615
     *
     * 推荐优先用控制台创建出来的 ep-xxx。
     */
    private final String modelName;

    /**
     * 向量维度。
     *
     * 你的截图里这个模型支持 2048、1024、512、256。
     * 你现在 pgvector dimension 配多少，这里就配多少。
     */
    private final Integer dimension;

    /**
     * HTTP 客户端。
     */
    private final HttpClient httpClient;

    /**
     * JSON 工具。
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VolcanoMultimodalEmbeddingModel(String baseUrl,
                                           String apiKey,
                                           String modelName,
                                           Integer dimension) {
        this.baseUrl = removeTrailingSlash(baseUrl);
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.dimension = dimension;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    /**
     * LangChain4j 的核心方法。
     *
     * RagQaServiceImpl 里调用：
     * ragEmbeddingModel.embed(question)
     *
     * 最终会走到这里的 embedAll。
     */
    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        if (textSegments == null || textSegments.isEmpty()) {
            return Response.from(new ArrayList<>());
        }

        List<Embedding> embeddings = new ArrayList<>();

        for (TextSegment textSegment : textSegments) {
            String text = textSegment.text();

            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("向量化文本不能为空");
            }

            // 调用火山方舟多模态向量化接口，拿到 float 向量
            float[] vector = embedText(text.trim());

            // 包装成 LangChain4j 的 Embedding 对象
            embeddings.add(Embedding.from(vector));
        }

        return Response.from(embeddings);
    }

    /**
     * 返回当前 embedding 模型输出的向量维度。
     *
     * PgVectorEmbeddingStore 创建表时需要这个维度。
     */
    @Override
    public int dimension() {
        return dimension;
    }

    /**
     * 返回模型名。
     */
    @Override
    public String modelName() {
        return modelName;
    }

    /**
     * 调用火山方舟 /embeddings/multimodal 接口。
     *
     * 请求示例：
     *
     * POST https://ark.cn-beijing.volces.com/api/v3/embeddings/multimodal
     *
     * {
     *   "model": "ep-xxx",
     *   "input": [
     *     {
     *       "type": "text",
     *       "text": "羽毛球馆有什么设施？"
     *     }
     *   ]
     * }
     */
    private float[] embedText(String text) {
        try {
            String url = baseUrl + "/embeddings/multimodal";

            // 构造 input[0]
            Map<String, Object> inputItem = new HashMap<>();
            inputItem.put("type", "text");
            inputItem.put("text", text);

            // 构造请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("input", List.of(inputItem));

            /**
             * 如果你在火山方舟控制台选择了 2048 维，下面这个字段通常可以不传。
             * 如果你后续要显式降维，可以根据火山实际 API 参数再补。
             *
             * 当前先不传 dimension，避免参数名不兼容导致 400。
             */
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("火山方舟多模态向量化接口调用失败，HTTP状态码："
                        + response.statusCode()
                        + "，响应："
                        + response.body());
            }

            return parseEmbedding(response.body());

        } catch (IOException e) {
            throw new RuntimeException("调用火山方舟多模态向量化接口失败：IO异常", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("调用火山方舟多模态向量化接口失败：线程被中断", e);
        }
    }

    /**
     * 解析火山方舟 embedding 响应。
     *
     * 火山方舟多模态 embedding 返回格式可能是：
     *
     * 格式一：
     * {
     *   "data": {
     *     "embedding": [...]
     *   }
     * }
     *
     * 也可能是 OpenAI 风格：
     * {
     *   "data": [
     *     {
     *       "embedding": [...]
     *     }
     *   ]
     * }
     *
     * 所以这里同时兼容两种格式。
     */
    private float[] parseEmbedding(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode dataNode = root.get("data");

        if (dataNode == null) {
            throw new RuntimeException("火山方舟向量化响应缺少 data 字段：" + responseBody);
        }

        JsonNode embeddingNode;

        // 情况一：data 是数组，例如 data[0].embedding
        if (dataNode.isArray()) {
            if (dataNode.isEmpty()) {
                throw new RuntimeException("火山方舟向量化响应 data 数组为空：" + responseBody);
            }

            embeddingNode = dataNode.get(0).get("embedding");
        }
        // 情况二：data 是对象，例如 data.embedding
        else if (dataNode.isObject()) {
            embeddingNode = dataNode.get("embedding");
        }
        // 其他格式直接报错
        else {
            throw new RuntimeException("火山方舟向量化响应 data 格式不支持：" + responseBody);
        }

        if (embeddingNode == null || !embeddingNode.isArray()) {
            throw new RuntimeException("火山方舟向量化响应缺少 embedding 数组：" + responseBody);
        }

        float[] vector = new float[embeddingNode.size()];

        for (int i = 0; i < embeddingNode.size(); i++) {
            vector[i] = (float) embeddingNode.get(i).asDouble();
        }

        // 校验实际返回维度和配置维度是否一致
        if (vector.length != dimension) {
            throw new RuntimeException(
                    "向量维度不匹配，配置维度为 "
                            + dimension
                            + "，接口实际返回维度为 "
                            + vector.length
                            + "。请修改 gym.rag.pgvector.dimension，并删除旧 pgvector 表后重建。"
            );
        }

        return vector;
    }

    /**
     * 去掉 baseUrl 末尾的斜杠，避免拼接 URL 时出现双斜杠。
     */
    private String removeTrailingSlash(String value) {
        if (value == null) {
            return "";
        }

        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}
