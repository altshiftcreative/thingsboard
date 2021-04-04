package org.thingsboard.server.firmware.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.firmware.model.FirmwareFile;

@Repository
public interface FirmwareFileRepository extends JpaRepository<FirmwareFile,Long>{

}






