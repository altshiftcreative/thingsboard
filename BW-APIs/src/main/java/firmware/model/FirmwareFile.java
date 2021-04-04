package firmware.model;

import firmware.model.DTO.FirmwareFileDTO;
import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;
import java.time.Instant;

@Data
@Entity
@Table(name = "firmware-file")
public class FirmwareFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "file")
    private Blob file;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "firmware_version")
    private String firmwareVersion;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "creation_date")
    private Instant creationDate;

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "FirmwareFile{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", file=" + file +
                ", modelNumber='" + modelNumber + '\'' +
                ", fileType='" + fileType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", checksum='" + checksum + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }

    public FirmwareFileDTO toDTO() {
        FirmwareFileDTO dto = new FirmwareFileDTO();
        dto.setId(getId());
        dto.setUsername(getUsername());
        dto.setFile(getFile());
        dto.setModelNumber(getModelNumber());
        dto.setFileType(getFileType());
        dto.setDeviceType(getDeviceType());
        dto.setFirmwareVersion(getFirmwareVersion());
        dto.setChecksum(getChecksum());
        dto.setCreationDate(getCreationDate());
        return dto;
    }
}
