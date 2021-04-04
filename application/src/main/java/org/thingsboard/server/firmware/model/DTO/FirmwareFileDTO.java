package org.thingsboard.server.firmware.model.DTO;


import org.thingsboard.server.firmware.model.FirmwareFile;

import java.sql.Blob;
import java.util.Date;
import java.time.Instant;

public class FirmwareFileDTO {

    private Long id;
    private String username;
    private String file;
    private int modelNumber;
    private String fileType;
    private String deviceType;
    private Double firmwareVersion;
    private String checksum;
    private Date creationDate;
    private String sampleUrl;

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

    public String getSampleUrl() {
        return sampleUrl;
    }

    public void setSampleUrl(String sampleUrl) {
        this.sampleUrl = sampleUrl;
    }

    @Override
    public String toString() {
        return "FirmwareFileDTO{" +
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
        entity.setSampleUrl(getSampleUrl());
        return entity;
    }
}
