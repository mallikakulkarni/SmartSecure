package com.mysjsu.mobsecurity;

public class BaseQueryBuilder {
   public String getDatabaseName() {
        return "smartsecure";
    }

    public String getApiKey() {
        return "01Mafx1RracMooXGqfrbdse3y4w4pjri";
    }


    public String getBaseUrl()
    {
        return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
    }


    public String docApiKeyUrl()
    {

        return "?apiKey="+getApiKey();
    }

    public String andApiKeyUrl()
    {
        return "&apiKey="+getApiKey();
    }

    public String docApiKeyUrl(String docid)
    {
        return "/"+docid+"?apiKey="+getApiKey();
    }

}
