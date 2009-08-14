-- DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
--
-- Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
--
-- The contents of this file are subject to the terms of either the GNU
-- General Public License Version 2 only ("GPL") or the Common Development
-- and Distribution License("CDDL") (collectively, the "License").  You
-- may not use this file except in compliance with the License. You can obtain
-- a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
-- or legal/LICENSE.txt.  See the License for the specific language governing
-- permissions and limitations under the License.
--
-- When distributing the software, include this License Header Notice in each
-- file and include the License file at legal/LICENSE.txt.  Sun designates this
-- particular file as subject to the "Classpath" exception as provided by Sun
-- in the GPL Version 2 section of the License file that accompanied this code.
-- If applicable, add the following below the License Header, with the fields
-- enclosed by brackets [] replaced by your own identifying information:
-- "Portions Copyrighted [year] [name of copyright owner]"
--
-- Contributor(s):
--
-- If you wish your version of this file to be governed by only the CDDL or
-- only the GPL Version 2, indicate your decision by adding "[Contributor]
-- elects to include this software in this distribution under the [CDDL or GPL
-- Version 2] license."  If you don't indicate a single choice of license, a
-- recipient has the option to distribute your version of this file under
-- either the CDDL, the GPL Version 2 or to extend the choice of license to
-- its licensees as provided above.  However, if you add GPL Version 2 code
-- and therefore, elected the GPL Version 2 license, then the option applies
-- only if the new code is made subject to such option by the copyright
-- holder.



-- Run this script to create the SocialSite tables in your database.


-- *****************************************************
-- * Create the SocialSite tables and indices.
-- *****************************************************

create table ss_themes (
    id            varchar(48) not null primary key,
    destination   varchar(48),
    anchorcolor   varchar(48),
    bgcolor       varchar(48),
    bgimage       varchar(48),
    fontcolor     varchar(48)
);
insert into ss_themes(id, destination, anchorcolor, bgcolor, bgimage, fontcolor) values('socialsiteDefault', 'socialsite', '#36546C', '#ffffff', '', '#333333');

create table ss_configproperty (
    name         varchar(255) not null primary key,
    value        clob(102400)
);

create table ss_profile (
    id           varchar(48) not null primary key,
    userid       varchar(100) not null,
    lastname     varchar(100),
    middlename   varchar(100),
    firstname    varchar(100),
    primaryemail varchar(255),
    surtitle     varchar(100),
    displayname  varchar(100),
    nickname     varchar(100),
    vistype      varchar(10) not null,
    vislevel     integer not null,
    image        blob(512000),
    imagetype    varchar(255),
    enabled      smallint default 1,
    created      timestamp not null,
    updated      timestamp not null
);
alter table ss_profile add constraint ss_profile_c1 unique(userid);
create index ss_profile_index1 on ss_profile(lastname asc, firstname asc, middlename asc);
create index ss_profile_index2 on ss_profile(created asc);
create index ss_profile_index3 on ss_profile(updated desc);

create table ss_profileprop (
    id         varchar(48) not null primary key,
    name       varchar(128) not null,
    profileid  varchar(48) not null,
    namekey    varchar(128) not null,
    value      clob(102400) not null,
    type       varchar(20) not null,
    vistype    varchar(10) not null,
    vislevel   integer not null,
    somegroups varchar(512),
    created    timestamp not null,
    updated    timestamp not null
);
alter table ss_profileprop add constraint ss_profileprop_c1 unique(profileid, name);
create index ss_profileprop_index1 on ss_profileprop(profileid);

create table ss_groupprop (
    id         varchar(48) not null primary key,
    name       varchar(128) not null,
    groupid    varchar(48) not null,
    namekey    varchar(128) not null,
    value      clob(102400) not null,
    vistype    varchar(10) not null,
    vislevel   integer not null,
    created    timestamp not null,
    updated    timestamp not null
);
alter table ss_groupprop add constraint ss_groupprop_c1 unique(groupid, name);
create index ss_groupprop_index1 on ss_groupprop(groupid);

-- for a friendship, two of these will exist
create table ss_userrel (
    id             varchar(48) not null primary key,
    fromprofileid  varchar(48) not null,
    toprofileid    varchar(48) not null,
    level          integer not null,
    howknow        varchar(512),
    created        timestamp not null,
    updated        timestamp not null
);
alter table ss_userrel add constraint ss_userrel_c1 unique(fromprofileid, toprofileid);
create index ss_userrel_index1 on ss_userrel(fromprofileid);
create index ss_userrel_index2 on ss_userrel(toprofileid);

create table ss_activity (
    id             varchar(48) not null primary key,
    appid          varchar(48),
    type           varchar(48),
    title          clob(102400),
    titleid        varchar(48),
    body           clob(102400),
    bodyid         varchar(48),
    externalid     varchar(48),
    profileid      varchar(48) not null,
    groupid        varchar(48),
    created        timestamp not null,
    updated        timestamp not null
);
create index ss_activity_index1 on ss_activity(profileid);

create table ss_mediaitem (
    id             varchar(48) not null primary key,
    activityid     varchar(48) not null,
    mimetype       varchar(48),
    type           varchar(48),
    url            clob(102400)
);

create table ss_templateparam (
    id             varchar(48) not null primary key,
    activityid     varchar(48) not null,
    name           clob(102400),
    value          clob(102400)
);

create table ss_socialrequest (
    id            varchar(48) not null primary key,
    fromprofileid varchar(48) not null,
    fromlevel     integer,
    toprofileid   varchar(48),
    tolevel       integer,
    howknow       varchar(512),
    groupid       varchar(48),
    requesttype   varchar(48) not null,
    status        varchar(48) not null,
    created       timestamp not null
);

    alter table ss_socialrequest add constraint ss_socialrequest_c1 unique(fromprofileid, toprofileid, groupid, requesttype);

create table ss_grouprel (
    id             varchar(48) not null primary key,
    profileid      varchar(48) not null,
    relcode        varchar(48) not null,
    groupid        varchar(48) not null,
    admin          smallint,
    created        timestamp not null,
    updated        timestamp not null
);
alter table ss_grouprel add constraint ss_grouprel_c1 unique(profileid, relcode, groupid);
create index ss_grouprel_index1 on ss_grouprel(profileid);

create table ss_content (
    id           varchar(48) not null primary key,
    profileid    varchar(48),
    groupid      varchar(48),
    appid        varchar(48),
    toprofileid  varchar(48),
    replytoid    varchar(48),
    title        varchar(48),
    summary      clob(102400),
    desctype     varchar(48),
    description  varchar(255) not null,
    created      timestamp not null,
    updated      timestamp not null,
    content      clob(102400),
    contenttype  varchar(48),
    visibility   varchar(48),
    hidden       smallint,
    status       varchar(48),
    catscheme    varchar(128),
    catlabel     varchar(48)
);
create index ss_content_index1 on ss_content(profileid);
create index ss_content_index2 on ss_content(groupid);
create index ss_content_index3 on ss_content(description);
create index ss_content_index4 on ss_content(updated desc);

create table ss_contentstate (
    id           varchar(48) not null primary key,
    messageid    varchar(48) not null,
    profileid    varchar(48) not null,
    groupid      varchar(48) not null,
    status       varchar(48)
);
create index ss_contentstate_index1 on ss_contentstate(profileid);
create index ss_contentstate_index2 on ss_contentstate(groupid);

create table ss_group (
    id             varchar(48) not null primary key,
    name           varchar(255) not null,
    handle         varchar(48) not null,
    description    varchar(1024),
    policy         varchar(48),
    domainsAllowed varchar(128),
    locale         varchar(48),
    image          blob(512000),
    imagetype      varchar(255),
    created        timestamp not null,
    updated        timestamp not null
);
alter table ss_group add constraint ss_group_c1 unique(handle);
create index ss_group_index1 on ss_group(created asc);
create index ss_group_index2 on ss_group(updated desc);
create index ss_group_index3 on ss_group(name asc);

create table ss_tag (
    id       varchar(48) not null primary key,
    scheme   varchar(128),
    label    varchar(48),
    updated  timestamp not null
);

create table ss_app (
    id                     varchar(48) not null primary key,
    url                    varchar(255) not null,
    title                  varchar(255) not null,
    titleurl               varchar(255),
    directorytitle         varchar(255),
    showindirectory        smallint,
    description            clob(102400),
    thumbnail              varchar(255),
    height                 integer,
    width                  integer,
    scrolling              smallint,
    singleton              smallint,
    author                 varchar(255),
    authoremail            varchar(255),
    authorlink             varchar(255),
    created                timestamp not null,
    updated                timestamp not null
);
alter table ss_app add constraint ss_app_c1 unique(url);
create index ss_app_index1 on ss_app(title asc);

create table ss_appdata (
    id                  varchar(48) not null primary key,
    appid               varchar(48) not null,
    profileid           varchar(48),
    groupid             varchar(48),
    name                varchar(255) not null,
    value               clob(102400) not null,
    created             timestamp not null,
    updated             timestamp not null
);

    alter table ss_appdata add constraint ss_appdata_c1 unique(appid, profileid, groupid, name);
create index ss_appdata_index1 on ss_appdata(appid);

create table ss_appinstance (
    id             integer not null primary key,
    appid          varchar(48) not null,
    profileid      varchar(48),
    groupid        varchar(48),
    collection     varchar(48),
    position       varchar(100),
    created        timestamp not null,
    updated        timestamp not null
);
create index ss_appinstance_index1 on ss_appinstance(profileid);
create index ss_appinstance_index2 on ss_appinstance(groupid);

create table ss_permissiongrant (
    id                  varchar(48) not null primary key,
    profileid           varchar(48),
    groupid             varchar(48),
    appid               varchar(48),
    gadgetdomain        varchar(255),
    type                varchar(48) not null,
    name                varchar(48),
    actions             varchar(255),
    created             timestamp not null,
    updated             timestamp not null
);
create index ss_permissiongrant_index1 on ss_permissiongrant(profileid);
create index ss_permissiongrant_index2 on ss_permissiongrant(groupid);
create index ss_permissiongrant_index3 on ss_permissiongrant(appid);
create index ss_permissiongrant_index4 on ss_permissiongrant(type);

create table ss_ids (
    name                varchar(48) not null primary key,
    value               integer not null
);
insert into ss_ids(name, value) values('appinstance', 0);


-- *****************************************************
-- * Create the UserAPI tables and indices.
-- *****************************************************

create table userapi_user (
    id                   varchar(48) not null primary key,
    userid               varchar(255) not null,
    passphrase           varchar(255) not null,
    username             varchar(255) not null,
    full_name            varchar(255) not null,
    email_address        varchar(255) not null,
    home_address         varchar(255),
    activation_code      varchar(48),
    creation_time        timestamp not null,
    update_time          timestamp not null,
    access_time          timestamp not null,
    locale               varchar(20),  
    timezone             varchar(50),    
    isenabled            smallint default 1 not null,
    security_question    varchar(255),
    security_answer      varchar(255),
    bio                  varchar(2048)
);
alter table userapi_user add constraint userapi_user_c1 unique(username);
create index userapi_user_index1 on userapi_user(userid);
create index userapi_user_index2 on userapi_user(username);

create table userapi_userrole (
    id                   varchar(48) not null primary key,
    rolename             varchar(255) not null
);
alter table userapi_userrole add constraint userapi_userrole_c1 unique(rolename);

create table userapi_permission (
    id                   varchar(48) not null primary key,
    user_id              varchar(48) not null,
    actions              varchar(255),
    object_id            varchar(48),
    object_type          varchar(255),
    pending              smallint default 1,
    creation_time        timestamp not null 
);
alter table userapi_permission add constraint userapi_permission_fk1 foreign key(user_id) references userapi_user(id);

create table userapi_user_userrole (
    user_id              varchar(48) not null,
    role_id              varchar(48) not null
);
alter table userapi_user_userrole add constraint userapi_user_userrole_pk primary key(user_id, role_id);
alter table userapi_user_userrole add constraint userapi_user_userrole_fk1 foreign key(user_id) references userapi_user(id);
alter table userapi_user_userrole add constraint userapi_user_userrole_fk2 foreign key(role_id) references userapi_userrole(id);
create index userapi_user_userrole_index1 on userapi_user_userrole(user_id);

create view userapi_userrole_view (role_id, rolename, username) as
  select r.id, r.rolename, u.username
  from userapi_userrole r, userapi_user u, userapi_user_userrole rel
  where rel.user_id = u.id and rel.role_id = r.id;
