package org.thingsboard.server.asc.controller;


import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.asc.model.DTO.FirmwareFileDTO;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.asc.service.FirmwareFileService;

import java.util.List;


@RestController
@RequestMapping("/api")
public class FirmwareFileController {

    private final FirmwareFileService firmwareFileService;

    public FirmwareFileController(FirmwareFileService firmwareFileService) {
        this.firmwareFileService = firmwareFileService;
    }

    @PostMapping("/firmware-file")
    public FirmwareFileDTO createFile(@RequestBody FirmwareFileDTO firmwareFileDTO) throws ThingsboardException {
        if (firmwareFileDTO.getId() != null) {
//            throw new BadRequestAlertException("A new ascOrder cannot already have an ID", ENTITY_NAME, "idexists");
            return null;
        } else {
            FirmwareFileDTO result = firmwareFileService.save(firmwareFileDTO);
            return result;
        }

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
    public String getPath(@PathVariable Long id) {
        FirmwareFileDTO fileDTO = firmwareFileService.findOne(id);
        return fileDTO.getFile();
    }

    @GetMapping("/firmware-file/fakeUrl/{url}")
    public String getRealPath(@RequestParam String url){
        String realUrl = firmwareFileService.findOneByFakeUrl(url);
        return realUrl;
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

