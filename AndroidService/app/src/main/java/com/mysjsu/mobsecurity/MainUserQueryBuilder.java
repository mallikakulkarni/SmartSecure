package com.mysjsu.mobsecurity;

public class MainUserQueryBuilder {

    BaseQueryBuilder qb = new BaseQueryBuilder();

    public String getUserCollection() {
        return "MasterUserTable";
    }

    public String buildUserSaveURL() {
        return qb.getBaseUrl() + getUserCollection() + qb.docApiKeyUrl();
    }
}
