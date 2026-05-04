package service;

import DAO.PhimDAO;
import DAO.DoAnDAO;
import DAO.SuatChieuDAO;
import entity.DoAn;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * GeminiService – Gọi API Google Gemini để tư vấn phim, đồ ăn và suất chiếu.
 */
public class GeminiService {

    private static final String API_KEY = "AIzaSyANqUZb8ZWQwWL2nNY2AVvuyydcHKgtots";
    private static final String MODEL = "gemini-2.5-flash"; // Dùng bản ổn định hơn
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent?key=" + API_KEY;

    private final HttpClient client;

    public GeminiService() {
        this.client = HttpClient.newHttpClient();
    }

    public String askGemini(String userPrompt) {
        try {
            String context = buildCinemaContext();
            String fullPrompt = "Bạn là trợ lý ảo thông minh của rạp phim MEGADE Cinema. " +
                    "Bạn có quyền truy cập vào dữ liệu thực tế sau:\n\n" +
                    context + "\n\n" +
                    "HƯỚNG DẪN TRẢ LỜI:\n" +
                    "1. Hãy trả lời thân thiện, ngắn gọn và chuyên nghiệp.\n" +
                    "2. Nếu khách hỏi về đồ ăn/uống, hãy tư vấn giá cả và các combo hấp dẫn.\n" +
                    "3. Nếu khách hỏi về lịch chiếu hoặc còn bao nhiêu chỗ, hãy dựa vào dữ liệu Suất Chiếu bên trên.\n" +
                    "4. Luôn khuyến khích khách hàng đặt vé trực tuyến.\n" +
                    "5. Nếu không có thông tin cụ thể, hãy trả lời dựa trên những gì bạn biết và xin lỗi khéo léo.\n\n" +
                    "Khách hàng hỏi: " + userPrompt;

            String jsonBody = "{" +
                    "\"contents\": [{" +
                    "\"parts\": [{\"text\": \"" + escapeJson(fullPrompt) + "\"}]" +
                    "}]" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return parseGeminiResponse(response.body());
            } else {
                return "Xin lỗi, tôi đang bận một chút (Lỗi " + response.statusCode() + "). Bạn thử lại sau nhé!";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Có lỗi xảy ra khi gọi trợ lý ảo: " + e.getMessage();
        }
    }

    private String parseGeminiResponse(String body) {
        try {
            int start = body.indexOf("\"text\": \"") + 9;
            int end = body.indexOf("\"", start);
            if (start > 8 && end > start) {
                String result = body.substring(start, end);
                return result.replace("\\n", "\n").replace("\\\"", "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Xin lỗi, tôi gặp trục trặc khi xử lý câu trả lời.";
    }

    /**
     * Tổng hợp dữ liệu từ Database thành văn bản ngữ cảnh cho AI.
     */
    private String buildCinemaContext() {
        StringBuilder sb = new StringBuilder();
        
        // 1. Phim đang chiếu
        PhimDAO phimDAO = new PhimDAO();
        List<Object[]> dsPhim = phimDAO.getPhimDangChieu();
        sb.append("--- DANH SÁCH PHIM ĐANG CHIẾU ---\n");
        for (Object[] p : dsPhim) {
            sb.append("- ").append(p[1]).append(" (").append(p[2]).append(")\n");
        }

        // 2. Suất chiếu hôm nay
        SuatChieuDAO scDAO = new SuatChieuDAO();
        List<Object[]> dsSC = scDAO.getLichChieuHomNay();
        sb.append("\n--- LỊCH CHIẾU & SỐ GHẾ TRỐNG HÔM NAY ---\n");
        if (dsSC.isEmpty()) {
            sb.append("(Chưa có suất chiếu nào được xếp hôm nay)\n");
        } else {
            for (Object[] sc : dsSC) {
                // sc = [maSC, tenPhim, tenPhong, gioChieu, trong, tong, giaVe]
                sb.append("- ").append(sc[1]).append(" | Giờ: ").append(sc[3])
                  .append(" | Phòng: ").append(sc[2])
                  .append(" | Còn trống: ").append(sc[4]).append("/").append(sc[5])
                  .append(" ghế | Giá vé: ").append(sc[6]).append("đ\n");
            }
        }

        // 3. Thực đơn đồ ăn
        DoAnDAO doAnDAO = new DoAnDAO();
        List<DoAn> dsDoAn = doAnDAO.getAllDoAn();
        sb.append("\n--- THỰC ĐƠN ĐỒ ĂN & NƯỚC UỐNG ---\n");
        for (DoAn da : dsDoAn) {
            sb.append("- ").append(da.getTen()).append(": ").append(da.getGia()).append("đ (").append(da.getMoTa()).append(")\n");
        }

        return sb.toString();
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
