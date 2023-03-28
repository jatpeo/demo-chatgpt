package org.jatpeo.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jatpeo.chatgpt.ChatGptUtil;
import org.jatpeo.common.R;
import org.springframework.web.bind.annotation.*;


/**
 * (FrEnterprise)表控制层
 *
 * @author jatpeo
 * @since 2022-11-23 16:22:15
 */
@Slf4j
@RestController
@RequestMapping("/demo")
@CrossOrigin(origins = "*")
public class DemoController {

    /**
     * 询问chatgpt
     *
     * @param ques
     * @return
     */
    @PostMapping("/")
    public R demo(@RequestBody String ques) {
        String result = ChatGptUtil.toAnswer(ques);
        if (StringUtils.isNotEmpty(result)) {
            return R.success(result);
        }
        return R.error();
    }

    /**
     * 询问chatgpt
     *
     * @param ques
     * @return
     */
    public static void main(String[] args) {
        try {

           cal(1, 0);
        } catch (Exception e) {
            StackTraceElement ste = e.getStackTrace()[0];
            log.error("======================================================");
            String errorMsg = "error:异常类：" + ste.getClassName() + "||异常方法：" + ste.getMethodName()
                    + "||异常行号：" + ste.getLineNumber() + "||异常：" + e.getMessage();
            log.error(errorMsg);
        }

    }

    static void cal(int a, int b) {
        double r = 0.0;
        try {
            r=  a / b;
        } catch (Exception e) {
            throw new RuntimeException("失败了");
        }
    }


}

