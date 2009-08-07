gadgetizer_LayoutManager = function() {
    gadgets.LayoutManager.call(this);
};

gadgetizer_LayoutManager.inherits(gadgets.LayoutManager);

gadgetizer_LayoutManager.prototype.getGadgetChrome = function(gadget) {
    var chromeId = 'gadget-chrome-' + gadget.moduleId;
    var chromeElement = null;
    if (chromeId != null) {
        chromeElement = document.getElementById(chromeId);
        // Assume that we want a wrapper only when there is a title bar
        if (gadget.includeChrome != false) {
            chromeElement.className = 'gadgets-gadget-chrome';
            if (gadget.height != null) {
                chromeElement.style.height = gadget.height+60+'px';
            }
        }
    }
    return chromeElement;
};

gadgets.container.layoutManager = new gadgetizer_LayoutManager();
