package org.thingsboard.server.firmware.model;

import lombok.Data;
import org.thingsboard.server.firmware.model.DTO.FirmwareFileDTO;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;
import java.time.Instant;

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
    private String file;

    @Column(name = "modelnumber")
    private int modelNumber;

    @Column(name = "filetype")
    private String fileType;

    @Column(name = "devicetype")
    private String deviceType;

    @Column(name = "firmwareversion")
    private Double firmwareVersion;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "creationdate")
    private Date creationDate;

    @Column(name = "sampleurl")
    private String sampleUrl;

    public String getSampleUrl() {
        return sampleUrl;
    }

    public void setSampleUrl(String sampleUrl) {
        this.sampleUrl = sampleUrl;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
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

    @Override
    public String toString() {
        return "FirmwareFile{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", file='" + file + '\'' +
                ", modelNumber=" + modelNumber +
                ", fileType='" + fileType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", firmwareVersion=" + firmwareVersion +
                ", checksum='" + checksum + '\'' +
                ", creationDate=" + creationDate +
                ", sampleUrl='" + sampleUrl + '\'' +
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
        dto.setSampleUrl(getSampleUrl());
        return dto;
    }
}
