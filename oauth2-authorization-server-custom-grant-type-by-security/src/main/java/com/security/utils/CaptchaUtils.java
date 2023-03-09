package com.security.utils;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

public class CaptchaUtils {

    /**
     * @Feild random: 随机数
     */
    private static Random random = new Random();

    /**
     * @Feild arithmeticOnly 是否为算术验证码
     */
    private boolean arithmeticOnly = false;

    /**
     * @Feild width: 验证码的宽度
     */
    private int width = 200;

    /**
     * @Feild height: 验证码高度
     */
    private int height = 60;

    /**
     * @Feild lineSize: 验证码中夹杂的干扰线数量
     */
    private int lineSize = 30;

    /**
     * @Feild randomStrNum: 验证码字符个数
     */
    private int randomStrNum = 4;

    /**
     * @Feild randomString: 验证码可选字符
     */
    private String randomString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWSYZ";

    /**
     * @Feild numberString: 验证码数字可选字符
     */
    private String numberString = "1234567890";

    /**
     * @Feild operatorString: 算术运算符可选字符
     */
    private String operatorString = "+-*/";


    /**
     * @Feild computedParams: 运算参数
     */
    private List<String> computedParams = new ArrayList<>();

    /**
     * @Feild computedResult: 运算结果
     */
    private String computedResult;

    public static CaptchaUtils builder() {
        CaptchaUtils captchaUtils = new CaptchaUtils();

        return captchaUtils;
    }

    /**
     * 算术验证码构造器
     * 每次调用初始化相关参数
     * @param randomStrNum
     * @return
     */
    public CaptchaUtils arithmetic(int randomStrNum) {
        this.arithmeticOnly = true;
        this.randomStrNum = randomStrNum;
        this.computedParams = new ArrayList<>();
        this.computedResult = BigDecimal.valueOf(0).toPlainString();

        return this;
    }


    //字体的设置
    private Font getFont() {
        return new Font("Times New Roman", Font.ROMAN_BASELINE, 40);
    }

    //颜色的设置
    private Color getRandomColor(int fc, int bc) {

        fc = Math.min(fc, 255);
        bc = Math.min(bc, 255);

        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 12);

        return new Color(r, g, b);
    }

    // 干扰线的绘制
    private void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(20);
        int yl = random.nextInt(10);
        g.drawLine(x, y, x + xl, y + yl);

    }

    // 随机字符的获取
    private String getRandomString(int num){
        num = num > 0 ? num : randomString.length();
        return String.valueOf(randomString.charAt(random.nextInt(num)));
    }

    // 随机运算符获取
    private String getOperatorString(int num){
        num = num > 0 ? num : operatorString.length();
        return String.valueOf(operatorString.charAt(random.nextInt(num)));
    }

    // 随机数字的获取
    private String getNumberString(int num, int digits) {
        digits = random.nextInt(digits);
        num = num > 0 ? num : numberString.length();
        String numString = "";

        for (int i = 0; i < digits; i++) {
            String rand = String.valueOf(numberString.charAt(random.nextInt(num)));
            if (rand.equals("0") && i == 0) {
                rand = String.valueOf((char) (numberString.charAt(random.nextInt(num)) + 1));
            }

            numString += rand;
        }

        numString = numString.equals("") ? "0" : numString;


        return numString;
    }

    // 字符串的绘制
    private String drawString(Graphics g, String randomStr, int i) {
        g.setFont(getFont());
        g.setColor(getRandomColor(108, 190));
        //System.out.println(random.nextInt(randomString.length()));
        String rand = "";

        // 当属于算术验证码
        if (arithmeticOnly) {
            rand = getNumberString(random.nextInt(numberString.length()), 3);
            // 第二个验证码符号为运算符
            if (i == 1) {
                rand = getOperatorString(random.nextInt(operatorString.length()));
            }

            if (i == 2 && computedParams.get(i-1).equals("/") && rand.equals("0")) {
                while (rand.equals("0")) {
                    rand = getNumberString(random.nextInt(numberString.length()), 3);
                }
            }

            computedParams.add(rand);

            // 添加=和？
            if (i == randomStrNum - 1) {
                rand += " = ?";
            }
        } else {
            rand = getRandomString(random.nextInt(randomString.length()));
        }

        randomStr += rand;

        g.translate(random.nextInt(3), random.nextInt(6));
        g.drawString(rand, 40 * i + 10, 38);

        return randomStr;
    }

    /**
     * 生成图片
     * @return
     */
    private Map<String, Object> generateImage() {
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, width, height);
        g.setColor(getRandomColor(105, 189));
        g.setFont(getFont());
        // 干扰线
        for (int i = 0; i < lineSize; i++) {
            drawLine(g);
        }

        // 随机字符
        String randomStr = "";
        for (int i = 0; i < randomStrNum; i++) {
            randomStr = drawString(g, randomStr, i);
        }


        // 计算运算结果
        if (arithmeticOnly) {
            int param1 = Integer.parseInt(computedParams.get(0));
            String operate = computedParams.get(1);
            int param2 = Integer.parseInt(computedParams.get(2));

            switch (operate) {
                case "+":
                    // stripTrailingZeros() 去掉末尾的0
                    computedResult = new BigDecimal(Math.round(param1 + param2)).stripTrailingZeros().toPlainString();
                    break;
                case "-":
                    computedResult = new BigDecimal(Math.round(param1 - param2)).stripTrailingZeros().toPlainString();
                    break;
                case "*":
                    computedResult = new BigDecimal(Math.round(param1 * param2)).stripTrailingZeros().toPlainString();
                    break;
                case "/":
                    computedResult = new BigDecimal((double) param1 / param2).setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                    break;
            }
        }

        System.out.println("随机字符："+randomStr);
        System.out.println("computedResult: " + computedResult);

        g.dispose();

        Map<String, Object> result = new HashMap<>();
        result.put("image", image);

//        if (arithmeticOnly) {
//            result.put("randomStr", computedResult);
//        } else {
//            result.put("randomStr", randomStr);
//        }

        result.put("randomStr", arithmeticOnly ? computedResult : randomStr);

        return result;
    }

    // 生成随机图片
    public String getRandomCodeImage(HttpServletResponse response) throws IOException {
        Map<String, Object> imageInfo = generateImage();

        BufferedImage image = (BufferedImage) imageInfo.get("image");
        String randomStr = imageInfo.get("randomStr").toString();
        //  将图片以png格式返回,返回的是图片
        ImageIO.write(image, "PNG", response.getOutputStream());

        return randomStr;
    }

    //生成随机图片的base64编码字符串
    public String getRandomCodeBase64(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> imageInfo = generateImage();

        BufferedImage image = (BufferedImage) imageInfo.get("image");
        String randomStr = (String) imageInfo.get("randomStr");

        String base64String = "";

        //返回 base64
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", bos);

        byte[] bytes = bos.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        base64String = encoder.encodeToString(bytes);

        return base64String;
    }
}
