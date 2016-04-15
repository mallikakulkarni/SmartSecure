package com.mysjsu.mobsecurity;

public class UserTestDataQueryBuilder {

    BaseQueryBuilder qb = new BaseQueryBuilder();

    public String getUserCollection() {
        return "smartsecuretest";
    }

    public String buildUserSaveURL() {
        return qb.getBaseUrl() + getUserCollection() + qb.docApiKeyUrl();
    }
}
