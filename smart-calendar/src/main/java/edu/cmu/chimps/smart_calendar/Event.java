package edu.cmu.chimps.smart_calendar;


public class Event {
    private String mEventName;
    private Long mBeginTime;
    private Long mEndTime;
    private String mLocation;

    public Long getBeginTime() {
        return mBeginTime;
    }

    public void setBeginTime(Long beginTime) {
        mBeginTime = beginTime;
    }

    public Long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Long endTime) {
        mEndTime = endTime;
    }

    public String getEventName() {
        return mEventName;
    }

    public void setEventName(String eventName) {
        mEventName = eventName;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
         mLocation = location;
    }
}
