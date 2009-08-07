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

/**
 * @fileoverview SocialSite Personal/Group Profile Editor common methods
 */

//----------------------------------------------------------------------------

/**
 * Returns HTML markup to display one property holder, which may have
 * properties, property objects, property object collections and nesting of
 * objects and collections. This is a recursive method.
 */
function displayHolder(basePath, holder) {
    var out = "";

    // Loop though PROPERTIES, display each with appropriate UI control
    if (holder.properties) {
        for (var j=0; j<holder.properties.length; j++) {
            out += generatePropertyEditor(basePath, holder.properties[j]);
        }
    }

    // Loop through section PROPERTY OBJECT definitions
    if (holder.propertyObjects) {
        for (var k=0; k<holder.propertyObjects.length; k++) {
            var name = basePath + "_" + holder.propertyObjects[k].short_name;
            out += '<div id="' + name + '" class="propertyObject">';
            out += holder.propertyObjects[k].local_name;
            out += '<table class="propertyTable">';
            out += displayHolder(name, holder.propertyObjects[k]);
            out += '</table>';
            out += '</div>';
        }
    }

    // Loop through PROPERTY OBJECT COLLECTION definitions
    if (holder.propertyObjectCollections) {
        for (var m=0; m<holder.propertyObjectCollections.length; m++) {
            out += '<div id="' + holder.propertyObjectCollections[m].name + '" class="propertyObjectCollectionTitle">';
            out += holder.propertyObjectCollections[m].local_name;
            out += '<a onclick=\"insertPropertyObjectCollectionEditor(\'' + holder.propertyObjectCollections[m].name + '\',\'' + basePath + "_" + holder.propertyObjectCollections[m].short_name + '\')\">';
            out += '&nbsp;<img class="linkIcon" src="' + baseImageURL + '/add.png" />&nbsp;Add one</a>?';
            out += '</div>';

            // Show the property object collection objects matching current definition
            for (var poci=1; poci<20; poci++) {

                var present = false;
                var pocBasePath = basePath + "_" + holder.propertyObjectCollections[m].short_name.replace('{n}', poci);
                for (var pocp=0; pocp<holder.propertyObjectCollections[m].properties.length; pocp++) {
                    var name = pocBasePath + "_" + holder.propertyObjectCollections[m].properties[pocp].short_name;
                    if (profile[name]) {
                        present = true;
                        break;
                    }
                }
                if (present) {
                    out += generatePropertyObjectCollectionEditor(pocBasePath, holder.propertyObjectCollections[m]);
                }
            }
        }
    }
    return out;
}

//----------------------------------------------------------------------------

/**
 * Returns HTML table row for editing specified property, in the form of a string
 */
function generatePropertyEditor(basePath, propertyDef) {
    var fullname = basePath + "_" + propertyDef.short_name;
    var propertyValue = profile[fullname];
    var out = '<label class="formLabel" for="' + fullname + '">' + propertyDef.local_name + '</label>';

    if (propertyDef.type == 'string' && !propertyDef.allowedValues) {
        out += '<input id="' + fullname + '" name="' + fullname + '"  class="formInput" size="40" ';
        if (propertyValue) {
            out += 'value=\'' + propertyValue + '\' />';
        } else {
            out += '/>';
        }

    } else if (propertyDef.type == 'integer') {
        out += '<input id="' + fullname + '" name="' + fullname + '"  class="formInput" size="10" ';
        if (propertyValue) {
            out += 'value=\'' + propertyValue + '\' />';
        } else {
            out += '/>';
        }

    } else if (propertyDef.type == 'stringenum' && propertyDef.allowedValues) {
        out += '<select id="' + fullname + '" name="' + fullname + '"  class="formInput">';
        for (var a=0; a<propertyDef.allowedValues.length; a++) {
            if (propertyValue == propertyDef.allowedValues[a].name) {
                out += '<option selected="selected" value="';
            } else {
                out += '<option value="';
            }
            out += propertyDef.allowedValues[a].name + '">' + propertyDef.allowedValues[a].local_name + '</option>';
        }
        out += '</select>';

    } else if (propertyDef.type == 'enum' && propertyDef.allowedValues) {
        out += '<select id="' + fullname + '" name="' + fullname + '"  class="formInput">';
        for (var a=0; a<propertyDef.allowedValues.length; a++) {
            if (propertyValue == propertyDef.allowedValues[a].name) {
                out += '<option selected="selected" value="';
            } else {
                out += '<option value="';
            }
            out += propertyDef.allowedValues[a].name + '">' + propertyDef.allowedValues[a].local_name + '</option>';
        }
        out += '</select>';

    } else if (propertyDef.type == 'date') {
        out += '<input id="' + fullname + '" name="' + fullname + '"  class="formInput date-pick" size="40" ';
        if (propertyValue) {
            out += 'value=\'' + propertyValue + '\' />';
        } else {
            out += '/>';
        }

    } else if (propertyDef.type == 'text' || propertyDef.type == 'stringarray') {
        out += '<textarea id="' + fullname + '" name="' + fullname + '" cols="40" class="formInput">';
        if (propertyValue) {
            out += propertyValue;
        }
        out += '</textarea>';

    } else if (propertyDef.type == 'boolean') {
        out += '<input id="' + fullname + '" type="checkbox" name="' + fullname+ '" class="formInput" ';
        if (propertyValue && propertyValue == 'true') {
            out += 'checked="' + propertyValue + '" value="' + propertyValue + '" ';
        } else {
            out += ' " value="' + propertyValue + '" ';
        }
        out += '/>';
    }

    out += '<br />';
    return out;
}


//----------------------------------------------------------------------------

/**
 * Inserts property object editor after the HTML div with id equal to the object name
 */
function insertPropertyObjectCollectionEditor(objectName, instanceName) {

    var collectionDef = profileDefinition.getPropertyObjectCollectionDefinition(objectName);
    var out = generatePropertyObjectCollectionEditor(instanceName, collectionDef);

    // use document.getElementById() here because jQuery has problems with our curly braces
    var insertionPoint = $(document.getElementById(objectName)).after(out);

    // new inputs added, so enable dirty check for them if appropriate
    enableDirtyCheck();
}


//----------------------------------------------------------------------------

/**
 * Inserts property object editor after the HTML div with id equal to the object name
 */
function generatePropertyObjectCollectionEditor(objectName, collectionDef) {
    var objectIndex = 1;
    for (var pci=1; pci<20; pci++) {
        objectIndex = pci;
        if (!document.getElementById(objectName.replace('{n}', pci))) {
            break;
        }
    }
    var fullname = objectName.replace('{n}', objectIndex);
    var out = '';
    out += '<div class="propertyObjectCollection" id="' + fullname + '">';
    out += '<div style="float:right"><a onclick="deletePropertyObjectCollection(\'';
    out += collectionDef.name + '\',\'' + objectIndex
    out += '\')"><img class="linkIcon" src="' + baseImageURL + '/cancel.png" /></a></div>';
    out += displayHolder(fullname, collectionDef);
    out += '</div>';
    return out;
}


//----------------------------------------------------------------------------

/**
 * Delete property object from UI, will be deleted on server on next save
 */
function deletePropertyObjectCollection(objectName, objectIndex) {
    $(document.getElementById( objectName.replace('{n}',objectIndex))).hide();
    var po = profileDefinition.getPropertyObjectCollectionDefinition(objectName);
    for (var pi=0; pi<po.properties.length; pi++) {
        var propertyName = po.properties[pi].name.replace('{n}', objectIndex);
        $(document.getElementById(propertyName)).val('zzz_DELETE_zzz');
    }
}


//----------------------------------------------------------------------------

function collectProperties(basePath, holder, allProperties) {
   var foundProps = false;
   if (holder.properties) {
       for (var pi=0; pi<holder.properties.length; pi++) {
           var name = basePath + "_" + holder.properties[pi].short_name;
           if (document.getElementById(name)) {
               if (holder.properties[pi].type == 'boolean') {
                   if (document.getElementById(name).checked) {
                       allProperties[name] = true;
                   } else {
                       allProperties[name] = false;
                   }
               } else {
                   allProperties[name] = document.getElementById(name).value;
               }
               foundProps = true;
           }
       }
   }
   if (holder.propertyObjectCollections) {
       for (var pci=0; pci<holder.propertyObjectCollections.length; pci++) {
           for (var pcj=1; pcj<20; pcj++) {
               var name = basePath + "_" + holder.propertyObjectCollections[pci].short_name;
               name = name.replace('{n}',pcj);
               if (!collectProperties(name, holder.propertyObjectCollections[pci], allProperties)) break;
           }
       }
   }
   if (holder.propertyObjects) {
       for (var poi=0; poi<holder.propertyObjects.length; poi++) {
           var name = basePath + "_" + holder.propertyObjects[poi].short_name;
           if (!collectProperties(name, holder.propertyObjects[poi], allProperties)) break;
       }
   }
   return foundProps;
}
