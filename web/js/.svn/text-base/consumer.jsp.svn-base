<%--
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2008 Sun Microsystems, Inc. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 2 only ("GPL") or the Common Development
 and Distribution License("CDDL") (collectively, the "License").  You
 may not use this file except in compliance with the License. You can obtain
 a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
 or legal/LICENSE.txt.  See the License for the specific language governing
 permissions and limitations under the License.

 When distributing the software, include this License Header Notice in each
 file and include the License file at legal/LICENSE.txt.  Sun designates this
 particular file as subject to the "Classpath" exception as provided by Sun
 in the GPL Version 2 section of the License file that accompanied this code.
 If applicable, add the following below the License Header, with the fields
 enclosed by brackets [] replaced by your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 Contributor(s):

 If you wish your version of this file to be governed by only the CDDL or
 only the GPL Version 2, indicate your decision by adding "[Contributor]
 elects to include this software in this distribution under the [CDDL or GPL
 Version 2] license."  If you don't indicate a single choice of license, a
 recipient has the option to distribute your version of this file under
 either the CDDL, the GPL Version 2 or to extend the choice of license to
 its licensees as provided above.  However, if you add GPL Version 2 code
 and therefore, elected the GPL Version 2 license, then the option applies
 only if the new code is made subject to such option by the copyright
 holder.
--%>

<%--
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
--%>

<%@ page language="java" %>
<%@ page contentType="application/x-javascript; charset=utf-8" %>
<%
  String nocache = com.sun.socialsite.config.Config.getProperty("socialsite.shindig.nocache");
%>

if (typeof socialsite == 'undefined') socialsite = new function() {

  var baseUrl = '<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()%>';
  var nextContainerElementNum = 0;
  var onloadCompleted = false;
  var origOnload = window.onload;
  var context = {};
  var requestItems = {};
  var responseItems = {};
  var currentLightbox = null;

  /*
   * Used for functions which we really consider to be private but
   * have to expose outside of this class.
   */
  this.private = {};

  /**
   * @public
   */
  this.addGadget = function(params) {
    var elementId = ((params.useLightbox) ? randomId() : nextElementId());
    requestItems[elementId] = params;
    if (onloadCompleted == true) callGadgetizer();
  }

  /**
   * @public
   */
  this.addGadgets = function(params) {
    var elementId = ((params.useLightbox) ? randomId() : nextElementId());
    requestItems[elementId] = params;
    if (onloadCompleted == true) callGadgetizer();
  }

  /**
   * @public
   */
  this.setContext = function(params) {
    context = params;
  }

  /**
   * If parameter does not exist, returns an empty string ("").
   * @public
   */
  this.getUrlParam = function(name) {
    var regex = new RegExp("[\\?&]"+name+"=([^&#]*)");
    var results = regex.exec(window.location.href);
    return (results != null) ? results[1] : "";
  }

  this.openLightbox = function(title, url, width, height) {
    // For now, we'll only support one lightbox at a time
    if (this.currentLightbox) {
      this.closeLightbox();
    }
    width = ((width != null) ? width : 600);
    height = ((height != null) ? height : 500);
    this.currentLightbox = jmaki.lbm.addLightbox({
      label: title,
      include: url,
      iframe: true,
      modal: true,
      overflowY: 'auto',
      overflowX: 'auto',
      startWidth: width,
      startHeight: height
    });
  }

  this.closeLightbox = function() {
    if (this.currentLightbox) {
      jmaki.lbm.hideLightbox({targetId:this.currentLightbox.id});
      jmaki.lbm.removeLightbox({targetId:this.currentLightbox.id});
      this.currentLightbox = null;
    }
  }

  /**
   * @private
   */
  this.private.handleServerResponse = function(response) {
    responseItems = response.gadgets;
    if (onloadCompleted == true) {
      createAndRenderGadgets();
    } else {
      // Only load css and libs on the first call
      var css = response.css;
      for (i in css) {
        var link = document.createElement('link');
        link.setAttribute('rel', 'stylesheet');
        link.setAttribute('type', 'text/css');
        link.setAttribute('href', css[i]);
        document.getElementsByTagName('head')[0].appendChild(link);
      }
      var libs = response.libs;
      libs.push(baseUrl+'/gadgets/files/container/socialsite_layoutmanager.js');
      loadLibs(libs, function() {
        jmaki.webRoot = baseUrl+'/';
        var wDir = baseUrl+'/resources/jmaki/lightboxManager';
        jmaki.addExtension({ name: "jmaki.lightboxManager", widgetDir: wDir });
        jmaki.initialize();
        jmaki.lbm = jmaki.getExtension("jmaki.lightboxManager");
        // TODO: provide API for consumers to specify their own onload-like additions
        if (typeof(socialsite_addGadget) == 'function') {
          gadgets.rpc.register('socialsite_addGadget', socialsite_addGadget);
        }
        gadgets.rpc.setRelayUrl(document.location.href);
        if (response.containerHelper && response.containerHelper.spec) {
          var requestItem = {
              'includeChrome': false
          };
          var responseItem = response.containerHelper;
          var containerElement = document.getElementsByTagName('body')[0];
          gadgets.container.containerHelper = createAndRenderGadget(requestItem, responseItem, containerElement);
        }
        createAndRenderGadgets();
        onloadCompleted = true;
      });
    }
  }

  /**
   * @private
   * @param libs an array of javascript URLs
   * @param callback be a function that will be executed once all libs are loaded
   */
  function loadLibs(libs, callback) {
    loadNextLib = function() {
      var lib = libs.pop();
      var script = document.createElement('script');
      script.setAttribute('type', 'text/javascript');
      script.setAttribute('src', lib);
      if (libs.length > 0) {
        script.onload = function() {
          loadNextLib(libs, callback);
        }
        // Need this because IE won't fire the onload event
        script.onreadystatechange = function () {
          if (this.readyState == 'loaded' || this.readyState == 'complete') loadNextLib(libs, callback);
        }
      } else {
        script.onload = function() {
          callback();
        }
        // Need this because IE won't fire the onload event
        script.onreadystatechange = function () {
          if (this.readyState == 'loaded') callback();
        }
      }
      document.getElementsByTagName('head')[0].appendChild(script);
    }
    loadNextLib(libs.reverse());
  }

  /**
   * @private
   */
  function createAndRenderGadgets() {
    for (i in responseItems) {
      if (typeof responseItems[i] !== 'function') {
        var requestItem = requestItems[i];
        for (j in responseItems[i]) {
          if (typeof responseItems[i][j] !== 'function') {
            var responseItem = responseItems[i][j];
            createAndRenderGadget(requestItem, responseItem, document.getElementById(i));
          }
        }
        delete requestItems[i];
      }
    }
  }

  /**
   * @private
   */
  function createAndRenderGadget(requestItem, responseItem, containerElement) {
    gadgets.container.setNoCache(<%=nocache%>);
    var title = ((responseItem.title != null) ? responseItem.title : 'Title');
    var height = ((responseItem.height != null) ? responseItem.height : '200px');
    var width = ((responseItem.width != null) ? responseItem.width : '100%');
    var containerId = ((responseItem.containerId != null) ? responseItem.containerId : 'socialsite');
    var removable = ((requestItem.removable != null) ? requestItem.removable : true);
    var includeChrome = ((requestItem.includeChrome != null) ? requestItem.includeChrome : true);
    var serverBase = ((responseItem.serverBase != null) ? (responseItem.serverBase+'/') : (baseUrl+'/gadgets/'));
    var resourceBase = ((responseItem.resourceBase != null) ? (responseItem.resourceBase+'/') : serverBase);
    if (requestItem.useLightbox == true) {
      currentLightbox = jmaki.lbm.addLightbox({
        id: 'gadget-chrome-'+responseItem.moduleId,
        label: title,
        content: ' ',
        iframe: true,
        modal: true,
        overflowY: 'auto',
        overflowX: 'auto',
        startWidth: ((width != '100%') ? width : 500),
        startHeight: ((height != '100%') ? height : 600)
      });
    } else {
      var chrome = document.createElement('div');
      chrome.id = 'gadget-chrome-'+responseItem.moduleId;
      if ((height == 0) && (width == 0)) {
          chrome.style.height = "0px";
          chrome.style.width = "0px";
          chrome.style.overflow = "hidden";
      }
      containerElement.appendChild(chrome);
    }
    var gadget = gadgets.container.createGadget({
      id: responseItem.moduleId,
      moduleId: responseItem.moduleId,
      CONTAINER: containerId,
      specUrl: responseItem.spec,
      title: title,
      height: height,
      width: width,
      removable: removable,
      includeChrome: includeChrome,
      secureToken: responseItem.token
    });
    gadget.setServerBase(serverBase);
    gadget.setResourceBase(resourceBase);
    gadget.setUserPrefs(responseItem.userPrefs);
    gadgets.container.addGadget(gadget);
    gadgets.container.renderGadget(gadget);
    return gadget;
  }

  /*
   * Calls the originally-set onload function (if there was one).
   * Then makes our remote call to the gadgetizerdata service, passing
   * in any addGadget requestItems that were present in our html page.
   */
  window.onload = function() {
    if (origOnload) {
      origOnload();
    }
    callGadgetizer();
  }

  /**
   * @private
   */
  function callGadgetizer() {
    var uri = baseUrl+'/gadgetizerdata';
    uri += '?callback='+encodeURIComponent("socialsite.private.handleServerResponse");
    uri += '&context='+encodeURIComponent(toJsonString(context));
    uri += '&items='+encodeURIComponent(toJsonString(requestItems));
    var script = document.createElement('script');
    script.setAttribute('type', 'text/javascript');
    script.setAttribute('src', uri);
    document.getElementsByTagName('head')[0].appendChild(script);
  }

  /**
   * @private
   */
  function nextElementId() {
    var elementId = 'gadget-container-'+nextContainerElementNum++;
    document.write('<div id="'+elementId+'"></div>');
    return elementId;
  }

  /**
   * @private
   */
  function randomId() {
    return (0x7FFFFFFF * Math.random());
  }

  /**
   * Derived from json.org code.
   * @private
   */
  function toJsonString(arg) {
    return toJsonStringArray(arg).join('');
  }

  /**
   * Derived from json.org code.
   * @private
   */
  function toJsonStringArray(arg, out) {
    out = out || new Array();
    var _undefined;
    switch (typeof arg) {
      case 'object':
        if (arg) {
          if (arg.constructor == Array) {
            out.push('[');
            for (var i = 0; i < arg.length; ++i) {
              if (i > 0)
                out.push(',\n');
              toJsonStringArray(arg[i], out);
            }
            out.push(']');
            return out;
          } else if (typeof arg.toString != 'undefined') {
            out.push('{');
            var first = true;
            for (var i in arg) {
              var curr = out.length; // Record position to allow undo when arg[i] is undefined.
              if (!first)
                out.push(',\n');
              toJsonStringArray(i, out);
              out.push(':');
              toJsonStringArray(arg[i], out);
              if (out[out.length - 1] == _undefined)
                out.splice(curr, out.length - curr);
              else
                first = false;
            }
            out.push('}');
            return out;
          }
          return out;
        }
        out.push('null');
        return out;
      case 'unknown':
      case 'undefined':
      case 'function':
        out.push(_undefined);
        return out;
      case 'string':
        out.push('"')
        out.push(arg.replace(/(["\\])/g, '\\$1').replace(/\r/g, '').replace(/\n/g, '\\n'));
        out.push('"');
        return out;
      default:
        out.push(String(arg));
        return out;
    }
  }

}
