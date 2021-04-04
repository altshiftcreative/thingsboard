package firmware.model.DTO;

import firmware.model.FirmwareFile;

import java.sql.Blob;
import java.time.Instant;

public class FirmwareFileDTO {

    private Long id;
    private String username;
    private Blob file;
    private String modelNumber;
    private String fileType;
    private String deviceType;
    private String firmwareVersion;
    private String checksum;
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
        return "FirmwareFileDTO{" +
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

    public FirmwareFile toEntity() {
        FirmwareFile entity = new FirmwareFile();
        entity.setId(getId());
        entity.setUsername(getUsername());
        entity.setFile(getFile());
        entity.setModelNumber(getModelNumber());
        entity.setFileType(getFileType());
        entity.setDeviceType(getDeviceType());
        entity.setFirmwareVersion(getFirmwareVersion());
        entity.setChecksum(getChecksum());
        entity.setCreationDate(getCreationDate());
        return entity;
    }
}
