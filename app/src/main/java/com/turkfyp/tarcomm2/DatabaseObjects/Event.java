package com.turkfyp.tarcomm2.DatabaseObjects;

/**
 * Created by User-PC on 11/10/2017.
 */

public class Event {

    private String eventName;
    private String eventDateTime;
    private String eventEndDateTime;
    private String eventDesc;
    private String eventImageURL;

    private String eventVenue;
    private String eventVenueName;
    private String eventHighlight;



    public Event()
    {
        this.setEventName("");
        this.setEventDateTime("");
        this.setEventDesc("");
        this.setEventImageURL("");
        this.setEventVenue("");
        this.setEventVenueName("");
        this.setEventEndDateTime("");
    }


    public Event(String eventName, String eventDateTime, String eventDesc, String eventImageURL, String eventVenue, String eventVenueName, String eventHighlight, String eventEndDateTime)
    {
        this.setEventName(eventName);
        this.setEventDateTime(eventDateTime);
        this.setEventDesc(eventDesc);
        this.setEventImageURL(eventImageURL);
        this.setEventVenue(eventVenue);
        this.setEventVenueName(eventVenueName);
        this.setEventHighlight(eventHighlight);
        this.setEventEndDateTime(eventEndDateTime);
    }



    //GETTER AND SETTER
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }


    public String getEventImageURL() {
        return eventImageURL;
    }

    public void setEventImageURL(String eventImageURL) {
        this.eventImageURL = eventImageURL;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventVenueName() { return eventVenueName; }

    public void setEventVenueName(String eventVenueName) { this.eventVenueName = eventVenueName; }

    public String getEventHighlight() { return eventHighlight; }

    public void setEventHighlight(String eventHighlight) { this.eventHighlight = eventHighlight; }

    public String getEventEndDateTime() {
        return eventEndDateTime;
    }

    public void setEventEndDateTime(String eventEndDateTime) {
        this.eventEndDateTime = eventEndDateTime;
    }
}
