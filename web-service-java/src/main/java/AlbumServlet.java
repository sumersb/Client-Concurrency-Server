import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;


@MultipartConfig
@WebServlet(name = "AlbumServlet", urlPatterns = "/albums/*")
public class AlbumServlet extends HttpServlet {

    private final AlbumInfo TEST_ALBUM = new AlbumInfo("Shakira", "Waka waka", "2012");


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getRequestURI();

        if (urlPath == null || urlPath.isEmpty() || !isGetRequestPathValid(urlPath)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(JsonUtils.objectToJson(new ErrorMsg("Invalid URL")));
            return;
        }

        if (urlPath.endsWith("/")) {
            urlPath=urlPath.substring(0,urlPath.length()-1);
        }
        String[] urlParts = urlPath.split("/");

        searchAlbum(res,urlParts[urlParts.length-1]);
    }


    public boolean isGetRequestPathValid(String urlPath) {
        String regex = ".*albums/[^/]+/?$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlPath);
        return  matcher.matches();
    }

    public void searchAlbum(HttpServletResponse res, String albumID) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(JsonUtils.objectToJson(TEST_ALBUM));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        String urlPath = req.getRequestURI();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty() || !isPostRequestPathValid(urlPath)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write(JsonUtils.objectToJson(new ErrorMsg("Invalid URL")));
            return;
        }

        postAlbum(req,res);

    }


    public boolean isPostRequestPathValid(String urlPath) {
        String regex = ".*albums/?$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlPath);
        return matcher.matches();
    }



    public void postAlbum(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        // Extracting JSON payload
        String profileJson = req.getParameter("profile");
        try {
            AlbumInfo albumInfo = JsonUtils.jsonToObject(profileJson, AlbumInfo.class);

            // Extracting image
            Part imagePart = req.getPart("image");

            String imageSize = String.valueOf(imagePart.getSize());

            // Responding with metadata
            ImageMetaData metaData = new ImageMetaData("album-key", imageSize);
            res.getWriter().write(Objects.requireNonNull(JsonUtils.objectToJson(metaData)));
            res.setStatus(HttpServletResponse.SC_CREATED);

        } catch (Exception e) {
            res.getWriter().write(JsonUtils.objectToJson(new ErrorMsg("Invalid parameters")));
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
