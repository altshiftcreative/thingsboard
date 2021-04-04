package org.thingsboard.server.firmware.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thingsboard.server.firmware.model.DTO.FirmwareFileDTO;
import org.thingsboard.server.firmware.model.FirmwareFile;
import org.thingsboard.server.firmware.repository.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FirmwareFileService {

    public FirmwareFileRepository firmwareFileRepository;
    public FirmwareFileService( FirmwareFileRepository firmwareFileRepository) {
        this.firmwareFileRepository = firmwareFileRepository;
    }


    public FirmwareFileDTO save(FirmwareFileDTO fileDTO) {
        if (fileDTO.getId() == null) {
            Date date = new Date();
            fileDTO.setCreationDate(date);
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
