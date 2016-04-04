package com.mysjsu.mobsecurity;

public class UserQueryBuilder {

    BaseQueryBuilder qb = new BaseQueryBuilder();

    public String getUserCollection() {
        return "smartsecure";
    }

    public String buildUserSaveURL() {
        return qb.getBaseUrl() + getUserCollection() + qb.docApiKeyUrl();
    }
}
