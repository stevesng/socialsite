/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
 * or legal/LICENSE.txt.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at legal/LICENSE.txt.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided by Sun
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.InitializationException;
import com.sun.socialsite.business.ListenerManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.SearchManager;
import com.sun.socialsite.business.SocialSite;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PostRemove;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.Searcher;


/**
 * Lucene implenentation of search manager. Keeps search index up-to-date by
 * listening for changes to Apps, Groups, and Profiles. Also provides a
 * background indexer, which is off by default but can be configured to run
 * via the startup property 'socialsite.search.indexer.pass.frequency'.
 */
@Singleton
public class LuceneSearchManagerImpl implements SearchManager {

    private static Log log = LogFactory.getLog(LuceneSearchManagerImpl.class);

    private File indexDir;

    private Analyzer analyzer = new StandardAnalyzer();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private BulkIndexer backgroundIndexer;

    @Inject
    protected LuceneSearchManagerImpl(ListenerManager listenerManager) {
        log.debug("Instantiating Lucene Search Manager");
        listenerManager.addListener(App.class, new AppListener());
        listenerManager.addListener(Group.class, new GroupListener());
        listenerManager.addListener(Profile.class, new ProfileListener());
    }

    public void initialize() throws InitializationException {

        String s = Config.getProperty("socialsite.search.writer.timeout");
        if (s != null) IndexWriter.setDefaultWriteLockTimeout(Long.parseLong(s));

        indexDir = new File(Config.getProperty("socialsite.search.index.path"));
        boolean needNewIndex = false;
        if (!indexDir.exists()) {
            log.info("Creating new indexDir: " + indexDir.getAbsolutePath());
            indexDir.mkdir();
            needNewIndex = true;
        } else if (indexDir.list().length == 0) {
            log.info("Populating empty indexDir: " + indexDir.getAbsolutePath());
            needNewIndex = true;
        } else {
            log.info("Using existing indexDir: " + indexDir.getAbsolutePath());
        }

        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, new StandardAnalyzer(), needNewIndex);
        } catch (CorruptIndexException ex) {
            throw new InitializationException("Corrupt search index", ex);
        } catch (IOException ex) {
            throw new InitializationException("Failed to create search index", ex);
        } finally {
            if (writer != null) try { writer.close(); } catch (Exception e) {};
        }

        int frequency = Config.getIntProperty("socialsite.search.indexer.pass.frequency");
        int itemsPerPass = Config.getIntProperty("socialsite.search.indexer.pass.items");

        // If creating a new index, make sure it's populated
        if ((needNewIndex) && (itemsPerPass > 0)) {
            log.info("Starting one-time foreground indexer");
            BulkIndexer tempIndexer = new BulkIndexer(this);
            tempIndexer.start();
            tempIndexer.run();
            tempIndexer.stop();
        }

        if ((frequency > 0) && (itemsPerPass > 0)) {
            log.trace("Starting ongoing background indexer");
            backgroundIndexer = new BulkIndexer(this);
            scheduler.scheduleWithFixedDelay(backgroundIndexer, 0, frequency, TimeUnit.SECONDS);
        }
    }

    public void release() {
    }

    public void shutdown() {
        backgroundIndexer.stop();
        scheduler.shutdown();
    }

    /**
     * @return false if the index entry was not updated because it
     * was already current; true otherwise.
     */
    public boolean addToIndex(final App app) throws IOException {

        boolean needNewEntry = true;

        String key = getKey(app);
        String url = app.getURL().toExternalForm();
        String title = app.getTitle();
        String description = app.getDescription();

        IndexReader reader = IndexReader.open(indexDir);
        TermDocs termDocs = reader.termDocs(new Term("key", key));
        while (termDocs.next()) {
            Document existingDoc = reader.document(termDocs.doc());
            if (areEqual("app", existingDoc.get("class"))
             && areEqual(url, existingDoc.get("url"))
             && areEqual(title, existingDoc.get("title"))
             && areEqual(description, existingDoc.get("description"))) {
                needNewEntry = false;
            }
        }
        termDocs.close();
        reader.close();

        if (needNewEntry) {
            Document newDoc = new Document();
            newDoc.add(new Field("key", key, Field.Store.YES, Field.Index.UN_TOKENIZED));
            newDoc.add(new Field("class", "app", Field.Store.YES, Field.Index.UN_TOKENIZED));
            newDoc.add(new Field("url", url, Field.Store.YES, Field.Index.TOKENIZED));
            if (title != null) newDoc.add(new Field("title", title, Field.Store.YES, Field.Index.TOKENIZED));
            if (description != null) newDoc.add(new Field("description", description, Field.Store.YES, Field.Index.TOKENIZED));

            IndexWriter writer = null;
            try {
                writer = new IndexWriter(indexDir, analyzer, false);
                writer.deleteDocuments(new Term("key", key)); // Delete old entry, if present
                writer.addDocument(newDoc);
            } finally {
                if (writer != null) try { writer.close(); } catch (Exception e) {};
            }

            log.trace(String.format("Indexed app[url=%s,title=%s,description=%s]", url, title, description));
        }

        return needNewEntry;
    }

    /**
     * @return false if the index entry was not updated because it
     * was already current; true otherwise.
     */
    public boolean addToIndex(final Group group) throws IOException {

        boolean needNewEntry = true;

        String key = getKey(group);
        String handle = group.getHandle();
        String name = group.getName();
        String description = group.getDescription();

        IndexReader reader = IndexReader.open(indexDir);
        TermDocs termDocs = reader.termDocs(new Term("key", key));
        while (termDocs.next()) {
            Document existingDoc = reader.document(termDocs.doc());
            if (areEqual("group", existingDoc.get("class"))
             && areEqual(handle, existingDoc.get("handle"))
             && areEqual(name, existingDoc.get("name"))
             && areEqual(description, existingDoc.get("description"))) {
                needNewEntry = false;
            }
        }
        termDocs.close();
        reader.close();

        if (needNewEntry) {
            Document newDoc = new Document();
            newDoc.add(new Field("key", key, Field.Store.YES, Field.Index.UN_TOKENIZED));
            newDoc.add(new Field("class", "group", Field.Store.YES, Field.Index.UN_TOKENIZED));
            newDoc.add(new Field("handle", handle, Field.Store.YES, Field.Index.TOKENIZED));
            newDoc.add(new Field("name", name, Field.Store.YES, Field.Index.TOKENIZED));
            if (description != null) newDoc.add(new Field("description", description, Field.Store.YES, Field.Index.TOKENIZED));

            IndexWriter writer = null;
            try {
                writer = new IndexWriter(indexDir, analyzer, false);
                writer.deleteDocuments(new Term("key", key)); // Delete old entry, if present
                writer.addDocument(newDoc);
            } finally {
                if (writer != null) try { writer.close(); } catch (Exception e) {};
            }

            log.trace(String.format("Indexed group[handle=%s,name=%s,description=%s]", name, handle, description));
        }

        return needNewEntry;
    }

    /**
     * @return false if the index entry was not updated because it
     * was already current; true otherwise.
     */
    public boolean addToIndex(final Profile profile) throws IOException {

        boolean needNewEntry = true;

        String key = getKey(profile);
        String userId = profile.getUserId();
        String firstName = profile.getFirstName();
        String middleName = profile.getMiddleName();
        String lastName = profile.getLastName();
        String nickName = profile.getNickName();
        String primaryEmail = profile.getPrimaryEmail();
        String displayName = profile.getDisplayName();

        IndexReader reader = IndexReader.open(indexDir);
        TermDocs termDocs = reader.termDocs(new Term("key", key));
        while (termDocs.next()) {
            Document existingDoc = reader.document(termDocs.doc());
            if (areEqual("profile", existingDoc.get("class"))
             && areEqual(userId, existingDoc.get("userId"))
             && areEqual(firstName, existingDoc.get("firstName"))
             && areEqual(middleName, existingDoc.get("middleName"))
             && areEqual(lastName, existingDoc.get("lastName"))
             && areEqual(nickName, existingDoc.get("nickName"))
             && areEqual(primaryEmail, existingDoc.get("primaryEmail"))
             && areEqual(displayName, existingDoc.get("displayName"))) {
                needNewEntry = false;
            }
        }
        termDocs.close();
        reader.close();

        if (needNewEntry) {
            Document newDoc = new Document();
            newDoc.add(new Field("key", key, Field.Store.YES, Field.Index.UN_TOKENIZED));
            newDoc.add(new Field("class", "profile", Field.Store.YES, Field.Index.UN_TOKENIZED));
            newDoc.add(new Field("userId", userId, Field.Store.YES, Field.Index.UN_TOKENIZED));
            if (firstName != null) newDoc.add(new Field("firstName", firstName, Field.Store.YES, Field.Index.TOKENIZED));
            if (middleName != null) newDoc.add(new Field("middleName", middleName, Field.Store.YES, Field.Index.TOKENIZED));
            if (lastName != null) newDoc.add(new Field("lastName", lastName, Field.Store.YES, Field.Index.TOKENIZED));
            if (nickName != null) newDoc.add(new Field("nickName", nickName, Field.Store.YES, Field.Index.TOKENIZED));
            if (primaryEmail != null) newDoc.add(new Field("primaryEmail", primaryEmail, Field.Store.YES, Field.Index.UN_TOKENIZED));
            if (displayName != null) newDoc.add(new Field("displayName", displayName, Field.Store.YES, Field.Index.TOKENIZED));

            IndexWriter writer = null;
            try {
                writer = new IndexWriter(indexDir, analyzer, false);
                writer.deleteDocuments(new Term("key", key)); // Delete old entry, if present
                writer.addDocument(newDoc);
            } finally {
                if (writer != null) try { writer.close(); } catch (Exception e) {};
            }

            log.trace(String.format("Indexed profile[userId=%s,firstName=%s,lastName=%s,nickName=%s,primaryEmail=%s,displayName=%s]",
                                    userId, firstName, lastName, nickName, primaryEmail, displayName));
        }

        return needNewEntry;
    }

    public void removeFromIndex(final App app) throws IOException {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, analyzer, false);
            writer.deleteDocuments(new Term("key", getKey(app)));
        } finally {
            if (writer != null) try { writer.close(); } catch (Exception e) {};
        }
    }

    public void removeFromIndex(final Group group) throws IOException {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, analyzer, false);
            writer.deleteDocuments(new Term("key", getKey(group)));
        } finally {
            if (writer != null) try { writer.close(); } catch (Exception e) {};
        }
    }

    public void removeFromIndex(final Profile profile) throws IOException {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, analyzer, false);
            writer.deleteDocuments(new Term("key", getKey(profile)));
        } finally {
            if (writer != null) try { writer.close(); } catch (Exception e) {};
        }
    }

    public void optimize() throws IOException {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, analyzer, false);
            writer.optimize();
        } finally {
            if (writer != null) try { writer.close(); } catch (Exception e) {};
        }
    }

    public List<App> getApps(AppManager appManager, int offset, int length, String queryString) throws SocialSiteException {
        IndexReader reader = null;
        Searcher searcher = null;
        try {
            reader = IndexReader.open(indexDir);
            searcher = new IndexSearcher(reader);
            Hits hits = getAppHits(reader, searcher, queryString);
            int endIndex = ((length != -1) ? Math.min(offset+length, hits.length()) : hits.length());
            int numResults = Math.max(endIndex-offset, 0);
            List<App> apps = new ArrayList<App>(numResults);
            for (int i = offset; i < endIndex; i++) {
                Document doc = hits.doc(i);
                apps.add(appManager.getAppByURL(new URL(doc.get("url"))));
            }
            return apps;
        } catch (Exception e) {
            throw ((e instanceof SocialSiteException) ? (SocialSiteException)(e) : new SocialSiteException(e));
        } finally {
            if (reader != null) { try { reader.close(); } catch (IOException e) { } };
            if (searcher != null) { try { searcher.close(); } catch (IOException e) { } };
        }
    }

    public int getTotalApps(String queryString) throws SocialSiteException {
        IndexReader reader = null;
        Searcher searcher = null;
        try {
            reader = IndexReader.open(indexDir);
            searcher = new IndexSearcher(reader);
            Hits hits = getAppHits(reader, searcher, queryString);
            return hits.length();
        } catch (Exception e) {
            throw ((e instanceof SocialSiteException) ? (SocialSiteException)(e) : new SocialSiteException(e));
        } finally {
            if (reader != null) { try { reader.close(); } catch (IOException e) { } };
            if (searcher != null) { try { searcher.close(); } catch (IOException e) { } };
        }
    }

    public List<Group> getGroups(GroupManager groupManager, int offset, int length, String queryString) throws SocialSiteException {
        IndexReader reader = null;
        Searcher searcher = null;
        try {
            reader = IndexReader.open(indexDir);
            searcher = new IndexSearcher(reader);
            Hits hits = getGroupHits(reader, searcher, queryString);
            int endIndex = ((length != -1) ? Math.min(offset+length, hits.length()) : hits.length());
            int numResults = Math.max(endIndex-offset, 0);
            List<Group> groups = new ArrayList<Group>(numResults);
            for (int i = offset; i < endIndex; i++) {
                Document doc = hits.doc(i);
                groups.add(groupManager.getGroupByHandle(doc.get("handle")));
            }
            return groups;
        } catch (Exception e) {
            throw ((e instanceof SocialSiteException) ? (SocialSiteException)(e) : new SocialSiteException(e));
        } finally {
            if (reader != null) { try { reader.close(); } catch (IOException e) { } };
            if (searcher != null) { try { searcher.close(); } catch (IOException e) { } };
        }
    }

    public int getTotalGroups(String queryString) throws SocialSiteException {
        IndexReader reader = null;
        Searcher searcher = null;
        try {
            reader = IndexReader.open(indexDir);
            searcher = new IndexSearcher(reader);
            Hits hits = getGroupHits(reader, searcher, queryString);
            return hits.length();
        } catch (Exception e) {
            throw ((e instanceof SocialSiteException) ? (SocialSiteException)(e) : new SocialSiteException(e));
        } finally {
            if (reader != null) { try { reader.close(); } catch (IOException e) { } };
            if (searcher != null) { try { searcher.close(); } catch (IOException e) { } };
        }
    }

    public List<Profile> getProfiles(ProfileManager profileManager, int offset, int length, String queryString) throws SocialSiteException {
        IndexReader reader = null;
        Searcher searcher = null;
        try {
            reader = IndexReader.open(indexDir);
            searcher = new IndexSearcher(reader);
            Hits hits = getProfileHits(reader, searcher, queryString);
            int endIndex = ((length != -1) ? Math.min(offset+length, hits.length()) : hits.length());
            int numResults = Math.max(endIndex-offset, 0);
            List<Profile> profiles = new ArrayList<Profile>(numResults);
            for (int i = offset; i < endIndex; i++) {
                Document doc = hits.doc(i);
                Profile profile = profileManager.getProfileByUserId(doc.get("userId"));
                if (profile != null) {
                    profiles.add(profile);
                } else {
                    String msg = String.format("Could not find profile for userId: %s", doc.get("userId"));
                    log.warn(msg);
                }
            }
            return profiles;
        } catch (Exception e) {
            throw ((e instanceof SocialSiteException) ? (SocialSiteException)(e) : new SocialSiteException(e));
        } finally {
            if (reader != null) { try { reader.close(); } catch (IOException e) { } };
            if (searcher != null) { try { searcher.close(); } catch (IOException e) { } };
        }
    }

    public int getTotalProfiles(String queryString) throws SocialSiteException {
        IndexReader reader = null;
        Searcher searcher = null;
        try {
            reader = IndexReader.open(indexDir);
            searcher = new IndexSearcher(reader);
            Hits hits = getProfileHits(reader, searcher, queryString);
            return hits.length();
        } catch (Exception e) {
            throw ((e instanceof SocialSiteException) ? (SocialSiteException)(e) : new SocialSiteException(e));
        } finally {
            if (reader != null) { try { reader.close(); } catch (IOException e) { } };
            if (searcher != null) { try { searcher.close(); } catch (IOException e) { } };
        }
    }

    private Hits getAppHits(IndexReader reader, Searcher searcher, String queryString) throws Exception {
        final String[] queryFields = { "url", "title", "description" };
        QueryParser parser = new MultiFieldQueryParser(queryFields, analyzer);
        Query query = parser.parse(queryString);
        Filter classFilter = new RangeFilter("class", "app", "app", true, true);
        Hits hits = searcher.search(query, classFilter);
        log.debug(String.format("Got %d results for query (%s)", hits.length(), queryString));
        return hits;
    }

    private Hits getGroupHits(IndexReader reader, Searcher searcher, String queryString) throws Exception {
        final String[] queryFields = { "name", "handle", "description" };
        QueryParser parser = new MultiFieldQueryParser(queryFields, analyzer);
        Query query = parser.parse(queryString);
        Filter classFilter = new RangeFilter("class", "group", "group", true, true);
        Hits hits = searcher.search(query, classFilter);
        log.debug(String.format("Got %d results for query (%s)", hits.length(), queryString));
        return hits;
    }

    private Hits getProfileHits(IndexReader reader, Searcher searcher, String queryString) throws Exception {
        final String[] queryFields = { "userId", "firstName", "middleName", "lastName", "nickName", "primaryEmail", "displayName" };
        QueryParser parser = new MultiFieldQueryParser(queryFields, analyzer);
        Query query = parser.parse(queryString);
        Filter classFilter = new RangeFilter("class", "profile", "profile", true, true);
        Hits hits = searcher.search(query, classFilter);
        log.debug(String.format("Got %d results for query (%s)", hits.length(), queryString));
        return hits;
    }

    private static String getKey(App app) {
        return new StringBuilder().append("app.").append(app.getId()).toString();
    }

    private static String getKey(Group group) {
        return new StringBuilder().append("group.").append(group.getId()).toString();
    }

    private static String getKey(Profile profile) {
        return new StringBuilder().append("profile.").append(profile.getUserId()).toString();
    }

    // Compares two objects for equality
    // (safely handling cases where one or both are null)
    private static boolean areEqual(Object o1, Object o2) {
        if (o1 != null) {
            return o1.equals(o2);
        } else if (o2 != null) {
            return o2.equals(o1);
        } else {
            // o1 and o2 are both null
            return true;
        }
    }

}


class AppListener {

    private static Log log = LogFactory.getLog(AppListener.class);

    private static boolean doIndex = Config.getBooleanProperty("socialsite.search.app-listener.index");
    private static boolean doOptimize = Config.getBooleanProperty("socialsite.search.app-listener.optimize");

    @PostPersist
    @PostUpdate
    public void appWritten(App app) {
        try {
            if (doIndex) {
                Factory.getSocialSite().getSearchManager().addToIndex(app);
            }
            if (doOptimize) {
                Factory.getSocialSite().getSearchManager().optimize();
            }
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

    @PostRemove
    public void appRemoved(App app) {
        try {
            Factory.getSocialSite().getSearchManager().removeFromIndex(app);
            Factory.getSocialSite().getSearchManager().optimize();
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

}


class GroupListener {

    private static Log log = LogFactory.getLog(GroupListener.class);

    private static boolean doIndex = Config.getBooleanProperty("socialsite.search.group-listener.index");
    private static boolean doOptimize = Config.getBooleanProperty("socialsite.search.group-listener.optimize");

    @PostPersist
    @PostUpdate
    public void groupWritten(Group group) {
        try {
            if (doIndex) {
                Factory.getSocialSite().getSearchManager().addToIndex(group);
            }
            if (doOptimize) {
                Factory.getSocialSite().getSearchManager().optimize();
            }
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

    @PostRemove
    public void groupRemoved(Group group) {
        try {
            Factory.getSocialSite().getSearchManager().removeFromIndex(group);
            Factory.getSocialSite().getSearchManager().optimize();
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

}


class ProfileListener {

    private static Log log = LogFactory.getLog(ProfileListener.class);

    private static boolean doIndex = Config.getBooleanProperty("socialsite.search.profile-listener.index");
    private static boolean doOptimize = Config.getBooleanProperty("socialsite.search.profile-listener.optimize");

    @PostPersist
    @PostUpdate
    public void profileWritten(Profile profile) {
        try {
            if (doIndex) {
                Factory.getSocialSite().getSearchManager().addToIndex(profile);
            }
            if (doOptimize) {
                Factory.getSocialSite().getSearchManager().optimize();
            }
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

    @PostRemove
    public void profileRemoved(Profile profile) {
        try {
            Factory.getSocialSite().getSearchManager().removeFromIndex(profile);
            Factory.getSocialSite().getSearchManager().optimize();
        } catch (IOException e) {
            log.error("IOException", e);
            throw new RuntimeException(e);
        }
    }

}


/**
 * TODO: Document
 */
class BulkIndexer implements Runnable {

    private static Log log = LogFactory.getLog(BulkIndexer.class);

    private static final int itemsPerPass = Config.getIntProperty("socialsite.search.indexer.pass.items");

    private LuceneSearchManagerImpl searchManager;

    public BulkIndexer(LuceneSearchManagerImpl searchManager) {
        this.searchManager = searchManager;
    }

    public void start() {
        if (log.isTraceEnabled()) {
            log.trace(String.format("%s started", getClass().getCanonicalName()));
        }
    }

    public void stop() {
        if (log.isTraceEnabled()) {
            log.trace(String.format("%s stopped", getClass().getCanonicalName()));
        }
    }

    public void run() {

        SocialSite socialsite = Factory.getSocialSite();

        try {

            AppManager appManager = socialsite.getAppManager();
            GroupManager groupManager = socialsite.getGroupManager();
            ProfileManager profileManager = socialsite.getProfileManager();

            int nextAppNum = 0;
            List<App> apps;
            do {
                long startTime = System.currentTimeMillis();
                apps = appManager.getOldestApps(nextAppNum, itemsPerPass);
                long endTime = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    long timeDiff = endTime-startTime;
                    int firstIndex = nextAppNum;
                    int lastIndex = nextAppNum+apps.size();
                    String msg = String.format("[getOldestApps took %dms to get results (%d...%d)]", timeDiff, firstIndex, lastIndex);
                    log.trace(msg);
                }
                for (App app : apps) {
                    log.trace(String.format("calling addToIndex(app[url=%s])", app.getURL()));
                    searchManager.addToIndex(app);
                }
                nextAppNum += apps.size();
                socialsite.release();
                searchManager.optimize();
            } while (apps.size() == itemsPerPass);

            int nextGroupNum = 0;
            List<Group> groups;
            do {
                long startTime = System.currentTimeMillis();
                groups = groupManager.getOldestGroups(nextGroupNum, itemsPerPass);
                long endTime = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    long timeDiff = endTime-startTime;
                    int firstIndex = nextGroupNum;
                    int lastIndex = nextGroupNum+groups.size();
                    String msg = String.format("[getOldestGroups took %dms to get results (%d...%d)]", timeDiff, firstIndex, lastIndex);
                    log.trace(msg);
                }
                for (Group group : groups) {
                    log.trace(String.format("calling addToIndex(group{id=%s})", group.getId()));
                    searchManager.addToIndex(group);
                }
                nextGroupNum += groups.size();
                socialsite.release();
                searchManager.optimize();
            } while (groups.size() == itemsPerPass);

            int nextProfileNum = 0;
            List<Profile> profiles;
            do {
                long startTime = System.currentTimeMillis();
                profiles = profileManager.getOldestProfiles(nextProfileNum, itemsPerPass);
                long endTime = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    long timeDiff = endTime-startTime;
                    int firstIndex = nextProfileNum;
                    int lastIndex = nextProfileNum+profiles.size();
                    String msg = String.format("[getOldestProfiles took %dms to get results (%d...%d)]", timeDiff, firstIndex, lastIndex);
                    log.trace(msg);
                }
                for (Profile profile : profiles) {
                    log.trace(String.format("calling addToIndex(profile{userid=%s})", profile.getUserId()));
                    searchManager.addToIndex(profile);
                }
                nextProfileNum += profiles.size();
                socialsite.release();
                searchManager.optimize();
            } while (profiles.size() == itemsPerPass);
        }
        catch (Exception e) {
            log.error("Exception", e);
        } finally {
            socialsite.release();
        }
    }

}
