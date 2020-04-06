package cn.eli486.controller;

import cn.eli486.config.GlobalInfo;
import cn.eli486.dto.PageInfo;
import cn.eli486.entity.Customer;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @author eli
 */
@Controller
public class FileController {
    @GetMapping ("/file")
    public String file () {
        return "file";
    }

    @ResponseBody
    @PostMapping ("/upload")
    public Result upload (@RequestParam ("file") MultipartFile file) {
        return handleUpload (file);
    }

    @ResponseBody
    @PostMapping("/uploadMore")
    public Result uploadMore(HttpServletRequest  request){
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles ("file");
        for (MultipartFile f : files) {
             handleUpload (f);
        }
        return new Result (200, "上传成功");
    }

    @ResponseBody
    @GetMapping("/download/{orgCode}")
    public Result download(@PathVariable("orgCode") String orgCode, HttpServletResponse response){
        Customer customer = PageInfo.map.get (orgCode);
        String orgName = customer.getOrgname ();
        String  fileName = GlobalInfo.DIR+"\\V"
                + orgCode.split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
        File file = new File (fileName);
        if(!file.exists ()){
            return new Result (404, "文件不存在") ;
        }
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + orgCode+ ".xls");
        byte[] buffer = new byte[1024];
        try(FileInputStream fileInputStream=new FileInputStream (file) ;
            BufferedInputStream bufferedInputStream = new BufferedInputStream (fileInputStream)){
            OutputStream outputStream = response.getOutputStream ();
            int i =bufferedInputStream.read (buffer);
            while(i!=-1){
                outputStream.write (buffer,0,i);
                i=bufferedInputStream.read (buffer);
            }
            outputStream.flush ();;
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return null;
    }



    private Result handleUpload(MultipartFile file){
        try {
            if (file.isEmpty ()) {
                return new Result (200, "文件为空");
            }
            String dir = "D:/File/";
            String filename = file.getOriginalFilename ();
            String suffix = filename != null ? filename.substring (filename.lastIndexOf (".")) : null;
            String path = dir+filename;
            System.out.println (path);
            File dest = new File (path);
            if (!dest.getParentFile ().exists ()) {
                dest.getParentFile ().mkdirs ();
            }
            file.transferTo (dest);
            return new Result (200, "上传成功");
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return new Result (500, "上传失败");
    }
}
