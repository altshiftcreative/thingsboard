package firmware.service;

import firmware.model.DTO.FirmwareFileDTO;
import firmware.model.FirmwareFile;
import firmware.repository.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FirmwareFileService {

    public FirmwareFileRepository firmwareFileRepository;

    @Autowired
    public FirmwareFileService(FirmwareFileRepository firmwareFileRepository) {
        this.firmwareFileRepository = firmwareFileRepository;
    }


    public FirmwareFileDTO save(FirmwareFileDTO fileDTO) {
        if (fileDTO.getId() == null) {
            fileDTO.setCreationDate(Instant.now());
        }
        FirmwareFile file = fileDTO.toEntity();
        firmwareFileRepository.save(file);
        return file.toDTO();
    }

    public List<FirmwareFileDTO> findAll() {
        List<FirmwareFile> files = firmwareFileRepository.findAll();
        List<FirmwareFileDTO> fileDtoList=new ArrayList<FirmwareFileDTO>();
        files.forEach(x->{
            fileDtoList.add(x.toDTO());
        });
        return fileDtoList;
    }

    public FirmwareFileDTO findOne(Long id) {
        FirmwareFile files = firmwareFileRepository.findById(id).get();
        return files.toDTO();
    }
}
