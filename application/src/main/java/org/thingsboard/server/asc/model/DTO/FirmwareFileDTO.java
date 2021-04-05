package org.thingsboard.server.asc.model.DTO;


import org.thingsboard.server.asc.model.FirmwareFile;

import java.util.Date;

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
    private String fakeUrl;
    private String fileName;

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

    public String getFakeUrl() {
        return fakeUrl;
    }

    public void setFakeUrl(String fakeUrl) {
        this.fakeUrl = fakeUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
                ", fakeUrl='" + fakeUrl + '\'' +
                ", fileName='" + fileName + '\'' +
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
        entity.setFakeUrl(getFakeUrl());
        entity.setFileName(getFileName());
        return entity;
    }
}
