package com.elsevier.entellect.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

@JsonRootName("evt:details")
public class EventDetails {

    @JsonProperty("ecm:identifier")
    private String ecmIdentifier;

    @JsonProperty("dct:date")
    private String dctDate;

    @JsonProperty("dct:modified")
    private String dctModified;

    @JsonProperty("dct:language")
    private String dctLanguage;

    @JsonProperty("dct:title")
    private String dctTitle;

    @JsonProperty("bam:subtype")
    private String bamSubtype;

    @JsonProperty("bam:patentClassificationCpc")
    private List<String> cpc;

    @JsonProperty("bam:patentClassificationIpcr")
    private List<String> ipcr;

    @JsonProperty("bam:patentClassification")
    private List<String> patentClassification;

    @JsonProperty("bam:assetId")
    private String assetId;

    public EventDetails(){

    }

    public String getEcmIdentifier() {
        return ecmIdentifier;
    }

    public void setEcmIdentifier(String ecmIdentifier) {
        this.ecmIdentifier = ecmIdentifier;
    }

    public String getDctDate() {
        return dctDate;
    }

    public void setDctDate(String dctDate) {
        this.dctDate = dctDate;
    }

    public String getDctModified() {
        return dctModified;
    }

    public void setDctModified(String dctModified) {
        this.dctModified = dctModified;
    }

    public String getDctLanguage() {
        return dctLanguage;
    }

    public void setDctLanguage(String dctLanguage) {
        this.dctLanguage = dctLanguage;
    }

    public String getDctTitle() {
        return dctTitle;
    }

    public void setDctTitle(String dctTitle) {
        this.dctTitle = dctTitle;
    }

    public String getBamSubtype() {
        return bamSubtype;
    }

    public void setBamSubtype(String bamSubtype) {
        this.bamSubtype = bamSubtype;
    }

    public List<String> getCpc() {
        return cpc;
    }

    public void setCpc(List<String> cpc) {
        this.cpc = cpc;
    }

    public List<String> getIpcr() {
        return ipcr;
    }

    public void setIpcr(List<String> ipcr) {
        this.ipcr = ipcr;
    }

    public List<String> getPatentClassification() {
        return patentClassification;
    }

    public void setPatentClassification(List<String> patentClassification) {
        this.patentClassification = patentClassification;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
