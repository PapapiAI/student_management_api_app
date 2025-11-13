package student.management.api_app.repository.specification;

import java.util.Locale;

public class SpecUtils {
    private SpecUtils() {}

    public static String likePattern(String input) {
        // Sử dụng Locale.ROOT theo quy tắc ASCII/Unicode chuẩn, không bị ảnh hưởng bởi ngôn ngữ hệ thống
        return "%" + input.toLowerCase(Locale.ROOT).trim() + "%";
    }
}
