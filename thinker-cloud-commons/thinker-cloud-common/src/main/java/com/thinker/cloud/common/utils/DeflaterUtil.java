package com.thinker.cloud.common.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * DeflaterUtils 压缩字符串
 *
 * @author admin
 */
@Slf4j
@UtilityClass
public class DeflaterUtil {

    /**
     * <a href="https://www.yiibai.com/javazip/javazip_deflater.html#article-start">...</a>
     * 0 ~ 9 压缩等级 低到高
     * public static final int BEST_COMPRESSION = 9;            最佳压缩的压缩级别。
     * public static final int BEST_SPEED = 1;                  压缩级别最快的压缩。
     * public static final int DEFAULT_COMPRESSION = -1;        默认压缩级别。
     * public static final int DEFAULT_STRATEGY = 0;            默认压缩策略。
     * public static final int DEFLATED = 8;                    压缩算法的压缩方法(目前唯一支持的压缩方法)。
     * public static final int FILTERED = 1;                    压缩策略最适用于大部分数值较小且数据分布随机分布的数据。
     * public static final int FULL_FLUSH = 3;                  压缩刷新模式，用于清除所有待处理的输出并重置拆卸器。
     * public static final int HUFFMAN_ONLY = 2;                仅用于霍夫曼编码的压缩策略。
     * public static final int NO_COMPRESSION = 0;              不压缩的压缩级别。
     * public static final int NO_FLUSH = 0;                    用于实现最佳压缩结果的压缩刷新模式。
     * public static final int SYNC_FLUSH = 2;                  用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率。
     */
    public static String zipString(String unzipString) {
        if (Objects.isNull(unzipString)) {
            return null;
        }

        if (unzipString.trim().isEmpty()) {
            return unzipString;
        }

        // 使用指定的压缩级别创建一个新的压缩器。
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        // 设置压缩输入数据。
        deflater.setInput(unzipString.getBytes(StandardCharsets.UTF_8));
        // 当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
        deflater.finish();

        final byte[] bytes = new byte[512];
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(512)) {
            while (!deflater.finished()) {
                // 压缩输入数据并用压缩数据填充指定的缓冲区。
                int length = deflater.deflate(bytes);
                outputStream.write(bytes, 0, length);
            }
            // 关闭压缩器并丢弃任何未处理的输入。
            deflater.end();
            return Base64.encodeBase64String(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("压缩字符串失败", e);
            return null;
        }
    }

    /**
     * 解压缩
     */
    public static String unzipString(String zipString) {
        if (Objects.isNull(zipString)) {
            return null;
        }

        if (zipString.trim().isEmpty()) {
            return zipString;
        }

        byte[] decode = Base64.decodeBase64(zipString);
        // 使用指定的压缩级别创建一个新的压缩器。
        Inflater inflater = new Inflater();
        // 设置解压缩的输入数据。
        inflater.setInput(decode);
        final byte[] bytes = new byte[512];
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(512)) {
            // finished() 如果已到达压缩数据流的末尾，则返回true。
            while (!inflater.finished()) {
                // 将字节解压缩到指定的缓冲区中。
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
            return outputStream.toString("UTF-8");
        } catch (IOException | DataFormatException e) {
            log.error("解压缩字符串失败", e);
            return null;
        } finally {
            // 关闭解压缩器并丢弃任何未处理的输入。
            inflater.end();
        }
    }
}
