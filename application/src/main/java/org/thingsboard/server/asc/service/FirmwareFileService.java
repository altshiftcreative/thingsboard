package org.thingsboard.server.asc.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.asc.model.DTO.FirmwareFileDTO;
import org.thingsboard.server.asc.model.FirmwareFile;
import org.thingsboard.server.asc.repository.FirmwareFileRepository;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FirmwareFileService {

    public FirmwareFileRepository firmwareFileRepository;
    public FirmwareFileService( FirmwareFileRepository firmwareFileRepository) {
        this.firmwareFileRepository = firmwareFileRepository;
    }

    protected SecurityUser getCurrentUser() throws ThingsboardException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new ThingsboardException("You aren't authorized to perform this operation!", ThingsboardErrorCode.AUTHENTICATION);
        }
    }


    public FirmwareFileDTO save(FirmwareFileDTO fileDTO) throws ThingsboardException {
        if (fileDTO.getId() == null) {
            Date date = new Date();
            fileDTO.setCreationDate(date);

            UUID uuid = UUID.randomUUID();
            String fakeUrl = ""+uuid;
            fileDTO.setFakeUrl(fakeUrl);

            String username = getCurrentUser().getName();
            fileDTO.setUsername(username);
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

    public String findOneByFakeUrl(String url) {
        String realUrl = firmwareFileRepository.findByFakeUrl(url);
        return realUrl;
    }

    public void deleteAll(){
        firmwareFileRepository.deleteAll();
    }

    public void delete(Long id){
        firmwareFileRepository.deleteById(id);
    }
}
