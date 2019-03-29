/*
 * Copyright 2018 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.servlet.examples.models;

import com.okta.sdk.resource.user.factor.FactorType;

import java.util.Map;

public class Factor {

    private String id;

    private String type;

    private String provider;

    private String vendorName;

    private String extraInfo;

    private Map<String, Object> profile;

    private String verifyHref;

    public Factor() {}

    public Factor(String id, String type, String provider, String vendorName, String extraInfo, Map<String, Object> profile, String verifyHref) {
        this.id = id;
        this.type = type;
        this.provider = provider;
        this.vendorName = vendorName;
        this.extraInfo = extraInfo;
        this.profile = profile;
        this.verifyHref = verifyHref;
    }

    public String getId() {
        return id;
    }

    public Factor setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Factor setType(String type) {
        this.type = type;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public Factor setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public String getVendorName() {
        return vendorName;
    }

    public Factor setVendorName(String vendorName) {
        this.vendorName = vendorName;
        return this;
    }

    public Map<String, Object> getProfile() {
        return profile;
    }

    public Factor setProfile(Map<String, Object> profile) {
        this.profile = profile;
        return this;
    }

    public String getVerifyHref() {
        return verifyHref;
    }

    public Factor setVerifyHref(String verifyHref) {
        this.verifyHref = verifyHref;
        return this;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public Factor setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
        return this;
    }

    public static String relativeLink(com.okta.authn.sdk.resource.Factor factor) {
        FactorType type = factor.getType();
        String templateName;
        switch (type) {
            case U2F:
                templateName = "u2f";
                break;
            case TOKEN_SOFTWARE_TOTP:
                templateName = "totp";
                break;
            case SMS:
                templateName = "sms";
                break;
            default:
                templateName = "unknown";
        }

        return templateName;
    }

    public static FactorType fromRelativeLink(String type) {
        FactorType oktaType;
        switch (type) {
            case "u2f":
                oktaType = FactorType.U2F;
                break;
            case "totp":
                oktaType = FactorType.TOKEN_SOFTWARE_TOTP;
                break;
            case "sms":
                oktaType = FactorType.SMS;
                break;
            default:
                oktaType = null;
        }
        return oktaType;
    }
}