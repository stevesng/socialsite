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


/*
 * ------------------------------------------------------------
 * Define our own customized version of gadgets.Gadget
 * ------------------------------------------------------------
 */

gadgets.SocialSiteIfrGadget = function(opt_params) {
  gadgets.IfrGadget.call(this, opt_params);
}

gadgets.SocialSiteIfrGadget.inherits(gadgets.IfrGadget);

gadgets.SocialSiteIfrGadget.prototype.CONTAINER = 'socialsite';

gadgets.IfrGadget.prototype.getMainContent = function(continuation) {
  var iframeId = this.getIframeId();
  continuation('<div class="'+this.cssClassGadgetContent+'"' +
      (this.height ? ' style="height: '+this.height+'px;"' : '') +
      '><iframe id="'+iframeId+'" name="'+iframeId+'" class="'+this.cssClassGadget +
      '" src="'+this.getIframeUrl() +
      '" frameborder="no" scrolling="no"' +
      (this.height ? ' height="'+this.height+'"' : '') +
      (this.width ? ' width="'+this.width+'"' : '') +
      '></iframe></div>');
  gadgets.rpc.setRelayUrl(iframeId, this.serverBase_ + this.rpcRelay);
  gadgets.rpc.setAuthToken(iframeId, this.rpcToken);
};

gadgets.SocialSiteIfrGadget.prototype.getTitleBarContent = function(continuation) {
  if (this.includeChrome == false) {
    continuation('');
    return;
  }
  var content = '';
  content += '<div id="' + this.cssClassTitleBar + '-' + this.id +
      '" class="' + this.cssClassTitleBar + '">' +
      '<span class="' + this.cssClassTitleButtonBar + '">';
  content += '<a href="#" onclick="gadgets.container.getGadget(' + this.id +
      ').handleOpenUserPrefsDialog();return false;" class="' + this.cssClassTitleButton +
      '"><img style="border: 0;" src="'+this.resourceBase_+'../local_gadgets/files/header-props.png"' +
      ' alt="edit gadget settings" title="Edit Gadget Settings"></a>';
  content += ' <a href="#" onclick="gadgets.container.getGadget(' +
      this.id + ').handleToggle();return false;" class="' + this.cssClassTitleButton +
      '"><img style="border: 0;" src="'+this.resourceBase_+'../local_gadgets/files/header-min.png"' +
      ' alt="toggle gadget display" title="Toggle Gadget Display"></a>';
  if (this.removable != false) {
      content += ' <a href="#" onclick="gadgets.container.getGadget(' + this.id +
          ').handleRemove();return false;" class="' + this.cssClassTitleButton +
          '"><img style="border: 0;" src="'+this.resourceBase_+'../local_gadgets/files/header-close.png"' +
          ' alt="remove this gadget" title="Remove This Gadget"></a>';
  }
  content += '</span>' +
      '<span id="' + this.getIframeId() + '_title" class="' + this.cssClassTitle + '">' +
      (this.title ? this.title : 'Title') + '</span>' +
      '</div>';
  continuation(content);
};

gadgets.SocialSiteIfrGadget.prototype.handleRemove = function() {
  var elementId = gadgets.container.containerHelper.getIframeId();
  var params = {
    'moduleId': this.moduleId,
    'token': this.secureToken
  };
  gadgets.rpc.call(elementId, "socialsite_removeGadget", null, params);
};

gadgets.IfrGadget.prototype.setResourceBase = function(url) {
  this.resourceBase_ = url;
};

gadgets.IfrGadget.prototype.getResourceBase = function() {
  return this.resourceBase_;
};


/*
 * ------------------------------------------------------------
 * Define our own customized version of gadgets.GadgetService
 * ------------------------------------------------------------
 */

gadgets.SocialSiteIfrGadgetService = function() {

  gadgets.IfrGadgetService.call(this);

  gadgets.rpc.register('socialsite_reloadPage', function(params) {
    window.location.reload();
  });

  gadgets.rpc.register('socialsite_showLightbox', function(params) {
    var title = params[0];
    var url = params[1];
    var width = ((params[2] != null) ? params[2] : 600);
    var height = ((params[3] != null) ? params[3] : 500);
    parent.socialsite.openLightbox(title, url, width, height);
  });

  // If params[0] == true, reloads the parent.  This is useful when the
  // lightbox results in config changes that will impact their "parent".
  gadgets.rpc.register('socialsite_closeLightbox', function(params) {
    parent.socialsite.closeLightbox();
    var reload = ((params[0] != null) ? params[0] : true);
    if (reload) {
        window.location.reload();
    }
  });

}

/**
 * Sets one or more user preferences
 * @param {String} editToken
 * @param {String} name Name of user preference
 * @param {String} value Value of user preference
 * More names and values may follow
 */
gadgets.IfrGadgetService.prototype.setUserPref = function(editToken, name,
    value) {
  var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
  var gadget = gadgets.container.getGadget(id);
  var prefs = gadget.getUserPrefs();
  for (var i = 1, j = arguments.length; i < j; i += 2) {
    prefs[arguments[i]] = arguments[i + 1];
  }
  gadget.setUserPrefs(prefs);
};

/**
 * Navigates the page to a new url based on a gadgets requested view and
 * parameters.
 */
gadgets.IfrGadgetService.prototype.requestNavigateTo = function(view,
    opt_params) {

  var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
  var url = document.getElementById(this.f).src;

  var prevView = getUrlParam("view", url);
  if (prevView) {
    var url = url.replace("view=" + prevView, "view=" + view);
  } else {
    url += '&view=' + encodeURIComponent(view);
  }

  if (opt_params) {
    var paramStr = JSON.stringify(opt_params);
    if (paramStr.length > 0) {
      url += '&appParams=' + encodeURIComponent(paramStr);
    }
  }

  var title = document.getElementById(this.f + '_title').firstChild.data;
  parent.socialsite.openLightbox(title, url);

};

/**
 * Simple utility function to get a URL param
 */
function getUrlParam(name, url) {
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(url);
    if (results == null) {
        return "";
    } else {
        return results[1];
    }
};



gadgets.SocialSiteIfrGadgetService.inherits(gadgets.IfrGadgetService);


/*
 * ------------------------------------------------------------
 * Define our own customized version of gadgets.UserPrefStore
 * ------------------------------------------------------------
 */

gadgets.SocialSiteUserPrefStore = function() {
  gadgets.UserPrefStore.call(this);
  this.prefsByGadget = {};
}

gadgets.SocialSiteUserPrefStore.inherits(gadgets.UserPrefStore);

gadgets.SocialSiteUserPrefStore.prototype.getPrefs = function(gadget) {
  return this.prefsByGadget[gadget];
}

gadgets.SocialSiteUserPrefStore.prototype.savePrefs = function(gadget) {
  this.prefsByGadget[gadget] = gadget.getUserPrefs();
}


/*
 * ------------------------------------------------------------
 * And finally, put our customized versions of Gadget, 
 * GadgetService, and UserPrefStore into action.
 * ------------------------------------------------------------
 */

delete gadgets.container.gadgetClass;
gadgets.container.gadgetClass = gadgets.SocialSiteIfrGadget;

delete gadgets.container.gadgetService;
gadgets.container.gadgetService = new gadgets.SocialSiteIfrGadgetService();

delete gadgets.container.userPrefStore;
gadgets.container.userPrefStore = new gadgets.SocialSiteUserPrefStore();

gadgets.container.addGadget = function(gadget) {
  // gadget.id = this.getNextGadgetInstanceId();
  gadget.setUserPrefs(this.userPrefStore.getPrefs(gadget));
  this.gadgets_[this.getGadgetKey_(gadget.id)] = gadget;
}
