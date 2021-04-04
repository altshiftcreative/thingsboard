package firmware.repository;

import firmware.model.FirmwareFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirmwareFileRepository extends JpaRepository<FirmwareFile, Long> {

}






