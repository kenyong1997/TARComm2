package com.turkfyp.tarcomm2.DatabaseObjects;

/**
 * Created by User-PC on 11/10/2017.
 */

public class Event {

    private String eventName;
    private String eventDateTime;
    private String eventCreator;
    private String eventDesc;
    private String eventImageURL;


    private String eventVenue;




    public Event()
    {
        this.setEventName("");
        this.setEventCreator("");
        this.setEventDateTime("");
        this.setEventDesc("");
        this.setEventImageURL("");
        this.setEventVenue("");
    }


    public Event(String eventName, String eventCreator, String eventDateTime, String eventDesc, String eventImageURL, String eventVenue)
    {
        this.setEventName(eventName);
        this.setEventCreator(eventCreator);
        this.setEventDateTime(eventDateTime);
        this.setEventDesc(eventDesc);
        this.setEventImageURL(eventImageURL);
        this.setEventVenue(eventVenue);
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

    public String getEventCreator() {
        return eventCreator;
    }

    public void setEventCreator(String eventCreator) {
        this.eventCreator = eventCreator;
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
}
