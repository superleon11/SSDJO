package Application.Servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import Application.MustacheRenderer;

public class BaseServlet extends HttpServlet {
    public static final  String PLAIN_TEXT_UTF_8 = "text/plain; charset=UTF-8";
    public static final  String HTML_UTF_8 = "text/html; charset=UTF-8";
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");


    private final MustacheRenderer mustache;

    protected BaseServlet() {
        mustache = new MustacheRenderer();
    }

    protected void issue(String mimeType, int returnCode, byte[] output, HttpServletResponse response) throws IOException {
        response.setContentType(mimeType);
        response.setStatus(returnCode);
        response.getOutputStream().write(output);
    }

    protected void issue(String mimeType, int returnCode, String output, HttpServletResponse response) throws IOException {
        issue(mimeType, returnCode, output.getBytes(Charset.defaultCharset()), response);
    }

    protected String user(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String)session.getAttribute("user");
    }

    protected String redirect(HttpServletRequest request) {
        final String deflt = "/index.html";
        HttpSession session = request.getSession(false);
        if (session == null) {
            return deflt;
        }
        String redirect = (String)session.getAttribute("redirect");
        return (redirect == null) ? deflt : redirect;
    }

    protected Map<String,Object> baseMap(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<>();

        String user = user(request);
        String showName = (user == null) ? "<not logged in>" : user;
        if (user!= null) map.put("user", user);
        map.put("userName", showName);
        map.put("currentURI", request.getRequestURI());
        return map;
    }

    protected void showView(HttpServletResponse response, String templateName, Object model) throws IOException {
        String html = mustache.render(templateName, model);
        issue(HTML_UTF_8, HttpServletResponse.SC_OK, html.getBytes(CHARSET_UTF8), response);
    }

}
