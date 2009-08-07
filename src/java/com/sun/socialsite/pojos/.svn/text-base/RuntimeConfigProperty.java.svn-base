/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.pojos;

import com.sun.socialsite.business.impl.JPAListenerManagerImpl;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Represents a single runtime property of the system.
 */
@Entity
@Table(name="ss_configproperty")
@EntityListeners({ JPAListenerManagerImpl.Listener.class })
@NamedQuery(name="RuntimeConfigProperty.getAll", query="SELECT r FROM RuntimeConfigProperty r")
public class RuntimeConfigProperty implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String name = null;

    private String value = null;

    public RuntimeConfigProperty() {
    }

    public RuntimeConfigProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of this property.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this property.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of this property.
     * @return Value of property value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value of this property.
     * @param value New value of property value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return String.format("%s[%s=%s]", getClass().getSimpleName(), this.name, this.value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof RuntimeConfigProperty != true) return false;
        RuntimeConfigProperty o = (RuntimeConfigProperty)other;
        return new EqualsBuilder().append(getName(), o.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).toHashCode();
    }

}
