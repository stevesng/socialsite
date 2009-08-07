/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.sun.socialsite.business.impl;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.pojos.OAuthAccessorRecord;
import com.sun.socialsite.pojos.OAuthConsumerRecord;
import net.oauth.OAuth;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerIndex;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreTokenIndex;
import org.apache.shindig.gadgets.oauth.OAuthStore;
import org.apache.shindig.gadgets.oauth.OAuthStore.ConsumerInfo;
import org.apache.shindig.gadgets.oauth.OAuthStore.TokenInfo;


/**
 * Stores consumer key, secrets and tokens.
 * Enables SocailSite to act as an OAuth consumer.
 */
@Singleton
public class JPAOAuthStore implements OAuthStore {

    private static Log log = LogFactory.getLog(JPAOAuthStore.class);
    private final JPAPersistenceStrategy strategy;
    private static final String CONSUMER_SECRET_KEY = "consumer_secret";
    private static final String CONSUMER_KEY_KEY = "consumer_key";
    private static final String KEY_TYPE_KEY = "key_type";

    /**
     * Key to use when no other key is found.
     */
    private BasicOAuthStoreConsumerKeyAndSecret defaultKey;
    /** Number of times we looked up a consumer key */
    private int consumerKeyLookupCount = 0;
    /** Number of times we looked up an access token */
    private int accessTokenLookupCount = 0;
    /** Number of times we added an access token */
    private int accessTokenAddCount = 0;
    /** Number of times we removed an access token */
    private int accessTokenRemoveCount = 0;
    

    @Inject
    public JPAOAuthStore(JPAPersistenceStrategy strategy) {
        this.strategy = strategy;
    }

    public void saveConsumerInfo(String gadgetUri, String serviceName,
            String consumerKey, String consumerSecret, String keyTypeStr) {

        KeyType keyType = KeyType.HMAC_SYMMETRIC;
        if (keyTypeStr.equals("RSA_PRIVATE")) {
            keyType = KeyType.RSA_PRIVATE;
            consumerSecret = convertFromOpenSsl(consumerSecret);
        }

        BasicOAuthStoreConsumerKeyAndSecret kas = new BasicOAuthStoreConsumerKeyAndSecret(
                consumerKey, consumerSecret, keyType, null);

        BasicOAuthStoreConsumerIndex index = new BasicOAuthStoreConsumerIndex();
        index.setGadgetUri(gadgetUri);
        index.setServiceName(serviceName);
        setConsumerKeyAndSecret(index, kas);
    }

    public void removeConsumerInfo(String gadgetUri, String serviceName) {
        BasicOAuthStoreConsumerIndex index = new BasicOAuthStoreConsumerIndex();
        index.setGadgetUri(gadgetUri);
        index.setServiceName(serviceName);
        consumerInfosRemove(index);
    }

    public BasicOAuthStoreConsumerKeyAndSecret
            getConsumerInfo(String gadgetUri, String serviceName) {
        BasicOAuthStoreConsumerIndex pk = new BasicOAuthStoreConsumerIndex();
        pk.setGadgetUri(gadgetUri);
        pk.setServiceName(serviceName);
        return consumerInfosGet(pk);
    }

    // Support standard openssl keys by stripping out the headers and blank lines
    public static String convertFromOpenSsl(String privateKey) {
        return privateKey.replaceAll("-----[A-Z ]*-----", "").replace("\n", "");
    }

    public void setDefaultKey(BasicOAuthStoreConsumerKeyAndSecret defaultKey) {
        this.defaultKey = defaultKey;
    }

    public void setConsumerKeyAndSecret(
            BasicOAuthStoreConsumerIndex providerKey, BasicOAuthStoreConsumerKeyAndSecret keyAndSecret) {
        consumerInfosPut(providerKey, keyAndSecret);
    }

    public ConsumerInfo getConsumerKeyAndSecret(
            SecurityToken securityToken, String serviceName, OAuthServiceProvider provider)
            throws GadgetException {
        ++consumerKeyLookupCount;
        BasicOAuthStoreConsumerIndex pk = new BasicOAuthStoreConsumerIndex();
        pk.setGadgetUri(securityToken.getAppUrl());
        pk.setServiceName(serviceName);
        BasicOAuthStoreConsumerKeyAndSecret cks = consumerInfosGet(pk);
        if (cks == null) {
            cks = defaultKey;
        }
        if (cks == null) {
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR,
                    "No key for gadget " + securityToken.getAppUrl() + " and service " + serviceName);
        }
        OAuthConsumer consumer = null;
        if (cks.getKeyType() == KeyType.RSA_PRIVATE) {
            consumer = new OAuthConsumer(null, cks.getConsumerKey(), null, provider);
            // The oauth.net java code has lots of magic.  By setting this property here, code thousands
            // of lines away knows that the consumerSecret value in the consumer should be treated as
            // an RSA private key and not an HMAC key.
            consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);
            consumer.setProperty(RSA_SHA1.PRIVATE_KEY, cks.getConsumerSecret());
        } else {
            consumer = new OAuthConsumer(null, cks.getConsumerKey(), cks.getConsumerSecret(), provider);
            consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
        }
        return new ConsumerInfo(consumer, cks.getKeyName());
    }

    private BasicOAuthStoreTokenIndex makeBasicOAuthStoreTokenIndex(
            SecurityToken securityToken, String serviceName, String tokenName) {
        BasicOAuthStoreTokenIndex tokenKey = new BasicOAuthStoreTokenIndex();
        tokenKey.setGadgetUri(securityToken.getAppUrl());
        tokenKey.setModuleId(securityToken.getModuleId());
        tokenKey.setServiceName(serviceName);
        tokenKey.setTokenName(tokenName);
        tokenKey.setUserId(securityToken.getViewerId());
        return tokenKey;
    }

    public TokenInfo getTokenInfo(SecurityToken securityToken, ConsumerInfo consumerInfo,
            String serviceName, String tokenName) {
        ++accessTokenLookupCount;
        BasicOAuthStoreTokenIndex tokenKey =
                makeBasicOAuthStoreTokenIndex(securityToken, serviceName, tokenName);
        return tokensGet(tokenKey);
    }

    public void setTokenInfo(SecurityToken securityToken, ConsumerInfo consumerInfo,
            String serviceName, String tokenName, TokenInfo tokenInfo) {
        ++accessTokenAddCount;
        BasicOAuthStoreTokenIndex tokenKey =
                makeBasicOAuthStoreTokenIndex(securityToken, serviceName, tokenName);
        tokensPut(tokenKey, tokenInfo);
    }

    public void removeToken(SecurityToken securityToken, ConsumerInfo consumerInfo,
            String serviceName, String tokenName) {
        ++accessTokenRemoveCount;
        BasicOAuthStoreTokenIndex tokenKey =
                makeBasicOAuthStoreTokenIndex(securityToken, serviceName, tokenName);
        tokensRemove(tokenKey);
    }

    public int getConsumerKeyLookupCount() {
        return consumerKeyLookupCount;
    }

    public int getAccessTokenLookupCount() {
        return accessTokenLookupCount;
    }

    public int getAccessTokenAddCount() {
        return accessTokenAddCount;
    }

    public int getAccessTokenRemoveCount() {
        return accessTokenRemoveCount;
    }

    
    public void consumerInfosPut(
            BasicOAuthStoreConsumerIndex providerKey,
            BasicOAuthStoreConsumerKeyAndSecret keyAndSecret) {
        try {
            OAuthConsumerRecord record = (OAuthConsumerRecord)
                strategy.load(OAuthConsumerRecord.class, providerKey.hashCode());
            if (record == null) {
                record = new OAuthConsumerRecord(providerKey, keyAndSecret);
            }
            strategy.store(record);
            Factory.getSocialSite().flush();

        } catch (SocialSiteException ex) {
            log.error("ERROR putting consumer info", ex);
        }
    }

    public BasicOAuthStoreConsumerKeyAndSecret consumerInfosGet(
            BasicOAuthStoreConsumerIndex pk) {
        try {
            OAuthConsumerRecord record = (OAuthConsumerRecord)
                strategy.load(OAuthConsumerRecord.class, pk.hashCode());
            if (record != null) {
                return record.getBasicOAuthStoreConsumerKeyAndSecret();
            }
        } catch (SocialSiteException ex) {
            log.error("ERROR getting consumer info", ex);
        }
        return null;
    }

    private void consumerInfosRemove(BasicOAuthStoreConsumerIndex index) {
        try {
            strategy.remove(OAuthConsumerRecord.class, index.hashCode());
        } catch (SocialSiteException ex) {
            log.error("ERROR removing consumer info", ex);
        }
    }

    public void tokensPut(
            BasicOAuthStoreTokenIndex tokenKey, TokenInfo tokenInfo) {
        try {
            OAuthAccessorRecord record = (OAuthAccessorRecord)
                strategy.load(OAuthAccessorRecord.class, tokenKey.hashCode());
            if (record == null) {
                record = new OAuthAccessorRecord(tokenKey, tokenInfo);
            }
            strategy.store(record);
            Factory.getSocialSite().flush();
            
        } catch (SocialSiteException ex) {
            log.error("ERROR putting token info", ex);
        }
    }

    public TokenInfo tokensGet(BasicOAuthStoreTokenIndex tokenKey) {
        try {
            OAuthAccessorRecord record = (OAuthAccessorRecord)
                strategy.load(OAuthAccessorRecord.class, tokenKey.hashCode());
            if (record != null) {
                return record.getTokenInfo();
            }

        } catch (SocialSiteException ex) {
            log.error("ERROR getting token info", ex);
        }
        return null;
    }

    private void tokensRemove(BasicOAuthStoreTokenIndex tokenKey) {
        try {
            strategy.remove(OAuthAccessorRecord.class, tokenKey.hashCode());
            Factory.getSocialSite().flush();

        } catch (SocialSiteException ex) {
            log.error("ERROR removing token info", ex);
        }
    }
}
