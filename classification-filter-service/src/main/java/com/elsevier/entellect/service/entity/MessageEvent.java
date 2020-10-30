package com.elsevier.entellect.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

@JsonRootName("msg:event")
public class MessageEvent {

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("evt:time")
    private String evtTime;

    @JsonProperty("evt:type")
    private String evtType;

    @JsonProperty("evt:about")
    private String evtAbout;

    @JsonProperty("evt:resource")
    private List<String> evtResource;

    @JsonProperty("evt:details")
    private List<EventDetails> evtDetails;

     public MessageEvent(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvtTime() {
        return evtTime;
    }

    public void setEvtTime(String evtTime) {
        this.evtTime = evtTime;
    }

    public String getEvtType() {
        return evtType;
    }

    public void setEvtType(String evtType) {
        this.evtType = evtType;
    }

    public String getEvtAbout() {
        return evtAbout;
    }

    public void setEvtAbout(String evtAbout) {
        this.evtAbout = evtAbout;
    }

    public List<String> getEvtResource() {
        return evtResource;
    }

    public void setEvtResource(List<String> evtResource) {
        this.evtResource = evtResource;
    }

    public List<EventDetails> getEvtDetails() {
        return evtDetails;
    }

    public void setEvtDetails(List<EventDetails> evtDetails) {
        this.evtDetails = evtDetails;
    }

}
