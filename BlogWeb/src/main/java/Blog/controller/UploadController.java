package Blog.controller;

import com.google.common.io.ByteStreams;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UploadController {

    @ResponseBody
    @RequestMapping("/fileUpload")
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FileUploadException {

        Map<String, Object> map = new HashMap<>();
        //获取page上下文
        ServletContext servletContext = request.getSession().getServletContext();
        //获取的的tamcat的路径，部署项目后相当于项目的路径(文件保存的路径)
        String sPath = servletContext.getRealPath("/") + "images/";
        //上传文件的url
        String sUrl = request.getContextPath() + "/images/";
        //定义允许上传的文件扩展名
        HashMap<String, String> extNameMap = new HashMap<>();
        extNameMap.put("image", "gif,jpg,jpeg,png,bmp");
        extNameMap.put("flash", "swf,flv");
        extNameMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extNameMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

        //定义最大文件的大小
        long maxSize = 1000000;
        response.setContentType("text/html; charset=UTF-8");
        //ServletFileUpload.isMultipartContent(request)判断请求是否为提交文件的表单
        if (!ServletFileUpload.isMultipartContent(request)) {
            return getError("请选择上传文件");
        }

        //检查上传文件的目录
        File dir = new File(sPath);
        if (!dir.isDirectory()) {
            return getError("上传目录不存在");
        }
        if (!dir.canWrite()) {
            return getError("上传目录无权限");
        }
        String dirName = request.getParameter("dir");
        if (dirName == null) {
            dirName = "image";
        }
        if (!extNameMap.containsKey(dirName)) {
            return getError("上传的目录名不正确");
        }

        //创建文件夹
        sPath = sPath + dirName + "/";
        sUrl = sUrl + dirName + "/";
        File saveFile = new File(sPath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }

        //格式化时间为年月日的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String ymd = sdf.format(new Date());
        sPath = sPath + ymd + "/";
        sUrl = sUrl + ymd + "/";
        File dirFileName = new File(sPath);
        if (!dirFileName.exists()) {
            dirFileName.mkdirs();
        }

        //DiskFileItemFactory为FileItem对象的工厂
        FileItemFactory fileItemFactory = new DiskFileItemFactory();
        //ServletFileUpload为处理表单数据，将数据封装在FileItem中
        ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
        upload.setHeaderEncoding("UTF-8");

        //转化request，解析出request中的文件
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        Iterator iterator = multipartHttpServletRequest.getFileNames();
        while (iterator.hasNext()) {
            String fileName = (String) iterator.next();
            MultipartFile file = multipartHttpServletRequest.getFile(fileName);

            if (file.getSize() > maxSize) {
                return getError("上传文件大小超过限制");
            }
            String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

            if (Arrays.asList(extNameMap.get(dirFileName).split(",")).contains(fileExt)) {
                return getError("上传文件的扩展名非法");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String newFileName = simpleDateFormat.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;

            try {
                File uploadFile = new File(sPath, newFileName);
                ByteStreams.copy(file.getInputStream(), new FileOutputStream(uploadFile));
            } catch (Exception e) {
                return getError("上传文件失败");
            }

            map.put("error", 0);
            map.put("url", sUrl + newFileName);

        }
        return map;
    }

    private Map<String, Object> getError(String message) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("error", 1);
        maps.put("message", message);
        return maps;
    }
}
