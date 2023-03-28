package org.jatpeo.chatgpt;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jatpeo.entity.Answer;
import org.jatpeo.entity.Choices;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.ServerException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.jatpeo.demo.MyApp.getSslConnectionSocketFactory;

@Slf4j
public class ChatGptUtil {

    /**
     * 返回答案
     */
    public static String toAnswer(String ques) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setSSLSocketFactory(getSslConnectionSocketFactory())
                    .build();
            Map mapTypes = JSON.parseObject(ques);
            return submit(httpClient, getHttpPost(), mapTypes.get("ques").toString());
        } catch (Exception e) {
            log.error("error:" + e.getMessage());
        }
        return null;
    }

    /**
     * getHttpPost
     *
     * @return
     * @throws IOException
     */
    private static HttpPost getHttpPost() throws IOException {
        Properties prop = new Properties();
        InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/application.properties"));
        prop.load(inputStream);
        String openAiKey = prop.getProperty("SECRET_KEY");
        String connectTimeout = prop.getProperty("connectTimeout");
        String connectionRequestTimeout = prop.getProperty("connectionRequestTimeout");
        String socketTimeout = prop.getProperty("socketTimeout");
        HttpPost post = new HttpPost("https://api.openai.com/v1/completions");
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Bearer " + openAiKey);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Integer.parseInt(connectTimeout)).setConnectionRequestTimeout(Integer.parseInt(connectionRequestTimeout))
                .setSocketTimeout(Integer.parseInt(socketTimeout)).build();
        post.setConfig(requestConfig);
        return post;
    }

    /**
     * 提交消息
     */
    private static String submit(CloseableHttpClient httpClient, HttpPost post, String questionStr) throws IOException {
        log.info("you: " + questionStr);
        StringEntity stringEntity = new StringEntity(getRequestJson(questionStr), getContentType());
        post.setEntity(stringEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(post);
        } catch (SocketTimeoutException e) {
            System.out.println("-- warning: Read timed out!");
        } catch (SocketException e) {
            System.out.println("-- warning: Connection reset!");
        } catch (Exception e) {
            System.out.println("-- warning: Please try again!");
        }
        String aiAnswer = printAnswer(response);
        log.info("AI:" + aiAnswer);
        return aiAnswer;
    }

    /**
     * 返回转换后的消息
     */
    private static String printAnswer(CloseableHttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String responseJson = EntityUtils.toString(response.getEntity());
            Answer answer = JSON.parseObject(responseJson, Answer.class);
            StringBuilder answers = new StringBuilder();
            List<Choices> choices = answer.getChoices();
            for (Choices choice : choices) {
                answers.append(choice.getText());
            }
            System.out.println(answers.substring(2, answers.length()));
            return answers.substring(2, answers.length());
        } else if (response.getStatusLine().getStatusCode() == 429) {
            System.out.println("-- warning: Too Many Requests!");
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            throw new ServerException("------ Server error, program terminated! ------");
        } else {
            System.out.println("-- warning: Error, please try again!");
        }
        return null;
    }


    /**
     * 转化json
     */
    private static String getRequestJson(String question) {
        return "{\"model\": \"text-davinci-003\", \"prompt\": \"" + question + "\", \"temperature\": 0, \"max_tokens\": 1024}";
    }

    /**
     * 请求头
     *
     * @return
     */
    private static ContentType getContentType() {
        return ContentType.create("text/json", "UTF-8");
    }
}
