package firmware.controller;

import firmware.model.DTO.FirmwareFileDTO;
import firmware.service.FirmwareFileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class FirmwareFileController {

    private final FirmwareFileService firmwareFileService;

    public FirmwareFileController(FirmwareFileService firmwareFileService) {
        this.firmwareFileService = firmwareFileService;
    }

    @PostMapping("/firmware-file")
    public FirmwareFileDTO createFile(@RequestBody FirmwareFileDTO firmwareFileDTO){
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

//    @DeleteMapping("/firmware-file/{id}")
//    public String deleteAscOrder(@PathVariable Long id) {
//        firmwareFileService.delete(id);
//        return "DELETED!";
//    }
}
