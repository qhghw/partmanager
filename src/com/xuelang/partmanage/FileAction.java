package com.xuelang.partmanage;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbuilder.utils.CompressUtil;
import com.webbuilder.utils.FileUtil;
import com.webbuilder.utils.StringUtil;

public class FileAction {
	public void getAndSaveFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			String root = request.getAttribute("sys.path").toString()
					+ "WEB-INF/myfile/";
			Date time = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			root += sdf.format(time);// tm就是昨天的日期的字符串表示
			File dir = new File(root);
			if (!dir.exists()) {
				if (!dir.mkdirs())
					throw new Exception("不能创建目录。");
			}
			String importDir = root;
			System.out.println("root=" + root);
			InputStream stream = (InputStream) request
					.getAttribute("importFile");
			String fn = request.getAttribute("importFile__file").toString();
			System.out.println("fn=" + fn);
			root += "/" + fn;
			String fn1 = fn.replace(".", "!");
			String kzm[] = fn1.split("!");

			if (StringUtil.isEqual(request.getAttribute("importType")
					.toString(), "1")) {
				if (StringUtil.isSame(FileUtil.extractFileExt(fn), "zip"))
					CompressUtil.unzip(stream, new File(importDir),
							(String) request.getAttribute("sys.fileCharset"));
				else
					throw new Exception("请选择一个zip格式的压缩文件。");
			} else {
				FileUtil.saveInputStreamToFile(stream, new File(importDir, fn));
			}
			response.getWriter().write(root);
			// request.setAttribute("root", root);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("end...................");
		}
	}

	public void delFile(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String f = StringUtil.fetchString(request, "f");
		try {
			File file = new File(f);
			if (file.exists() && file.isFile()) {
				file.delete();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
