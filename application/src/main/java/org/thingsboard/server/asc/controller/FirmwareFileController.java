package org.thingsboard.server.asc.controller;


import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.asc.model.DTO.FirmwareFileDTO;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.asc.service.FirmwareFileService;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;


@RestController
@RequestMapping("/api")
public class FirmwareFileController {

    private final FirmwareFileService firmwareFileService;

    public FirmwareFileController(FirmwareFileService firmwareFileService) {
        this.firmwareFileService = firmwareFileService;
    }

    @PostMapping("/firmware-file")
    public FirmwareFileDTO uploadData(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null) {
            throw new RuntimeException("You must select the a file for uploading");
        }

        Instant instant = Instant.now();
        byte [] byteArr=file.getBytes();

        FirmwareFileDTO fileDTO = new FirmwareFileDTO();

        InputStream inputStream = new ByteArrayInputStream(byteArr);
        String originalName = file.getOriginalFilename();
        String name = file.getName();
        String contentType = file.getContentType();
        long size = file.getSize();
        System.out.println("inputStream: " + inputStream);
        System.out.println("originalName: " + originalName);
        System.out.println("name: " + name);
        System.out.println("contentType: " + contentType);
        System.out.println("size: " + size);
        // Do processing with uploaded file data in Service layer

        fileDTO.setFile(byteArr);
        fileDTO.setFileType(contentType);

        FirmwareFileDTO result = firmwareFileService.save(fileDTO);

        return result;
    }

    @GetMapping("/firmware-file")
    public List<FirmwareFileDTO> getAllFiles() {
        List<FirmwareFileDTO> files = firmwareFileService.findAll();
        return files;
    }

    @GetMapping("/firmware-file/{id}")
    public FirmwareFileDTO getFile(@PathVariable Long id) {
        FirmwareFileDTO fileDTO = firmwareFileService.findOne(id);
        return fileDTO;
    }

    @GetMapping("/firmware-file/path/{id}")
    public void getPath(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        FirmwareFileDTO fileDTO = firmwareFileService.findOne(id);

//        try {
//            InputStream inputStream = new ByteArrayInputStream(fileDTO.getFile());
//            BufferedInputStream in = new BufferedInputStream(inputStream);
//            FileOutputStream fos = new FileOutputStream(new File("/home/obada/newFile"));
//            BufferedOutputStream bout = new BufferedOutputStream(fos , 1024);
//            byte[] buffer = new byte[1024];
//            double downloaded = 0.00;
//            int read=0;
//            double percentDownloaded = 0.00;
//            while ((read = in.read(buffer,0,1024)) >=0){
//                bout.write(buffer,0,read);
//                downloaded += read;
//                percentDownloaded = (downloaded*100/500.0);
//                String percent = String.format("%.4f",percentDownloaded);
//                System.out.println("Downloaded "+percent + "% of a file.");
//            }
//            bout.close();
//            in.close();
//            System.out.println("Download Complete.");
//
//        }
//        catch (IOException ex){
//            ex.printStackTrace();
//        }


        try{
            response.setHeader("Content-Disposition", "inline; filename=\"" + "fileName" + "\"");
            OutputStream out = response.getOutputStream();
            response.setContentType("application/octet-stream");
            IOUtils.copy(new ByteArrayInputStream(fileDTO.getFile()), out);
            out.flush();
            out.close();


        } catch (IOException e) {
            System.out.println(e.toString());
            //Handle exception here
        }

    }


    @DeleteMapping("firmware-file")
    public String deleteAllFiles() {
        firmwareFileService.deleteAll();
        return "DELETED!";
    }

    @DeleteMapping("/firmware-file/{id}")
    public String deleteFile(@PathVariable Long id) {
        firmwareFileService.delete(id);
        return "DELETED!";
    }
}

