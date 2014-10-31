<%@ page language="java" contentType="text/html; charset=UTF-8"
import="java.util.*,java.io.*,org.apache.commons.fileupload.*" 
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件上传</title>

        <%
            response.setCharacterEncoding("UTF-8");
            String uploadPath = "E:\\partmanager\\upload\\";
            String tempPath = "E:\\";
            try {
                DiskFileUpload fu = new DiskFileUpload();
                fu.setSizeMax(4194304); // 设置最大文件尺寸，这里是4MB
                fu.setSizeThreshold(4096); // 设置缓冲区大小，这里是4kb
                fu.setRepositoryPath(tempPath); // 设置临时目录
                List fileItems = fu.parseRequest(request); // 得到所有的文件：
                Iterator i = fileItems.iterator();
                // 依次处理每一个文件：
                while (i.hasNext()) {
                    FileItem fi = (FileItem) i.next();
                    String fileName = fi.getName();// 获得文件名，这个文件名包括路径：
                    if (fileName != null) {
                        // 在这里可以记录用户和文件信息
                        // 此处可以定义一个接口（CallBack），用于处理后事。
                        // 写入文件a.txt，你也可以从fileName中提取文件名：
                        String name = fileName.substring(0, fileName
                                .indexOf("."));
                        String extfile = fileName.substring(fileName
                                .indexOf("."));
 
                        //上传时间作为文件名，用以防止重复上传
                        //Timestamp now = new Timestamp((new java.util.Date()).getTime());
                        //SimpleDateFormat   fmt   =   new   SimpleDateFormat("yyyyMMddHHmmssSSS");   
                        //String  pfileName=   fmt.format(now).toString().trim();   
                        System.out.println(name + extfile);
                        fi.write(new File(name + extfile));
                    }
                }
 
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print("{success:true,message:'上传成功'}");
                // 跳转到上传成功提示页面
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().print("{success:false,message:'上传失败'}");
                // 可以跳转出错页面
            }
        %>
</head>
<body></body>
</html>


