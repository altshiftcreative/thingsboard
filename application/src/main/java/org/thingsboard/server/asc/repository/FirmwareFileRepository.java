package org.thingsboard.server.asc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.asc.model.FirmwareFile;


@Repository
public interface FirmwareFileRepository extends JpaRepository<FirmwareFile,Long>{


}






