package edu.cmu.chimps.smart_calendar;


public class Event {
    private String EventName;
    private Long BeginTime;
    private Long EndTime;
    private String Location;

    public Long getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(Long beginTime) {
        BeginTime = beginTime;
    }

    public Long getEndTime() {
        return EndTime;
    }

    public void setEndTime(Long endTime) {
        EndTime = endTime;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
