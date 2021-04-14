package org.thingsboard.server.asc.model;

import lombok.Data;
import org.thingsboard.server.asc.model.DTO.FirmwareFileDTO;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

@Data
@Entity
@Table(name = "firmware_file")
public class FirmwareFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "file")
    private byte[] file;

    @Column(name = "model_number")
    private int modelNumber;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "firmware_version")
    private Double firmwareVersion;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "filename")
    private String fileName;

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

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(int modelNumber) {
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

    public Double getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(Double firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FirmwareFile{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", file=" + Arrays.toString(file) +
                ", modelNumber=" + modelNumber +
                ", fileType='" + fileType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", firmwareVersion=" + firmwareVersion +
                ", checksum='" + checksum + '\'' +
                ", creationDate=" + creationDate +
                ", fileName='" + fileName + '\'' +
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
        dto.setFileName(getFileName());
        return dto;
    }
}
