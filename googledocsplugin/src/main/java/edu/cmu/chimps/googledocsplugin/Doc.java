package edu.cmu.chimps.googledocsplugin;


public class Doc {
    private String mDocName;
    private String mDocUrl;
    private Long mCreatedTime;

    public Long getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(Long createdTime) {
        mCreatedTime = createdTime;
    }

    public String getDocName() {
        return mDocName;
    }

    public void setDocName(String docName) {
            mDocName = docName;
        }

    public String getDocUrl() {
            return mDocUrl;
        }


    public void setDocUrl(String docUrl) {
            mDocUrl = docUrl;
        }
}
